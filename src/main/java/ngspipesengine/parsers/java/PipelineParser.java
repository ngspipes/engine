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
package ngspipesengine.parsers.java;
// Generated from Pipeline.g4 by ANTLR 4.5
import java.util.List;

import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PipelineParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, String=8, Digit=9, 
		WS=10;
	public static final int
		RULE_pipeline = 0, RULE_tool = 1, RULE_command = 2, RULE_argument = 3, 
		RULE_chain = 4, RULE_repositoryType = 5, RULE_repositoryLocation = 6, 
		RULE_toolName = 7, RULE_configuration = 8, RULE_commandName = 9, RULE_argumentName = 10, 
		RULE_argumentValue = 11, RULE_outputName = 12, RULE_toolPos = 13, RULE_commandPos = 14;
	public static final String[] ruleNames = {
		"pipeline", "tool", "command", "argument", "chain", "repositoryType", 
		"repositoryLocation", "toolName", "configuration", "commandName", "argumentName", 
		"argumentValue", "outputName", "toolPos", "commandPos"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'Pipeline'", "'{'", "'}'", "'tool'", "'command'", "'argument'", 
		"'chain'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, "String", "Digit", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Pipeline.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PipelineParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class PipelineContext extends ParserRuleContext {
		public RepositoryTypeContext repositoryType() {
			return getRuleContext(RepositoryTypeContext.class,0);
		}
		public RepositoryLocationContext repositoryLocation() {
			return getRuleContext(RepositoryLocationContext.class,0);
		}
		public List<ToolContext> tool() {
			return getRuleContexts(ToolContext.class);
		}
		public ToolContext tool(int i) {
			return getRuleContext(ToolContext.class,i);
		}
		public PipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitPipeline(this);
		}
	}

	public final PipelineContext pipeline() throws RecognitionException {
		PipelineContext _localctx = new PipelineContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_pipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(T__0);
			setState(31);
			repositoryType();
			setState(32);
			repositoryLocation();
			setState(33);
			match(T__1);
			setState(35); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(34);
				tool();
				}
				}
				setState(37); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__3 );
			setState(39);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ToolContext extends ParserRuleContext {
		public ToolNameContext toolName() {
			return getRuleContext(ToolNameContext.class,0);
		}
		public ConfigurationContext configuration() {
			return getRuleContext(ConfigurationContext.class,0);
		}
		public List<CommandContext> command() {
			return getRuleContexts(CommandContext.class);
		}
		public CommandContext command(int i) {
			return getRuleContext(CommandContext.class,i);
		}
		public ToolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tool; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterTool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitTool(this);
		}
	}

	public final ToolContext tool() throws RecognitionException {
		ToolContext _localctx = new ToolContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_tool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(T__3);
			setState(42);
			toolName();
			setState(43);
			configuration();
			setState(44);
			match(T__1);
			setState(46); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(45);
				command();
				}
				}
				setState(48); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__4 );
			setState(50);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandContext extends ParserRuleContext {
		public CommandNameContext commandName() {
			return getRuleContext(CommandNameContext.class,0);
		}
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public List<ChainContext> chain() {
			return getRuleContexts(ChainContext.class);
		}
		public ChainContext chain(int i) {
			return getRuleContext(ChainContext.class,i);
		}
		public CommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitCommand(this);
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_command);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(T__4);
			setState(53);
			commandName();
			setState(54);
			match(T__1);
			setState(57); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(57);
				switch (_input.LA(1)) {
				case T__5:
					{
					setState(55);
					argument();
					}
					break;
				case T__6:
					{
					setState(56);
					chain();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(59); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__5 || _la==T__6 );
			setState(61);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentContext extends ParserRuleContext {
		public ArgumentNameContext argumentName() {
			return getRuleContext(ArgumentNameContext.class,0);
		}
		public ArgumentValueContext argumentValue() {
			return getRuleContext(ArgumentValueContext.class,0);
		}
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitArgument(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(T__5);
			setState(64);
			argumentName();
			setState(65);
			argumentValue();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ChainContext extends ParserRuleContext {
		public ArgumentNameContext argumentName() {
			return getRuleContext(ArgumentNameContext.class,0);
		}
		public OutputNameContext outputName() {
			return getRuleContext(OutputNameContext.class,0);
		}
		public CommandNameContext commandName() {
			return getRuleContext(CommandNameContext.class,0);
		}
		public ToolNameContext toolName() {
			return getRuleContext(ToolNameContext.class,0);
		}
		public CommandPosContext commandPos() {
			return getRuleContext(CommandPosContext.class,0);
		}
		public ToolPosContext toolPos() {
			return getRuleContext(ToolPosContext.class,0);
		}
		public ChainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_chain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterChain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitChain(this);
		}
	}

	public final ChainContext chain() throws RecognitionException {
		ChainContext _localctx = new ChainContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_chain);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(T__6);
			setState(68);
			argumentName();
			setState(79);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(73);
				switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
				case 1:
					{
					setState(70);
					_la = _input.LA(1);
					if (_la==Digit) {
						{
						setState(69);
						toolPos();
						}
					}

					setState(72);
					toolName();
					}
					break;
				}
				setState(76);
				_la = _input.LA(1);
				if (_la==Digit) {
					{
					setState(75);
					commandPos();
					}
				}

				setState(78);
				commandName();
				}
				break;
			}
			setState(81);
			outputName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RepositoryTypeContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(PipelineParser.String, 0); }
		public RepositoryTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_repositoryType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterRepositoryType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitRepositoryType(this);
		}
	}

	public final RepositoryTypeContext repositoryType() throws RecognitionException {
		RepositoryTypeContext _localctx = new RepositoryTypeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_repositoryType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RepositoryLocationContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(PipelineParser.String, 0); }
		public RepositoryLocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_repositoryLocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterRepositoryLocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitRepositoryLocation(this);
		}
	}

	public final RepositoryLocationContext repositoryLocation() throws RecognitionException {
		RepositoryLocationContext _localctx = new RepositoryLocationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_repositoryLocation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ToolNameContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(PipelineParser.String, 0); }
		public ToolNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_toolName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterToolName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitToolName(this);
		}
	}

	public final ToolNameContext toolName() throws RecognitionException {
		ToolNameContext _localctx = new ToolNameContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_toolName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConfigurationContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(PipelineParser.String, 0); }
		public ConfigurationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_configuration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterConfiguration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitConfiguration(this);
		}
	}

	public final ConfigurationContext configuration() throws RecognitionException {
		ConfigurationContext _localctx = new ConfigurationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_configuration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandNameContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(PipelineParser.String, 0); }
		public CommandNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commandName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterCommandName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitCommandName(this);
		}
	}

	public final CommandNameContext commandName() throws RecognitionException {
		CommandNameContext _localctx = new CommandNameContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_commandName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentNameContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(PipelineParser.String, 0); }
		public ArgumentNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterArgumentName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitArgumentName(this);
		}
	}

	public final ArgumentNameContext argumentName() throws RecognitionException {
		ArgumentNameContext _localctx = new ArgumentNameContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_argumentName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentValueContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(PipelineParser.String, 0); }
		public ArgumentValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterArgumentValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitArgumentValue(this);
		}
	}

	public final ArgumentValueContext argumentValue() throws RecognitionException {
		ArgumentValueContext _localctx = new ArgumentValueContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_argumentValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OutputNameContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(PipelineParser.String, 0); }
		public OutputNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterOutputName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitOutputName(this);
		}
	}

	public final OutputNameContext outputName() throws RecognitionException {
		OutputNameContext _localctx = new OutputNameContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_outputName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ToolPosContext extends ParserRuleContext {
		public TerminalNode Digit() { return getToken(PipelineParser.Digit, 0); }
		public ToolPosContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_toolPos; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterToolPos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitToolPos(this);
		}
	}

	public final ToolPosContext toolPos() throws RecognitionException {
		ToolPosContext _localctx = new ToolPosContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_toolPos);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(Digit);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandPosContext extends ParserRuleContext {
		public TerminalNode Digit() { return getToken(PipelineParser.Digit, 0); }
		public CommandPosContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commandPos; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).enterCommandPos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PipelineListener ) ((PipelineListener)listener).exitCommandPos(this);
		}
	}

	public final CommandPosContext commandPos() throws RecognitionException {
		CommandPosContext _localctx = new CommandPosContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_commandPos);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(Digit);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\fj\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\2\3\2\3\2\6\2&"+
		"\n\2\r\2\16\2\'\3\2\3\2\3\3\3\3\3\3\3\3\3\3\6\3\61\n\3\r\3\16\3\62\3\3"+
		"\3\3\3\4\3\4\3\4\3\4\3\4\6\4<\n\4\r\4\16\4=\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\6\3\6\3\6\5\6I\n\6\3\6\5\6L\n\6\3\6\5\6O\n\6\3\6\5\6R\n\6\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17"+
		"\3\17\3\20\3\20\3\20\2\2\21\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36\2\2"+
		"b\2 \3\2\2\2\4+\3\2\2\2\6\66\3\2\2\2\bA\3\2\2\2\nE\3\2\2\2\fU\3\2\2\2"+
		"\16W\3\2\2\2\20Y\3\2\2\2\22[\3\2\2\2\24]\3\2\2\2\26_\3\2\2\2\30a\3\2\2"+
		"\2\32c\3\2\2\2\34e\3\2\2\2\36g\3\2\2\2 !\7\3\2\2!\"\5\f\7\2\"#\5\16\b"+
		"\2#%\7\4\2\2$&\5\4\3\2%$\3\2\2\2&\'\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2()\3"+
		"\2\2\2)*\7\5\2\2*\3\3\2\2\2+,\7\6\2\2,-\5\20\t\2-.\5\22\n\2.\60\7\4\2"+
		"\2/\61\5\6\4\2\60/\3\2\2\2\61\62\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63"+
		"\64\3\2\2\2\64\65\7\5\2\2\65\5\3\2\2\2\66\67\7\7\2\2\678\5\24\13\28;\7"+
		"\4\2\29<\5\b\5\2:<\5\n\6\2;9\3\2\2\2;:\3\2\2\2<=\3\2\2\2=;\3\2\2\2=>\3"+
		"\2\2\2>?\3\2\2\2?@\7\5\2\2@\7\3\2\2\2AB\7\b\2\2BC\5\26\f\2CD\5\30\r\2"+
		"D\t\3\2\2\2EF\7\t\2\2FQ\5\26\f\2GI\5\34\17\2HG\3\2\2\2HI\3\2\2\2IJ\3\2"+
		"\2\2JL\5\20\t\2KH\3\2\2\2KL\3\2\2\2LN\3\2\2\2MO\5\36\20\2NM\3\2\2\2NO"+
		"\3\2\2\2OP\3\2\2\2PR\5\24\13\2QK\3\2\2\2QR\3\2\2\2RS\3\2\2\2ST\5\32\16"+
		"\2T\13\3\2\2\2UV\7\n\2\2V\r\3\2\2\2WX\7\n\2\2X\17\3\2\2\2YZ\7\n\2\2Z\21"+
		"\3\2\2\2[\\\7\n\2\2\\\23\3\2\2\2]^\7\n\2\2^\25\3\2\2\2_`\7\n\2\2`\27\3"+
		"\2\2\2ab\7\n\2\2b\31\3\2\2\2cd\7\n\2\2d\33\3\2\2\2ef\7\13\2\2f\35\3\2"+
		"\2\2gh\7\13\2\2h\37\3\2\2\2\n\'\62;=HKNQ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}