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
package ngspipesengine.core.configurator.engines;

import ngspipesengine.core.configurator.properties.VMProperties;
import ngspipesengine.core.configurator.scripts.Script;
import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.core.utils.Log;
import ngspipesengine.core.utils.Uris;
import ngspipesengine.core.utils.Utils;
import ngspipesengine.presentation.utils.WorkQueue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class VMEngine extends Engine {

	private static final String TAG = "VMEngine";
	private static final String VBOX_MINIMAL_VERSION = "4.3.2";
	private static final int VERSION = 432;

	private static String getDeleteVMCommand(String engineName) {
		return String.format(Utils.DELETE_VM_COMMAND, engineName);
	}

	private static String getRegisterVMCommand() throws EngineException {
		return Utils.REGISTER_VM_COMMAND + Engine.OS_URL_FORMATTERS.get(Utils.OS_TYPE).apply(Uris.getVboxFilePath());
	}

	private static boolean compareVersions(String currVersion) {
		String[] numbers = currVersion.split("\\.");
		int version = 0;
		int multiplicand = 100;
		for (String number : numbers) {
			version += multiplicand * Integer.parseInt(number);
			multiplicand /= 10;
		}

		return version >= VERSION;
	}

	private static boolean isRegistered(Log log) throws EngineException {
		try {
			List<String> vms = getVMsName();

			for(String vm : vms)
				if(vm.equals(VMProperties.BASE_VM_NAME))
					return true;

		} catch (IOException e) {
			Utils.treatException(log, TAG, e, "verifying if engine is registered");
		}
		return false;
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
		if(!isRegistered(log)) {

			log.debug(TAG, "Register beginning");
			Utils.executeCommand(getRegisterVMCommand(), log, TAG, "Error registering engine");
			System.out.println("Register is complete");
			log.debug(TAG, "Register ended");
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
		props.getLog().debug(TAG, "Finishing");
		System.out.println("Finishing engine");
		props.unset();
		unregisterEngine();
		props.getLog().debug(TAG, "Finishing success");
		props.stopLog();
	}

	@Override
	public void stop() throws EngineException {

		super.stop();
		props.getLog().debug(TAG, "Stopping execution");
		System.out.println("Stopping engine execution");

		if(isVMRunning()) {
			Utils.executeCommand(getPowerOffVMCommand(), props.getLog(), TAG, "Stopping engine execution");
			props.unset();
		}
		unregisterEngine();
		props.getLog().debug(TAG, "Execution stopped");
	}


	@Override
	protected void cloneEngine() throws EngineException {

		if(shouldClone()) {
			props.getLog().debug(TAG, "Cloning");
			System.out.println("Cloning engine");
			executeClone();
			props.getLog().debug(TAG, "Cloning success");
		}
	}

	@Override
	protected void internalStart() throws EngineException {
		props.getLog().debug(TAG, "Starting to run");

		try {
			System.out.println("Booting engine and installing necessary packages");
			Utils.executeCommand(getStartVMCommand(), props.getLog(), TAG,
					"Error trying running virtual machine");
		} catch (EngineException e) {
			props.getLog().error(TAG, Utils.getStackTrace(e));
		}
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
	protected void registerEngine() throws EngineException {
		synchronized (lockObject) {
			System.out.println("Registering engine");
			props.getLog().debug(TAG, "Registering engine");
			if(	WORKING_ENGINES.contains(props.getEngineName()) ||
					props.getEngineName().equals(props.getBaseEngineName()))
				props.setEngineName(getCloneName());

			WORKING_ENGINES.add(props.getEngineName());
			System.out.println("Engine registered");
			props.getLog().debug(TAG, "Engine registered");
		}
	}

	@Override
	protected void unregisterEngine() throws EngineException {
		synchronized (lockObject) {
			WORKING_ENGINES.remove(props.getEngineName());
			System.out.println("Engine unregister");
			props.getLog().debug(TAG, "Engine unregister");
		}
	}


	private void executeClone() throws EngineException {
		final CountDownLatch cd = new CountDownLatch(1);

		WorkQueue.run(()->{
			try{
				Utils.executeCommand(getCloneVMCommand(), props.getLog(), TAG,
						"Error cloning virtual machine " + props.getBaseEngineName());
			} catch(Exception e) {
				props.getLog().error(TAG, "Error while cloning engine" + e.getMessage());
			} finally {
				cd.countDown();
			}
		});

		while(cd.getCount() != 0)
			Utils.wait(5000, () -> System.out.println("...... Cloning executor image"));
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

		if(WORKING_ENGINES.size() == 0)
			return props.getBaseEngineName() + "0";

		return props.getBaseEngineName() + getNextCloneNumber();
	}

	private String getNextCloneNumber() {
		WORKING_ENGINES.sort(this::engineNameComparator);

		String biggestVmNameNumber = WORKING_ENGINES.get(WORKING_ENGINES.size() - 1);

		int biggestVmNumber = Integer.parseInt(biggestVmNameNumber.substring(biggestVmNameNumber.length() - 1));

		if(biggestVmNumber >= WORKING_ENGINES.size() - 1)
			biggestVmNumber = searchFreeVm(biggestVmNumber);

		return (biggestVmNumber + 1) + "";
	}

	private int searchFreeVm(int biggestVmNumber) {
		int [] freeEngines = new int[WORKING_ENGINES.size() - 1];

		for (int idx = 0; idx < WORKING_ENGINES.size() - 1; idx++)
			freeEngines[idx] = (engineNumberComparator(WORKING_ENGINES.get(idx), idx) == 0) ? 0 : 1;

		for (int idx = freeEngines.length - 1; idx >= 0 ; idx++) {
			if(freeEngines[idx] == 1)
				return freeEngines[idx];
		}

		return biggestVmNumber;
	}

	private int engineNumberComparator(String eName, int number) {
		int e1 = Integer.parseInt(eName.substring(eName.length() - 2));
		return number - e1;
	}

	private int engineNameComparator(String eName1, String eName2) {
		int e1 = Integer.parseInt(eName1.substring(eName1.length() - 2));
		int e2 = Integer.parseInt(eName2.substring(eName2.length() - 2));
		return e1 - e2;
	}

	private boolean shouldClone() throws EngineException {
		try {
			List<String> vms = getVMsName();
			return !vms.contains(props.getEngineName());
		} catch (IOException e) {
			Utils.treatException(props.getLog(), TAG, e, "Error checking if engine is running");
		}
		return false;
	}

	private boolean isVMRunning() throws EngineException {
		String runningVMs = "";
		try {
			runningVMs = (String)Utils.run(	Utils.LIST_RUNNING_VMS_COMMAND,
					props.getLog(), Utils::readStream);
		} catch (IOException e) {
			Utils.treatException(props.getLog(), TAG, e, "Error checking if engine is running");
		}

		return runningVMs.contains(props.getEngineName());
	}

	private void waitUntilRunning() throws EngineException {
		boolean running = true;
		while(running)
			try {
				running = isVMRunning();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Utils.treatException(props.getLog(), TAG, e, "Error checking if engine is running");
			}
	}
}