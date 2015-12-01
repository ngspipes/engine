package ngspipesengine.configurator.engines;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ngspipesengine.configurator.properties.VMProperties;
import ngspipesengine.configurator.scripts.Script;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.Log;
import ngspipesengine.utils.Uris;
import ngspipesengine.utils.Utils;

public class VMEngine extends Engine {

	private static final String TAG = "VMEngine";
	private static final String VBOX_MINIMAL_VERSION = "4.3.2";
	private static final int VERSION = 432;

	private static String getDeleteVMCommand(String engineName) {
		return String.format(Utils.DELETE_VM_COMMAND, engineName);
	}
	
	private static String getRegisterVMCommand() {
		return Utils.REGISTER_VM_COMMAND + Engine.OS_URL_FORMATTERS.get(Utils.OS_TYPE).apply(Uris.getVboxFilePath());
	}
	
	private static boolean compareVersions(String currVersion) {		
		String[] numbers = currVersion.split("\\.");
		int version = 0;
		int multiplicator = 100;
		for(int idx = 0; idx < numbers.length; ++idx) {
				version += multiplicator * Integer.parseInt(numbers[idx]);
				multiplicator /= 10;
		}
		
		return version >= VERSION;
	}
	
	private static boolean isRegisted(Log log) throws EngineException {
		try {
			List<String> vms = getVMsName();

			for(String vm : vms)
				if(vm.equals(VMProperties.BASE_VM_NAME))
					return true;
			
		} catch (IOException e) {
			Utils.treatException(log, TAG, e, "verifying if engine is registed");
		}
		return false;
	}

	private static String getNextCloneNumber(List<String> vmsNames) {
		
		List<Integer> numbers = new LinkedList<>();
		
		for(String vm : vmsNames) {			
			if(!vm.equals(VMProperties.BASE_VM_NAME)) { 
				int idxNumber = VMProperties.BASE_VM_NAME.length();
				numbers.add(Integer.parseInt(vm.substring(idxNumber)));
			}
		}
		
		if(numbers.isEmpty()) return "" + 0;
		
		Collections.sort(numbers);
		
		return "" + (numbers.get(numbers.size() -1) + 1);
	}
	
	public static void clean(String engineName) throws EngineException {
		Log clean = new Log("clean");
		System.out.println("Cleaning engine");
		Utils.executeCommand(getDeleteVMCommand(engineName), clean, TAG, 
				"Error deleting virtual machine " + engineName);
		clean.stop();
	}
	
	public static boolean acceptedVBoxVersion() throws EngineException {
		try {
			String result = Utils.run(Utils.VBOX_VERSION_COMMAND);
			if(result.startsWith(VBOX_MINIMAL_VERSION))
				return true;
			return compareVersions(result.substring(0, VBOX_MINIMAL_VERSION.length()));
		} catch (IOException e) {
			throw new EngineException("Error getting VirtualBox version", e);
		}
	}
	
	public static void register() throws EngineException {
		Log log = new Log("register");
		if(!isRegisted(log)) {
			log.debug(TAG, "Resgister beginning");	
			Utils.executeCommand(getRegisterVMCommand(), log, TAG, "Error registering engine");
			System.out.println("Register is complete");
			log.debug(TAG, "Resgister ended");
		}
		log.stop();
	}

	public static List<String> getVMsName() throws IOException {
		List<String> vmsName = new LinkedList<>();
		String runResults = Utils.run(Utils.LIST_VMS_COMMAND);		
		
		if(runResults == null || runResults.isEmpty())
			return vmsName;
		
		String[] vms = runResults.split("\n");

		for(String vm : vms)
			if(vm.startsWith("\"" + VMProperties.BASE_VM_NAME))
				vmsName.add(vm.substring(1, vm.indexOf("\"", 1)));

		return vmsName;
	}
	
	

	
	public VMEngine(VMProperties properties) throws EngineException {
		super(properties);
	}
	
	@Override
	public void clean() throws EngineException {
		clean(props.getEngineName());
	}

	@Override
	public void finish() throws EngineException {
		waitUntilRunning();
		props.getLog().debug(TAG, "Finshing");
		System.out.println("Finishing engine");
		props.unset();
		props.getLog().debug(TAG, "Finshing success");
		props.stopLog();
	}

