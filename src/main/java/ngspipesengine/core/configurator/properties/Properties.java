/*-
 * Copyright (c) 2016, NGSPipes Team <ngspipes@gmail.com>
 * All rights reserved.
 *
 * This file is part of NGSPipes <http://ngspipes.github.io/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ngspipesengine.core.configurator.properties;

import configurators.IConfigurator;
import dsl.entities.Pipeline;
import ngspipesengine.core.compiler.*;
import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.core.utils.Log;
import ngspipesengine.core.utils.Uris;
import ngspipesengine.core.utils.Utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class Properties implements IProperties {

	private static final String TAG = "Properties";
	protected static final String METHOD_NAME = "loadPipeline";
	private static final Map<String, Function<CompilerProperties, ICompiler>> EXECUTABLES;

	static {
		EXECUTABLES = new HashMap<>();
		loadExecutables();
	}

	private static void loadExecutables() {
		EXECUTABLES.put("java", JavaCompiler::new);
		EXECUTABLES.put("pipes", PipesCompiler::new);
	}
	
	private static Collection<IConfigurator> getConfigurators(Pipeline pipe, int from, int to) {
		if(to == -1 && from == -1)
			return pipe.getConfigurators();
		return (to == -1) ? pipe.getConfigurators(from) : pipe.getConfigurators(from, to);
	}

	private static Map<String, List<String>> getSetupsFromConfigurator(Collection<IConfigurator> configurators) {
		Map<String, List<String>> map = new HashMap<>();

		configurators.forEach((configurator)->{
			if(!map.containsKey(configurator.getName()))
				map.put(configurator.getName(), configurator.getSetup());
		});

		return map;
	}
	
	
	
	protected String engineName;
	public String getEngineName() {  return engineName; }
	public void setEngineName(String engineName) {  this.engineName = engineName; }
	
	private String baseName;
	public String getBaseEngineName() {  return baseName; }	
	
	String inputsPath;
	@Override
	public String getInputsPath() { return inputsPath; }
	@Override
	public void setInputsPath(String input) { this.inputsPath = input; }
	
	String outputPath;
	@Override
	public String getOutputsPath() { return this.outputPath; }
	@Override
	public void setOutputsPath(String output) { this.outputPath = output; }

	protected final Log log;
	public Log getLog() { return log; }
	
	protected final PipelineCompiler compiler;
	public PipelineCompiler getCompiler() { return this.compiler; }
	
	Map<String, List<String>> setups;	
	public Map<String, List<String>> getSetups() {	return setups; }
	
	protected final CompilerProperties compilerProps;
	
	protected Properties(String baseEngineName, String engineName, String pipelinePath, int from, int to) throws EngineException {
		this(baseEngineName, pipelinePath, from, to);
		this.engineName = engineName;
	}

	protected Properties(String baseEngineName, String pipelinePath, int from, int to) throws EngineException {
		validateSteps(from, to);
		this.baseName = baseEngineName;
		this.compilerProps = new CompilerProperties(pipelinePath, from, to, getBaseClassName());
		this.compiler = initCompiler();
		this.log = new Log();
		initLog();
		load();
	}

	Pipeline pipe;
	
	public String getPipelineName() { 
		return compiler.getName();
	}
	public String getPipelineFolder() {
		return compilerProps.getPipelineFolder();
	}
	
	public void stopLog() {
		log.stop();
	}	
		
	@Override
	public int getFrom() { return compilerProps.getFrom(); }
	@Override
	public void setFrom(int from) { 
		compilerProps.setFrom(from);
		setups = getSetupsFromConfigurator(getConfigurators(pipe, getFrom(), getTo()));	
		internalUpdate();
	}

	@Override
	public int getTo() { return compilerProps.getTo(); }
	@Override
	public void setTo(int to) { 
		compilerProps.setTo(to);
		setups = getSetupsFromConfigurator(getConfigurators(pipe, getFrom(), getTo()));	
		internalUpdate();
	}
	
	int port;
	@Override
	public void setPort(int port) { this.port = port; }
		
	public void loadRequirements() throws EngineException {

		log.debug(TAG, "Loading pipeline requirements");
		Class<?> klass = getPipelineClass();
		try {
			Object obj = klass.newInstance();
			Method method = obj.getClass().getDeclaredMethod(METHOD_NAME, String.class);
			if(method == null)
				Utils.treatException(log, TAG, "Error creating pipeline instance");

			if(method.getReturnType().equals(Pipeline.class)) {
				pipe = (Pipeline) method.invoke(obj, compilerProps.getRepositoryUri());
				setups = getSetupsFromConfigurator(getConfigurators(pipe, compilerProps.getFrom(), compilerProps.getTo()));
				internalUpdate();
			}		
		} catch (InstantiationException | IllegalAccessException |
				SecurityException | NoSuchMethodException | 
				IllegalArgumentException | InvocationTargetException e) {
			Utils.treatException(log, TAG, e, "Error creating pipeline instance:  " + e.getMessage());
		}
	}

	public int getTotalSteps() { return pipe.getSteps().size(); }
	
	public abstract String getSpecificsArguments() throws EngineException;
	
	
	protected abstract void internalUpdate();
	
	protected abstract String getBaseClassName() throws EngineException;
	
	protected Class<?> getPipelineClass() throws EngineException {
		Class<?> klass = null;
		try {			
			File f = new File(Uris.PIPELINES_FOLDER_PATH + compilerProps.getPipelineFolder());
			URL[] cp = {f.toURI().toURL()};
			@SuppressWarnings("resource")
			URLClassLoader urlcl = new URLClassLoader(cp);
			klass = urlcl.loadClass(compiler.getName());
		} catch (ClassNotFoundException | MalformedURLException e) {
			Utils.treatException(log, TAG, e, "Error loading pipeline class");
		}
		return klass;
	}
	
	
	
	private void validateSteps(int from, int to) throws EngineException {
		if(to != -1 && to < from)
			Utils.treatException(log, TAG, "Invalid steps range");
	}

	private void initLog() {
		log.setFileName(getPipelineFolder().substring(0, getPipelineFolder().length() - 1));
		compilerProps.setLog(log);
	}

	private void load() throws EngineException {
		Uris.load(this, TAG);
		compiler.compile();
		System.out.println("Getting engine requirements");
		this.loadRequirements();
	}

	private PipelineCompiler initCompiler() throws EngineException {
		
		String extension = Utils.getFileExtension(compilerProps.getPipelinePath());
		
		if(EXECUTABLES.containsKey(extension))
			return (PipelineCompiler) EXECUTABLES.get(extension).apply(compilerProps);
		
		Utils.treatException(log, TAG, "Error extension ." + extension + " has no executable support");
		return null;
	}
}
