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
package ngspipesengine.core.utils;

import ngspipesengine.core.exceptions.EngineException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


public class Utils {
	
	private static final String LOCKED_VM = "Locked";
	private static final String TAG = "utils";
    private static final String ERROR_TAG =  "Error";
    private static final String INPUT_TAG = "Input";

    public static final Map<String, Integer> VBOX_ERRORS = new HashMap<>();
	public static final String OS_TYPE = getOSType();
    public static final String WIN_OS_TYPE = "Windows";
    public static final String UNIX_OS_TYPE = "Unix";
    private static final String VBOXMANAGE = "VBoxManage";

    ///////// COMMANDS ////////
    
    public static final String MODIFY_VM = VBOXMANAGE + " modifyvm ";
    public static final String EXECUTE_SCRIPT_COMMAND = "sh %1$s";
    public static final String REGISTER_VM_COMMAND = VBOXMANAGE + " registervm "; 
    public static final String START_VM_COMMAND = "VBoxHeadless -s "; 
    public static final String VM_CLONE_COMMAND = VBOXMANAGE + " clonevm %1$s --name %2$s --register";
	public static final String POWER_OFF_VM_COMMAND = VBOXMANAGE + " controlvm %1$s poweroff";
	public static final String SET_PROCESSORS_NUMBER_VM_COMMAND = VBOXMANAGE + " modifyvm %1$s --cpus %2$s";
    public static final String DELETE_VM_COMMAND = VBOXMANAGE + " unregistervm %1$s --delete"; 
    private static final String LIST_VM_COMMAND = VBOXMANAGE + " list ";
    public static final String LIST_RUNNING_VMS_COMMAND = LIST_VM_COMMAND + "runningvms";
    public static final String LIST_VMS_COMMAND = LIST_VM_COMMAND + "vms";
    public static final String VBOX_VERSION_COMMAND = VBOXMANAGE + " --version";
    public static final String VBOX_GET_HOST_ONLY_COMMAND = LIST_VM_COMMAND + "hostonlyifs";
    public static final String VBOX_CREATE_HOST_ONLY_COMMAND = VBOXMANAGE + " hostonlyif create";
    public static final String VBOX_SHOWINFO_COMMAND = VBOXMANAGE + " showvminfo ";
    public static final String VBOX_CREATE_HOST_ONLY_ADAPTER_COMMAND = MODIFY_VM + "%1$s --nic2 hostonly";
    public static final String VBOX_CONFIG_HOST_ONLY_ADAPTER_COMMAND = MODIFY_VM + "%1$s --hostonlyadapter2 %2$s";
    public static final String VBOX_DHCP_SERVER_MODIFY_COMMAND = VBOXMANAGE + " dhcpserver modify --ifname %1$s" + 
																"  --ip 192.168.56.100 --netmask 255.255.255.0 --lowerip" +
																" 192.168.56.101 --upperip 192.168.56.254 --enable";
    
    public static final String MEMORY_OPTION = " --memory ";
    
    private static final String SHARE_FOLDER_COMMAND = VBOXMANAGE + " sharedfolder ";
    public static final String SHARE_FOLDER_ADD_COMMAND = SHARE_FOLDER_COMMAND + "add ";
    public static final String SHARE_FOLDER_REMOVE_COMMAND = SHARE_FOLDER_COMMAND + "remove ";
    public static final String NAME_OPTION = " --name %1$s ";
    public static final String HOST_PATH_OPTION = "--hostpath ";
	    

    static {
    	VBOX_ERRORS.put(LOCKED_VM, 0x80bb0007);
    }
    

	public static void executeCommand(String command, Log log, String tag, String msg) throws EngineException {
    		try {
				if((int)run(command, log, Utils::executeWithLogStream) != 0) 
					treatException(log, tag, msg);
			} catch (IOException e) {
				treatException(log, tag, e, msg);
			}
    }

