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
package ngspipesengine.configurator.properties;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ngspipesengine.configurator.engines.Engine;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.Pair;
import ngspipesengine.utils.Uris;
import ngspipesengine.utils.Utils;
import dsl.entities.Pipeline;

public class VMProperties extends Properties {

	private static final String TAG = "VMProperties";
	private static final List<Pair<String, String>> SHARED_FOLDERS = new LinkedList<Pair<String,String>>();
	public static final String BASE_VM_NAME = "NGSPipesEngineExecutor";
	public static final String BASE_CLASS_NAME = "PipeBaseClass.txt";

	static {
		SHARED_FOLDERS.add(new Pair<String, String>(Uris.ENGINE_FOLDER_NAME, Uris.ENGINE_PATH));
	}
	
	private static String getTagContentOf(String source, String tag) {
		int beginIdx = source.indexOf(tag) + tag.length();
		int endIdx = source.indexOf("\n", beginIdx);
		
		return source.substring(beginIdx, endIdx).trim();
	}
	
	private static String getServerIP() throws EngineException {
		
		try {
			String runResults = Utils.run(Utils.VBOX_GET_HOST_ONLY_COMMAND);
			
			if(runResults == null || runResults.isEmpty())
				throw new EngineException("Error getting host-only adapter configuration");
			
			
			return getTagContentOf(runResults, "IPAddress:");
		} catch (IOException ex) {
			throw new EngineException("Getting VirtualBox host-only adapter", ex);
		}
	}
	
	private static void createHostOnlyAdapter(String vmName) throws EngineException {

		try {
			
			String runResults = Utils.run(Utils.VBOX_GET_HOST_ONLY_COMMAND);
			
			if(runResults == null || runResults.isEmpty())
				createHostOnlyNetwork();

			runResults = Utils.run(Utils.VBOX_GET_HOST_ONLY_COMMAND);

			if(runResults == null || runResults.isEmpty())
				throw new EngineException("Error creating and configurating host-only adapter");
				
			String vboxHostonlyAdapterName = Engine.OS_QUOTATION_MARKS_FORMATTERS.get(Utils.OS_TYPE)
														.apply(getTagContentOf(runResults, "Name:"));
			
			if(!existHostOnlyAdapter(vmName))
				createAndConfigHostOnlyAdapter(vmName, vboxHostonlyAdapterName);
			
			String dhcpServerCommand = String.format(Utils.VBOX_DHCP_SERVER_MODIFY_COMMAND, vboxHostonlyAdapterName);
			Utils.run(dhcpServerCommand);
			
		} catch(IOException | InterruptedException e) {
			throw new EngineException("Error creating and configurating host-only adapter", e);
		}
	}
	
	private static void createAndConfigHostOnlyAdapter(String vmName, String adapterName) throws IOException, InterruptedException {
		
		
		String createCommand = String.format(Utils.VBOX_CREAT_HOST_ONLY_ADAPTER_COMMAND, vmName);
		Utils.run(createCommand);
		
		
		String configCommand = String.format(Utils.VBOX_CONFIG_HOST_ONLY_ADAPTER_COMMAND, vmName, adapterName);
		Utils.run(configCommand);
		
	}

	private static boolean existHostOnlyAdapter(String vmName) throws IOException, InterruptedException {
		return Utils.run(Utils.VBOX_SHOWINFO_COMMAND + vmName).contains("Host-only Interface");
	}

	private static void createHostOnlyNetwork() throws EngineException {
		try {
			Utils.run(Utils.VBOX_CREATE_HOST_ONLY_COMMAND);
		} catch (IOException e) {
			throw new EngineException("Creating host-only network", e);
		}
	}

	private static String getAddSharedFolderCommand(String engineName, Pair<String, String> sharedFolder) {
		StringBuilder sharedFolderCommand = new StringBuilder();

		sharedFolderCommand	.append(getSharedFolderCommand(Utils.SHARE_FODLER_ADD_COMMAND, engineName, sharedFolder.getKey()))
							.append(Utils.HOST_PATH_OPTION)
							.append(Engine.OS_PATH_FORMATTERS.get(Utils.OS_TYPE).apply(sharedFolder.getValue()));

		return sharedFolderCommand.toString();
	}

	private static String getSharedFolderCommand(String command, String engineName, String sharedFolderName) {
		StringBuilder sharedFolderCommand = new StringBuilder(command);
		sharedFolderCommand .append(engineName)
							.append(String.format(Utils.NAME_OPTION, sharedFolderName));
		
		return sharedFolderCommand.toString();
	}

	private static int getRequiredMemory(Pipeline pipe, int from, int to) {
		if(to == -1 && from == -1)
			return pipe.getRequiredMemory();
		return (to == -1) ? pipe.getRequiredMemory(from) : pipe.getRequiredMemory(from, to);
	}

