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
package ngspipesengine.compiler;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ngspipesengine.configurator.engines.Engine;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.IO;
import ngspipesengine.utils.Uris;
import ngspipesengine.utils.Utils;

public abstract class PipelineCompiler implements ICompiler {


	private static final String TAG = "PipelineExecutableCreator";
	
	private static final Map<String, Supplier<String>> REPOSITORY_URI_SUPPLIER = new HashMap<>(); 

	private static void initRepositoryUriProvider(CompilerProperties props) {
		REPOSITORY_URI_SUPPLIER.put("Local", () -> Uris.VM_REPOSITORY_FOLDER_PATH);
		REPOSITORY_URI_SUPPLIER.put("Remote", () -> props.getRepositoryUri());
		REPOSITORY_URI_SUPPLIER.put("Github", () -> props.getRepositoryUri());
	}
	
	private static String getCompilerCommand(String classPath, String filePath, String name) {
		String cp = Engine.OS_PATH_FORMATTERS.get(Utils.OS_TYPE).apply(
				classPath + Uris.DSL_JAR_NAME + File.pathSeparator + classPath + Uris.REPOSITORY_JAR_NAME);
		System.err.println("Using classpath " + cp);
		String compilerCommand = "javac -cp " + cp
									+ " "
									+ Engine.OS_PATH_FORMATTERS.get(Utils.OS_TYPE).apply(filePath + name)
									+ ".java";
		return compilerCommand;
	}




	protected final CompilerProperties props;

	public PipelineCompiler(CompilerProperties props) {
		this.props = props;
		initRepositoryUriProvider(props);
	}

	@Override
	public void compile() throws EngineException {
		props.getLog().debug(TAG, "Begin create");
		StringBuilder pipelinePath = new StringBuilder(Uris.PIPELINES_FOLDER_PATH + props.getPipelineFolder());
		pipelinePath.append(getName())
		.append(".java");

		try {
			String pipelineClassContent = getExecutableJavaClass();
			IO.writeToFile(pipelinePath.toString(), pipelineClassContent);
			compileClass(Uris.PIPELINES_FOLDER_PATH + props.getPipelineFolder());

		} catch (MalformedURLException e) {
			Utils.treatException(props.getLog(), TAG, e, "Error creating executable pipeline class");
		}

		props.getLog().debug(TAG, "End create");
	}
	
	public String getMainArguments(String specificsArgs) throws EngineException {

		String repoUri = REPOSITORY_URI_SUPPLIER.get(props.getRepositoryType()).get();
		StringBuilder mainArgs = new StringBuilder(repoUri);
		
		mainArgs.append(specificsArgs);
		
		if(props.getFrom() == -1 && props.getTo() == -1)
			return mainArgs.toString();

		if(props.getTo() == -1)
			mainArgs.append(" ").append(props.getFrom());
		else
			mainArgs.append(" ").append(props.getFrom()).append(" ")
					.append(props.getTo()).append(" ");
		
		return mainArgs.toString();
	}	
	
	public String getName() {
		return "PipelineExecutable";
	}

	
	
	protected abstract String getExecutableJavaClass() throws MalformedURLException, EngineException;

	protected void compileClass(String filePath) throws EngineException {
		
		String command = getCompilerCommand(filePath);
		Utils.executeCommand(command, props.getLog(), TAG, "Pipeline is malformed");
	}
	
	
	
	private String getCompilerCommand(String filePath) {
		return getCompilerCommand(Uris.ENGINE_PATH + Uris.SEP, filePath, getName());
	}
	
}
