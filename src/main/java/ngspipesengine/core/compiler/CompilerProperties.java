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
package ngspipesengine.core.compiler;

import ngspipesengine.presentation.ui.utils.Log;
import ngspipesengine.presentation.ui.utils.Uris;
import ngspipesengine.presentation.ui.utils.Utils;

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
