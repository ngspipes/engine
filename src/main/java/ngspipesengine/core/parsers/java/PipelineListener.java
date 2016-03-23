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
// Generated from Pipeline.g4 by ANTLR 4.5
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PipelineParser}.
 */
public interface PipelineListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PipelineParser#pipeline}.
	 * @param ctx the parse tree
	 */
	void enterPipeline(PipelineParser.PipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#pipeline}.
	 * @param ctx the parse tree
	 */
	void exitPipeline(PipelineParser.PipelineContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#tool}.
	 * @param ctx the parse tree
	 */
	void enterTool(PipelineParser.ToolContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#tool}.
	 * @param ctx the parse tree
	 */
	void exitTool(PipelineParser.ToolContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#command}.
	 * @param ctx the parse tree
	 */
	void enterCommand(PipelineParser.CommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#command}.
	 * @param ctx the parse tree
	 */
	void exitCommand(PipelineParser.CommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(PipelineParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(PipelineParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#chain}.
	 * @param ctx the parse tree
	 */
	void enterChain(PipelineParser.ChainContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#chain}.
	 * @param ctx the parse tree
	 */
	void exitChain(PipelineParser.ChainContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#repositoryType}.
	 * @param ctx the parse tree
	 */
	void enterRepositoryType(PipelineParser.RepositoryTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#repositoryType}.
	 * @param ctx the parse tree
	 */
	void exitRepositoryType(PipelineParser.RepositoryTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#repositoryLocation}.
	 * @param ctx the parse tree
	 */
	void enterRepositoryLocation(PipelineParser.RepositoryLocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#repositoryLocation}.
	 * @param ctx the parse tree
	 */
	void exitRepositoryLocation(PipelineParser.RepositoryLocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#toolName}.
	 * @param ctx the parse tree
	 */
	void enterToolName(PipelineParser.ToolNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#toolName}.
	 * @param ctx the parse tree
	 */
	void exitToolName(PipelineParser.ToolNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#configuration}.
	 * @param ctx the parse tree
	 */
	void enterConfiguration(PipelineParser.ConfigurationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#configuration}.
	 * @param ctx the parse tree
	 */
	void exitConfiguration(PipelineParser.ConfigurationContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#commandName}.
	 * @param ctx the parse tree
	 */
	void enterCommandName(PipelineParser.CommandNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#commandName}.
	 * @param ctx the parse tree
	 */
	void exitCommandName(PipelineParser.CommandNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#argumentName}.
	 * @param ctx the parse tree
	 */
	void enterArgumentName(PipelineParser.ArgumentNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#argumentName}.
	 * @param ctx the parse tree
	 */
	void exitArgumentName(PipelineParser.ArgumentNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#argumentValue}.
	 * @param ctx the parse tree
	 */
	void enterArgumentValue(PipelineParser.ArgumentValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#argumentValue}.
	 * @param ctx the parse tree
	 */
	void exitArgumentValue(PipelineParser.ArgumentValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#outputName}.
	 * @param ctx the parse tree
	 */
	void enterOutputName(PipelineParser.OutputNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#outputName}.
	 * @param ctx the parse tree
	 */
	void exitOutputName(PipelineParser.OutputNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#toolPos}.
	 * @param ctx the parse tree
	 */
	void enterToolPos(PipelineParser.ToolPosContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#toolPos}.
	 * @param ctx the parse tree
	 */
	void exitToolPos(PipelineParser.ToolPosContext ctx);
	/**
	 * Enter a parse tree produced by {@link PipelineParser#commandPos}.
	 * @param ctx the parse tree
	 */
	void enterCommandPos(PipelineParser.CommandPosContext ctx);
	/**
	 * Exit a parse tree produced by {@link PipelineParser#commandPos}.
	 * @param ctx the parse tree
	 */
	void exitCommandPos(PipelineParser.CommandPosContext ctx);
}