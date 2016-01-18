package ngspipesengine.configurator.scripts;

import java.util.List;
import java.util.Map;

import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.IO;

public class Script {

	static final String BEGIN_SCRIPT = "#!/bin/bash\n";
	static final String BEGIN_VERIFY_EXIST_COMMAND = 	"if which docker ; then \n echo exist\n	else\n\t";
	static final String END_IF_COMMAND = "fi";
	static final String SHARED_FOLDER_COMMAND = "if [ ! -f %1$s ]; then\n\tmount -t vboxsf %2$s %1$s";
	static final String POWER_OFF_MACHINE = "\nsudo /sbin/shutdown -h now";
	static final String UPDATE_COMMAND = " apt-get update";

	private static final String ROOT_COMMAND = "sudo";
	
	
	private static String path;
	public static void setPath(String updatedPath) { path = updatedPath; }
	

	public static void createExecute(Map<String, List<String>> commands, String runnerCommand) throws EngineException {
		StringBuilder script = new StringBuilder(BEGIN_SCRIPT);
		script.append(ROOT_COMMAND).append(UPDATE_COMMAND);
		getScriptContent(commands, script);
		script	.append("\n").append(ROOT_COMMAND).append(" ")
				.append(runnerCommand)
				.append(POWER_OFF_MACHINE);
		IO.writeToFile(getExecuteName(), script.toString(), false);
	}
	
	private static String getExecuteName() {
		return path + "execute.sh";
	}

	private static void getScriptContent(Map<String, List<String>> commands, StringBuilder script) {
		script.append("\n");

		for (Map.Entry<String, List<String>> entry : commands.entrySet()) 
			getCommandInstallation(entry.getKey(), entry.getValue(), script);
	}

	private static void getCommandInstallation(String name, List<String> setups, StringBuilder script) {
		script	.append(String.format(BEGIN_VERIFY_EXIST_COMMAND, name))
		.append(getSetups(setups))
		.append(END_IF_COMMAND).append("\n");
	}

	private static StringBuilder getSetups(List<String> setups) {
		StringBuilder commands = new StringBuilder();

		for (String c : setups) {
			if(c.contains(ROOT_COMMAND))
				commands.append(ROOT_COMMAND).append(" ");
			
			commands.append(c).append("\n");
		}
		return commands;
	}
}
