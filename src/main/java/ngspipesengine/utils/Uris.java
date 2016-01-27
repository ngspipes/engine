package ngspipesengine.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import ngspipesengine.configurator.properties.Properties;
import ngspipesengine.exceptions.EngineException;

public class Uris {

	static final String URL_BEGIN = "file:/";

    public static final String ENGINE_FOLDER_NAME = "Engine";
    public static final String PIPELINES_FOLDER_NAME = "Pipelines";
    public static final String DSL_JAR_NAME = "dsl.jar";
    public static final String JSON_JAR_NAME = "java-json.jar";
    public static final String OUTPUTS_FOLDER_NAME = "Outputs";
    public static final String INPUTS_FOLDER_NAME = "Inputs";
    public static final String REPOSITORY_FOLDER_NAME = "Repository";
    
	///////// HOST DIRECTORIES PATHS ////////
    public final static String NGSPIPES_DIRECTORY = getNGSPipesDirectory();

    public static final String SEP = "/";
    public static final String ENGINE_PATH = NGSPIPES_DIRECTORY + ENGINE_FOLDER_NAME;
    public static final String LOG_FOLDER_PATH = ENGINE_PATH  + SEP + "Log" + SEP;
    public static final String PIPELINES_FOLDER_PATH = ENGINE_PATH + SEP + PIPELINES_FOLDER_NAME + SEP;
    static final String ENGINE_JAR_PATH = getEngineJarUri();
    static final String RESOURCES = "resources" + SEP;
	static final String DSL_JAR_ORIGIN_PATH = RESOURCES + DSL_JAR_NAME;
	static final String JSON_JAR_ORIGIN_PATH = RESOURCES + JSON_JAR_NAME;
	public static final String REGISTER_FILE = LOG_FOLDER_PATH + "register.json";
	static final String VBOX_FILE_RELATIVE_PATH = "../NGSPipesEngineExecutor/NGSPipesEngineExecutor.vbox";
	

	///////// GUEST DIRECTORIES PATHS ////////
    static final String VM_MAIN_FOLDER = "/home/ngspipes/";
    public static final String VM_ENGINE_PATH = VM_MAIN_FOLDER + ENGINE_FOLDER_NAME + SEP;
    public static final String VM_PIPELINE_PATH = VM_MAIN_FOLDER + "Pipeline" + SEP;
    public static final String VM_OUTPUTS_FOLDER_PATH = VM_MAIN_FOLDER + OUTPUTS_FOLDER_NAME;  
    public static final String VM_INPUTS_FOLDER_PATH = VM_MAIN_FOLDER + INPUTS_FOLDER_NAME;  
    public static final String VM_REPOSITORY_FOLDER_PATH = VM_MAIN_FOLDER + REPOSITORY_FOLDER_NAME;


	public static URL getVboxFilePath() {
		URL url = null;
		/** TODO: refactor this to throw MalformedURLException*/
		try {
			url = new File(VBOX_FILE_RELATIVE_PATH).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		 return url;
	}

    public static String getLogPath(String fileName) {
    	return LOG_FOLDER_PATH + fileName;
    }

	public static URL getURLFor(String filePath) throws MalformedURLException {
		return new URL(URL_BEGIN + filePath);
	}
	
	public static String getActualPipelinePath(String pipelineFolderName) {
		return Uris.PIPELINES_FOLDER_PATH + pipelineFolderName;
	}	
	
	public static String getFilePath(String filePath) {
		int idx = filePath.lastIndexOf("/");
		
		if(idx == -1) idx = filePath.lastIndexOf("\\");
		
		return (idx == -1) ? "" : filePath.substring(0, idx + 1);
	}	
	
	public static void load(Properties props, String tag) throws EngineException {
		System.out.println("Loading engine directories");
		File engineDirectory = new File(ENGINE_PATH);
		
		if(!engineDirectory.exists())
			engineDirectory.mkdirs();
		

		File logDirectory = new File(Uris.LOG_FOLDER_PATH);
		File pipelinesDirectory = new File(Uris.PIPELINES_FOLDER_PATH);
		File dslJar = new File(ENGINE_PATH + SEP + DSL_JAR_NAME);
		File jsonJar = new File(ENGINE_PATH + SEP + JSON_JAR_NAME);
		
		if(!logDirectory.exists())
			logDirectory.mkdirs();
		
		if(!pipelinesDirectory.exists())
			pipelinesDirectory.mkdirs();		

		System.out.println("Loading engine resources");
		if(!dslJar.exists())		
			copyLibrary(props, tag, DSL_JAR_ORIGIN_PATH);

		if(!jsonJar.exists())		
			copyLibrary(props, tag, JSON_JAR_ORIGIN_PATH);
		
		
		createPipelineFolder(props, tag);
	}

	private static String getNGSPipesDirectory() {
		return System.getProperty("user.home").replace("\\", "/") + "/NGSPipes/";
	}
	
	private static String getEngineJarUri() {
		return Uris.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath();
	}
	
	private static void createPipelineFolder(Properties props, String tag) {
		File pipelineDirectory = new File(Uris.getActualPipelinePath(props.getPipelineFolder()));

		if(!pipelineDirectory.exists()) {
			props.getLog().debug(tag, "Creating pipeline " + props.getPipelineName() + " folder");
			pipelineDirectory.mkdirs();
		}
	}

	private static void copyLibrary(Properties props, String tag, String uri) throws EngineException {
		props.getLog().debug(tag, "Start copying libraries");	
		
		try {		
			File jarFile = new File(ENGINE_JAR_PATH);			
			JarFile engineJar = new JarFile(jarFile);
			copyResourcesToDirectory(engineJar, uri);
		} catch (IOException e) {
			Utils.treatException(props.getLog(), tag, e, "Error copying libraries");
		} 

		props.getLog().debug(tag, "End copying libraries");	
	}

	private static void copyResourcesToDirectory(JarFile fromJar, String jarDir) throws IOException {
		for (Enumeration<JarEntry> entries = fromJar.entries(); entries.hasMoreElements();) {

			JarEntry entry = entries.nextElement();

			if (entry.getName().startsWith(jarDir) && !entry.isDirectory()) {
				File dest = new File(ENGINE_PATH + SEP + entry.getName().substring(jarDir.indexOf("/")+"/".length()));
				
				File parent = dest.getParentFile();

				if (!parent.exists()) 
					parent.mkdirs();

				FileOutputStream out = new FileOutputStream(dest);
				InputStream in = fromJar.getInputStream(entry);

				try {
					byte[] buffer = new byte[1024];

					int s = 0;
					while ((s = in.read(buffer)) > 0)
						out.write(buffer, 0, s);
						
				} finally {
					in.close();
					out.close();
				}
			}
		}
	}

}