	@Override
	public void stop() throws EngineException {
		props.getLog().debug(TAG, "Stopping execution");
		System.out.println("Stopping engine execution");
		Utils.executeCommand(getPowerOffVMCommand(), props.getLog(), TAG, "Stopping engine execution");
		props.unset();
		props.getLog().debug(TAG, "Execution stoped");
	}
	
	
	@Override
	protected void cloneEngine() throws EngineException {

		if(shouldClone()) {
			props.getLog().debug(TAG, "Cloning");
			
			System.out.println("Getting clone engine");
			props.setEngineName(getCloneName());
			System.out.println("Clonning engine");
			executeClone();

			props.getLog().debug(TAG, "Cloning success");
		}
	}
	
	@Override
	protected void internalStart() throws EngineException { 
		props.getLog().debug(TAG, "Starting to run");		
		Utils.executeCommand(getStartVMCommand(), props.getLog(), TAG, 
							"Error trying running virtual machine");
		System.out.println("Booting engine and installing necessary packages");
		props.getLog().debug(TAG, "Running success");
	}

	@Override
	protected void createScripts() throws EngineException {
		props.getLog().debug(TAG, "Create script");
		Script.setPath(Uris.getActualPipelinePath(props.getPipelineFolder()));
		Script.createExecute(props.getSetups(), getRunnerCommand());
		props.getLog().debug(TAG, "Create script success");
	}
	
	@Override
	protected String getRunnerCommand() throws EngineException {
		return getRunCommand(Uris.VM_PIPELINE_PATH,
				Uris.VM_ENGINE_PATH, props.getCompiler().getName(), 
				props.getCompiler().getMainArguments(props.getSpecificsArguments()));
	}

	@Override
	protected void recoverState() throws EngineException {
		
	}


	private void executeClone() throws EngineException {
		final CountDownLatch cd = new CountDownLatch(1);
		Thread thread = new Thread(()->{
			try{

				Utils.executeCommand(getCloneVMCommand(), props.getLog(), TAG, 
						"Error cloning virtual machine " + props.getBaseEngineName());
				cd.countDown();
			} catch(Exception e) {
				props.getLog().error(TAG, "Error while clonning engine" + e.getMessage());
			}
		});
		
		thread.setDaemon(false);
		thread.start();

		while(cd.getCount() != 0)
			Utils.wait(5000, () -> System.out.println("...... Clonning engine"));
	}
	
	private String getStartVMCommand() {
		return Utils.START_VM_COMMAND + props.getEngineName();
	}
	
	private String getPowerOffVMCommand() {
		return String.format(Utils.POWER_OFF_VM_COMMAND, props.getEngineName());
	}
	
	private String getCloneVMCommand() {
		return String.format(Utils.VM_CLONE_COMMAND, props.getBaseEngineName(), props.getEngineName());
	}
	
	private String getCloneName() throws EngineException {
		String vmCloneNumber;
		try {
			List<String> vmsName = getVMsName();
			vmCloneNumber = getNextCloneNumber(vmsName);
			return props.getBaseEngineName() + vmCloneNumber;
		} catch (IOException e) {
			Utils.treatException(props.getLog(), TAG, e, "Error getting clone engine name");
		}
				
		return null;
	}	
	
	private boolean shouldClone() throws EngineException {
			try {
				return areVMsRunning(Utils.run(Utils.LIST_RUNNING_VMS_COMMAND, 
												props.getLog(), Utils::readStream));
			} catch (IOException e) {
				Utils.treatException(props.getLog(), TAG, e, "Error checking if engine is running");
			}
			
			return false;
	}

	private boolean areVMsRunning(Object result) throws IOException {
		
		String runningVMs = (String)result;
		if(!runningVMs.contains(props.getEngineName()) 
				&& !props.getBaseEngineName().equals(props.getEngineName()))
			return false;
		
		List<String> vms = getVMsName();
		for(String vm : vms) {
			if(!runningVMs.contains(vm) 
					&& !props.getBaseEngineName().equals(vm)) {
				props.setEngineName(vm);
				return false;
			}
		}
		
		return true;
	}

	private void waitUntilRunning() throws EngineException {
		boolean running = true;
		while(running)
			try {
				running = areVMsRunning(Utils.run(Utils.LIST_RUNNING_VMS_COMMAND, 
											props.getLog(), Utils::readStream));
				Thread.sleep(1000);
			} catch (InterruptedException | IOException e) {
				Utils.treatException(props.getLog(), TAG, e, "Error checking if engine is running");
			}
	}
}
