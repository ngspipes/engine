package ngspipesengine.configurator.engines;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import ngspipesengine.configurator.properties.Properties;
import ngspipesengine.configurator.scripts.Script;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.Uris;
import ngspipesengine.utils.Utils;

public abstract class Engine implements IEngine {


	private static final String TAG = "Engine";
	public static final Map<String, Function<String, String>> OS_PATH_FORMATTERS;
	public static final Map<String, Function<URL, String>> OS_URL_FORMATTERS;
	public static final Map<String, Function<String, String>> OS_QUOTATION_MARKS_FORMATTERS;

	static {
		OS_PATH_FORMATTERS = new HashMap<>();
		OS_URL_FORMATTERS = new HashMap<>();
		OS_QUOTATION_MARKS_FORMATTERS = new HashMap<>();
		loadFormatters();
	}

	private static void loadFormatters() {
		OS_PATH_FORMATTERS.put(Utils.WIN_OS_TYPE, Engine::windowsPathFormater);
		OS_PATH_FORMATTERS.put(Utils.UNIX_OS_TYPE, Engine::unixPathFormater);
		
		OS_URL_FORMATTERS.put(Utils.WIN_OS_TYPE, Engine::windowsUrlFormater);
		OS_URL_FORMATTERS.put(Utils.UNIX_OS_TYPE, Engine::unixUrlFormater);
		
		OS_QUOTATION_MARKS_FORMATTERS.put(Utils.UNIX_OS_TYPE, Engine::unixQuotationMarks);
		OS_QUOTATION_MARKS_FORMATTERS.put(Utils.WIN_OS_TYPE, Engine::windowsQuotationMarks);
	}
	
	private static String windowsPathFormater(String path) {
		if(!path.contains(" "))
			return path;

		return "\"" + path + "\"";
	}

	private static String unixPathFormater(String path) {
		if(!path.contains(" "))
			return path;

		return path.replace(" ", "\\ ");
	}
	
	private static String windowsUrlFormater(URL url) {
		return url.getPath().substring(1);
	}

	private static String unixUrlFormater(URL url) {
		return url.getPath();
	}
	
	private static String windowsQuotationMarks(String source) {
		return "\"" + source + "\"";
	}

	private static String unixQuotationMarks(String source) {
		return source;
	}

	protected static String getRunCommand(String pipelinePath, String libsPath, String executableName, String mainArgs) {
		StringBuilder runCommand = new StringBuilder("java -cp ");
		
		Function<String, String> formatter = OS_PATH_FORMATTERS.get(Utils.OS_TYPE);
		
		runCommand	.append(formatter.apply(pipelinePath))
					.append(":")
					.append(formatter.apply(libsPath + Uris.DSL_JAR_NAME))
					.append(":")
					.append(formatter.apply(libsPath + Uris.JSON_JAR_NAME))
					.append(" ").append(executableName).append(" ").append(mainArgs);
		
		return runCommand.toString();
	}
	
	public static String formatter(String path) {
		return OS_PATH_FORMATTERS.get(Utils.OS_TYPE).apply(path);
	}

	
	
	protected final Properties props;
	
	public Engine(Properties props) throws EngineException {    
		this.props = props;		
	}

	@Override
	public void start() throws EngineException {
		props.getLog().debug(TAG, "Starting");
		this.cloneEngine();
		System.out.println("Configurating engine");
		this.props.set();
		createScripts();
		System.out.println("Starting execute engine");
		this.internalStart();
	}

	protected abstract void cloneEngine() throws EngineException;
	
	protected abstract void recoverState() throws EngineException;

	protected abstract void internalStart() throws EngineException;
	
	protected abstract String getRunnerCommand() throws EngineException;

	protected void createScripts() throws EngineException {
		props.getLog().debug(TAG, "Create script");
		Script.setPath(Uris.getActualPipelinePath(props.getPipelineName()));
		Script.createExecute(props.getSetups(), getRunnerCommand());
		props.getLog().debug(TAG, "Create script success");
	}
	
}
