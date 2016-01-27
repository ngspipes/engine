package ngspipesengine.compiler;

import ngspipesengine.utils.Log;
import ngspipesengine.utils.Uris;
import ngspipesengine.utils.Utils;

public class CompilerProperties {		

	private static String getPathForName(String filePath) {
		String path = Uris.getFilePath(filePath);
		
		if(path.contains("/"))
			path = path.replace("/", "");
		if(path.contains("\\"))
			path = path.replace("\\", "");
		if(path.contains(":"))
			path = path.replace(":", "");
		return path;
	}

	
	
	
	final String baseClassName;
	public String getBaseClassName() { return baseClassName; }
	
	final String pipelinePath;	
	public String getPipelinePath() { return pipelinePath; }

	Log log;	
	public void setLog(Log log) { this.log = log; }
	public Log getLog() { return log; }
	
	int from;		
	public int getFrom() { return this.from; }
	public void setFrom(int from) { 
		this.from = from;
	}
	
	int to;
	public int getTo() { return this.to; }
	public void setTo(int to) { 
		this.to = to;
	}

	protected final String pipelineFolder;
	public String getPipelineFolder() { return this.pipelineFolder; }
	
	protected String repositoryUri;
	public String getRepositoryUri() { return repositoryUri; }
	public void setRepositoryUri(String repositoryUri) { this.repositoryUri = repositoryUri; }
	
	protected String repositoryType; 
	public void setRepositoryType(String repositoryType) { this.repositoryType = repositoryType; }
	public String getRepositoryType() { return this.repositoryType; }
	
	
	public CompilerProperties(String pipelinePath, int from, int to, String baseClassName) {
		this.pipelinePath = pipelinePath;
		this.baseClassName = baseClassName;
		this.from = from;
		this.to = to;
		this.pipelineFolder = getPathForName(pipelinePath) +  Utils.getFileName(pipelinePath) + Uris.SEP;
	}

	
}
