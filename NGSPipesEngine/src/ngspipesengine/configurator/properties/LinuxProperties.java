package ngspipesengine.configurator.properties;

import ngspipesengine.exceptions.EngineException;

public class LinuxProperties extends Properties {
	
	public static final String BASE_CLASS_NAME = "PipeBaseClassLinux.txt";
	
	

	protected LinuxProperties(String baseEngineName, String engineName,
			String pipelinePath, int from, int to) throws EngineException {
		super(baseEngineName, engineName, pipelinePath, from, to);
	}

	public LinuxProperties(String pipelinePath, int from, int to) throws EngineException {
		this("", "", pipelinePath, from, to);
	}

	public LinuxProperties(String pipelinePath, int from) throws EngineException {
		this(pipelinePath, from, -1);
	}

	public LinuxProperties(String pipelinePath) throws EngineException {
		this(pipelinePath, -1, -1);
	}
	
	@Override
	protected String getBaseClassName() throws EngineException {
		return BASE_CLASS_NAME;
	}	
	
	@Override
	public void set(String... args) throws EngineException {}

	@Override
	public void unset(String... args) throws EngineException {}

	@Override
	public String getSpecificsArguments() throws EngineException {
		return "";
	}

	@Override
	protected void internalUpdate() {}
	
}