	private static String getEngineName(String engineName) {
		return engineName.length() > 1 ? engineName : BASE_VM_NAME;
	}
	

	int memory;
	public void setMemory(int memory) { this.memory = memory; }
	public int getMemory() { return this.memory; }
	
	int processorsNumber = 2;
	public void setProcessorsNumber(int processorValue) {
		if(processorValue != -1)
			processorsNumber = processorValue;
	}
	public int getProcessorsNumber() { return this.processorsNumber; }
	
	
	private VMProperties(String baseEngineName, String engineName, String pipelinePath, 
							int from, int to) throws EngineException {
		super(baseEngineName, getEngineName(engineName), pipelinePath, from, to);
	}

	public VMProperties(String engineName, String pipelinePath, int from, int to) throws EngineException {
		this(BASE_VM_NAME, engineName, pipelinePath, from, to);
	}

	public VMProperties(String engineName, String pipelinePath, int from) throws EngineException {
		this(engineName, pipelinePath, from, -1);
	}

	public VMProperties(String vmName, String pipelinePath) throws EngineException {
		this(vmName, pipelinePath, -1, -1);
	}
	
	public VMProperties(String pipelinePath) throws EngineException {
		super(BASE_VM_NAME, pipelinePath, -1, -1);
	}

	@Override
	public void set(String... repositoryUri) throws EngineException {

		log.debug(TAG, "Begin set");

		setRequiredMemory(); 
		setProcessorsNumber();
		completeSharedFolders();
		setSharedFolders(this.engineName);
		createHostOnlyAdapter(engineName);

		log.debug(TAG, "End set");
	}

	@Override
	public void unset(String... args) throws EngineException {

		log.debug(TAG, "Begin unset");
		cleanSharedFolders(this.engineName);
		log.debug(TAG, "End unset");
	}
	
	public String getSpecificsArguments() throws EngineException {
		return " " + this.port + " " + getServerIP();
	}
	

	
	@Override
	protected void internalUpdate() {
		memory = getRequiredMemory(pipe, getFrom(), getTo());
	}
	
	@Override
	protected String getBaseClassName() throws EngineException {
		return BASE_CLASS_NAME;
	}	
	
	
	private void setProcessorsNumber() throws EngineException {
		log.debug(TAG, "Setting processors number");
		Utils.executeCommand(getProcessorsNumberVMCommand(), log, TAG, "setting processors number");
		log.debug(TAG, "Finish setting processors number");
	}

	private String getProcessorsNumberVMCommand() {
		return String.format(Utils.SET_PROCESSORS_NUMBER_VM_COMMAND, engineName, processorsNumber);
	}
	
	private void cleanSharedFolders(String engineName) throws EngineException {
		log.debug(TAG, "Unsetting shared folders properties"); 

		for (Pair<String, String> sharedFolder : SHARED_FOLDERS)
			Utils.executeCommand(getSharedFolderCommand(Utils.SHARE_FODLER_REMOVE_COMMAND, engineName, 
								sharedFolder.getKey()), log, 
								TAG, "Error cleaning shared folders properties");
	}

	private void setSharedFolders(String engineName) throws EngineException {

		log.debug(TAG, "Setting shared folders properties"); 

		for (Pair<String, String> sharedFolder : SHARED_FOLDERS)
			Utils.executeCommand(getAddSharedFolderCommand(engineName, sharedFolder), log, 
								TAG, "Error setting shared folders properties");
	}

	private void completeSharedFolders() {
		SHARED_FOLDERS.add(new Pair<String, String>("Pipeline", 
				Uris.getActualPipelinePath(getPipelineFolder())));

		SHARED_FOLDERS.add(new Pair<String, String>(Uris.INPUTS_FOLDER_NAME, getInputsPath()));

		if(compilerProps.getRepositoryType().equals("Local"))
		SHARED_FOLDERS.add(new Pair<String,String>(Uris.REPOSITORY_FOLDER_NAME, this.compilerProps.getRepositoryUri()));

		SHARED_FOLDERS.add(new Pair<String, String>(Uris.OUTPUTS_FOLDER_NAME, getOutputsPath()));
	}

	private void setRequiredMemory() throws EngineException {  

		log.debug(TAG, "Setting require memory"); 
		Utils.executeCommand(getMemoryCommand(), log, TAG, "Error setting require memory");

	}

	private String getMemoryCommand() {

		StringBuilder memoryCommand = new StringBuilder(Utils.MODIFY_VM);
		memoryCommand   .append(this.engineName)
						.append(Utils.MEMORY_OPTION)
						.append(memory);
		return memoryCommand.toString();
	}

}
