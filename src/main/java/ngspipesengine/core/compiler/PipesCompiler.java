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

import java.net.MalformedURLException;

import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.core.parsers.java.PipesJavaParser;
import ngspipesengine.presentation.ui.utils.IO;

public class PipesCompiler extends PipelineCompiler {

	private static final String PIPES_BASE_PATH = "";

	public PipesCompiler(CompilerProperties props) {
		super(props);
	}


	@Override
	protected String getExecutableJavaClass() throws MalformedURLException, EngineException {
		
		String pipesName = getName();
		StringBuilder pipeClassContent = PipesJavaParser.getPipelineAsJava(props.getPipelinePath(), props.getLog());

		
		StringBuilder repoType = new StringBuilder();
		int repoFinishLineIdx = getRepoType(pipeClassContent, repoType);
		String pipeContent = pipeClassContent.substring(repoFinishLineIdx + 1);

		props.setRepositoryType(repoType.toString());

		String pipeClass = IO.readFile(PIPES_BASE_PATH + props.getBaseClassName());
		pipeClass = String.format(pipeClass, pipesName, "\"" + repoType + "\"", pipeContent);

		return pipeClass;
	}

	
	private int getRepoType(StringBuilder pipeClassContent, StringBuilder repoType) {
		int endTypeIdx = pipeClassContent.indexOf(", ");
		int endLineIdx = pipeClassContent.indexOf(").");
		int sepLength = "\"".length();

		repoType.append(pipeClassContent.substring((pipeClassContent.indexOf("\"") + sepLength) , (endTypeIdx - sepLength)));
		props.setRepositoryUri(pipeClassContent.substring(endTypeIdx + ", \"".length(), endLineIdx - ")".length()));

		return endLineIdx + ".".length();
	}
}
