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
package ngspipesengine.core.parsers.java;

import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.core.utils.IO;
import ngspipesengine.core.utils.Log;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import java.net.MalformedURLException;
import java.util.List;

public class PipesJavaParser {
	
	private static final String TAG = "PipesParser"; 
	
	private static final String BEGIN_ELEMENT = "(";
	private static final String END_ELEMENT = ").";
	private static final String SEPARATOR = ", ";
	private static final String END_LINE = "\n\r";
	
	public static StringBuilder getPipelineAsJava(String filePath, Log log) throws MalformedURLException, EngineException {

		log.debug(TAG, "Begin compiling pipes");
		
		PipelineLexer lexer = new PipelineLexer(new ANTLRInputStream(IO.readFile(filePath)));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PipelineParser parser = new PipelineParser(tokens);
		
		PipelineParser.PipelineContext pipelineCtx;
		try {
			pipelineCtx = parser.pipeline();
			log.debug(TAG, "End compiling pipes");
		} catch(RecognitionException e){
			log.error(TAG, "pipes file is malformed");
			throw new EngineException("pipes file is malformed", e);
		}

		log.debug(TAG, "Parsing pipes to java");
		return getPipesContent(pipelineCtx);
	}
	
	private static StringBuilder constructElement(String... values) {
		StringBuilder element = new StringBuilder(BEGIN_ELEMENT);
		
		for(int idx = 0; idx < values.length; ++idx) {
			if(idx > 0) 
				element.append(SEPARATOR);
			element.append(values[idx]);
		}
			
		element.append(END_ELEMENT).append(END_LINE);

		return element;
		
	}
	
	private static StringBuilder getPipesContent(PipelineParser.PipelineContext pipelineCtx) {
		StringBuilder pipelineContent = new StringBuilder();
		pipelineContent	.append(pipelineCtx.children.get(0))
						.append(constructElement(pipelineCtx.repositoryType().getText(), 
												pipelineCtx.repositoryLocation().getText()))
						.append(getToolsContent(pipelineCtx.tool()))
						.append("\t\t\t\t\t");
		
		return pipelineContent;
	}

	private static StringBuilder getToolsContent(List<PipelineParser.ToolContext> tools) {
		StringBuilder toolsContent = new StringBuilder();
		
		for (PipelineParser.ToolContext tool : tools)
			toolsContent.append(getToolContent(tool));
		
		return toolsContent;
	}

	private static StringBuilder getToolContent(PipelineParser.ToolContext tool) {
		StringBuilder toolContent = new StringBuilder("\t\t\t\t\t");
		
		toolContent	.append(tool.children.get(0))
					.append(constructElement(tool.toolName().getText(), tool.configuration().getText()))
					.append(getCommandsContent(tool.command()));
		
		return toolContent;
	}

	private static StringBuilder getCommandsContent(List<PipelineParser.CommandContext> commands) {
		StringBuilder commandsContent = new StringBuilder();

		for (PipelineParser.CommandContext command : commands)
			commandsContent.append(getCommandContent(command));
		
		return commandsContent;
	}
	
	private static StringBuilder getCommandContent(PipelineParser.CommandContext command) {
		StringBuilder commandContent = new StringBuilder("\t\t\t\t\t\t");
		
		commandContent	.append(command.children.get(0))
						.append(constructElement(command.commandName().getText()))
						.append(getArgumentsContent(command.argument()))
						.append(getChainsContent(command.chain()));
		
		return commandContent;
	}
	
	private static StringBuilder getArgumentsContent(List<PipelineParser.ArgumentContext> arguments) {
		StringBuilder argumentsContent = new StringBuilder();

		for (PipelineParser.ArgumentContext argument : arguments)
			argumentsContent.append(getArgumentContent(argument));
		
		return argumentsContent;
	}
	
	private static StringBuilder getArgumentContent(PipelineParser.ArgumentContext argument) {
		StringBuilder argumentContent = new StringBuilder("\t\t\t\t\t\t\t");
		
		argumentContent	.append(argument.children.get(0))
						.append(constructElement(argument.argumentName().getText(), argument.argumentValue().getText()));
		
		return argumentContent;
	}
	
	private static StringBuilder getChainsContent(List<PipelineParser.ChainContext> chains) {
		StringBuilder chainsContent = new StringBuilder();

		for (PipelineParser.ChainContext chain : chains)
			chainsContent.append(getChainContent(chain));
		
		return chainsContent;
	}
	
	private static StringBuilder getChainContent(PipelineParser.ChainContext chain) {
		StringBuilder chainContent = new StringBuilder("\t\t\t\t\t\t\t");
		
		chainContent.append(chain.children.get(0).getText())
					.append(BEGIN_ELEMENT)
					.append(chain.argumentName().getText())
					.append(SEPARATOR)
					.append(getOptionalParameters(chain))
					.append(chain.outputName().getText())
					.append(END_ELEMENT).append(END_LINE);
		
		return chainContent;
	}

	private static StringBuilder getOptionalParameters(PipelineParser.ChainContext chain) {
		StringBuilder optionalParameters = new StringBuilder();
		
		if(chain.toolPos() != null)
			optionalParameters	.append(chain.toolPos().getText())
								.append(SEPARATOR);

		if(chain.toolName() != null)
			optionalParameters	.append(chain.toolName().getText())
								.append(SEPARATOR);
		
		if(chain.commandPos() != null)
			optionalParameters	.append(chain.commandPos().getText())
								.append(SEPARATOR);
		
		if(chain.commandName() != null)
			optionalParameters	.append(chain.commandName().getText())
								.append(SEPARATOR);
		
		return optionalParameters;
	}
}