	public static String getFileNameWithExtension(String fileName) {
		int idxBegin = getLastSlashIndex(fileName);
		return (idxBegin != -1) ? fileName.substring(idxBegin + "/".length()) : fileName;
	}
	
	public static String getFileName(String filePath) {
		String fileName = getFileNameWithExtension(filePath);
		int idxEnd = fileName.indexOf(".");
		return (idxEnd != -1) ? fileName.substring(0, idxEnd) : fileName;
	}
	
	public static String getFileExtension(String filePath) {
		return filePath.substring(filePath.lastIndexOf(".") + 1);
	}

	public static void treatException(Log log, String tag, String msg) throws EngineException {
		treatException(log, tag, null, msg);
	}
	
	public static void treatException(Log log, String tag, Exception e, String msg) throws EngineException {
		log.error(tag, msg);
		
		if(e != null) {
			log.error(tag, "stackTrace: " + getStackTrace(e));
			throw new EngineException(":: " + msg + " ::", e);
		}
		throw new EngineException(":: " + msg + " ::");
	}
	
	public static Object run(String command, Log log, BiFunction<Process, Log, Object> callback) throws IOException {
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);

		return callback.apply(proc, log);
	}

	public static String run(String command) throws IOException {
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);

		try (BufferedReader bf = new BufferedReader(new InputStreamReader(proc.getInputStream(), Charset.forName("UTF-8")))) {
			StringBuilder inputContent = new StringBuilder();
			String line;

			while ((line = bf.readLine()) != null)
				inputContent.append(line).append("\n");

			return inputContent.toString();
		}
	}
	
	public static String getStackTrace(Throwable t){
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	
	
	
	private static int executeWithLogStream(Process proc, Log log) {
			try {
				logStream(proc.getErrorStream(), ERROR_TAG, log);
				logStream(proc.getInputStream(), INPUT_TAG, log);
				
				return proc.waitFor();
			} catch (IOException | InterruptedException e) {
				log.error(TAG, "Error logging command execution" + e.getMessage());
			}
			return 1;
	}
    
    private static void logStream(InputStream stream, String tag, Log log) throws IOException{
    		BufferedReader bf = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));

            String line;
            StringBuilder msg = new StringBuilder();
            while((line=bf.readLine()) != null)
                    msg.append(line).append("\n");
            
            if(msg.length() != 0) {
            	if(tag.equals(ERROR_TAG))
            		log.error(tag, msg.toString());
            	else
            		log.info(tag, msg.toString());
            }

            bf.close();	
    }

	private static int getLastSlashIndex(String fileName) {
		int indexOfBackSlash = fileName.lastIndexOf("/");
		int indexOfSlash = fileName.lastIndexOf("\\");
		return indexOfSlash != -1 ? indexOfSlash : indexOfBackSlash;
	}
	
	public static String readStream(Process proc, Log log) {
		BufferedReader bf = new BufferedReader(new InputStreamReader(proc.getInputStream(), Charset.forName("UTF-8")));
		String line;
		StringBuilder inputContent = new StringBuilder();
		try {
			while((line=bf.readLine()) != null)
				inputContent.append(line).append("\n");	
			return inputContent.toString();
		} catch (IOException e) {
			log.error(TAG, "Error reading stream " + e.getMessage());
		}
		try {
			bf.close();
		} catch (IOException e) {
			log.error(TAG, "Error reading stream " + e.getMessage());
		}
		return null;
	}

	public static void wait(long waitTime, Runnable runner) {
		long endTime = System.currentTimeMillis() + waitTime;
		
		while(System.currentTimeMillis() <= endTime){
			try{Thread.sleep(500);}catch(InterruptedException ex){}
		}

		runner.run();
	}

	public static String getOSType() {
		String osName = System.getProperty("os.name");
		if(osName.contains(WIN_OS_TYPE))
			return WIN_OS_TYPE;
		return UNIX_OS_TYPE;
	}
}
