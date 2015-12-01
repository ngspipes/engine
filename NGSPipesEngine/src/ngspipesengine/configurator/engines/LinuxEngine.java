package ngspipesengine.configurator.engines;

import ngspipesengine.configurator.properties.LinuxProperties;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.Uris;
import ngspipesengine.utils.Utils;

public class LinuxEngine extends Engine {

	private static final String rootCommand = "sudo";
	private static final String TAG = "LinuxEngine";
	
	// GET LINUX TYPE cat /etc/issue
	
	
	public LinuxEngine(LinuxProperties props) throws EngineException {
		super(props);
	}

	
	public String getRootCommand() { return rootCommand; }

	@Override
	public void finish() throws EngineException {
		props.getLog().debug(TAG, "Finshing");
		System.out.println("Finishing engine");
		props.getLog().debug(TAG, "Finshing success");
		props.stopLog();
	}

	@Override
	public void clean() throws EngineException {}

	@Override
	protected void cloneEngine() throws EngineException {}

	@Override
	protected void internalStart() throws EngineException {
		props.getLog().debug(TAG, "Starting to run");

		Utils.executeCommand(getExecuteScriptCommand(), props.getLog(), TAG, "Error trying running script");

		props.getLog().debug(TAG, "Running success");
	}

	@Override
	protected String getRunnerCommand() throws EngineException {
		return getRunCommand(Uris.PIPELINES_FOLDER_PATH + props.getPipelineFolder(),
				Uris.ENGINE_PATH + Uris.SEP, props.getCompiler().getName(), 
				props.getCompiler().getMainArguments(props.getSpecificsArguments()));
	}


	
	private String getExecuteScriptCommand() {
		return rootCommand + " " + String.format(Utils.EXECUTE_SCRIPT_COMMAND, getScriptPath());
	}


	private String getScriptPath() {
		return Uris.PIPELINES_FOLDER_PATH + props.getPipelineFolder() + "execute.sh";
	}


	@Override
	public void stop() throws EngineException {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void recoverState() throws EngineException {
		// TODO Auto-generated method stub
		
	}
		
}
