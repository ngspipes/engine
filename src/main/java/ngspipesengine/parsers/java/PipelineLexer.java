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
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PipelineLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, String=8, Digit=9, 
		WS=10;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "String", "Digit", 
		"ESC", "UNICODE", "HEX", "WS"
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


	public PipelineLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Pipeline.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\fi\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3"+
		"\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\7\t"+
		"J\n\t\f\t\16\tM\13\t\3\t\3\t\3\n\6\nR\n\n\r\n\16\nS\3\13\3\13\3\13\5\13"+
		"Y\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16\6\16d\n\16\r\16\16\16e\3\16"+
		"\3\16\2\2\17\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\2\27\2\31\2\33"+
		"\f\3\2\7\4\2$$^^\3\2\62;\n\2$$\61\61^^ddhhppttvv\5\2\62;CHch\5\2\13\f"+
		"\17\17\"\"j\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\33\3\2\2\2\3\35"+
		"\3\2\2\2\5&\3\2\2\2\7(\3\2\2\2\t*\3\2\2\2\13/\3\2\2\2\r\67\3\2\2\2\17"+
		"@\3\2\2\2\21F\3\2\2\2\23Q\3\2\2\2\25U\3\2\2\2\27Z\3\2\2\2\31`\3\2\2\2"+
		"\33c\3\2\2\2\35\36\7R\2\2\36\37\7k\2\2\37 \7r\2\2 !\7g\2\2!\"\7n\2\2\""+
		"#\7k\2\2#$\7p\2\2$%\7g\2\2%\4\3\2\2\2&\'\7}\2\2\'\6\3\2\2\2()\7\177\2"+
		"\2)\b\3\2\2\2*+\7v\2\2+,\7q\2\2,-\7q\2\2-.\7n\2\2.\n\3\2\2\2/\60\7e\2"+
		"\2\60\61\7q\2\2\61\62\7o\2\2\62\63\7o\2\2\63\64\7c\2\2\64\65\7p\2\2\65"+
		"\66\7f\2\2\66\f\3\2\2\2\678\7c\2\289\7t\2\29:\7i\2\2:;\7w\2\2;<\7o\2\2"+
		"<=\7g\2\2=>\7p\2\2>?\7v\2\2?\16\3\2\2\2@A\7e\2\2AB\7j\2\2BC\7c\2\2CD\7"+
		"k\2\2DE\7p\2\2E\20\3\2\2\2FK\7$\2\2GJ\5\25\13\2HJ\n\2\2\2IG\3\2\2\2IH"+
		"\3\2\2\2JM\3\2\2\2KI\3\2\2\2KL\3\2\2\2LN\3\2\2\2MK\3\2\2\2NO\7$\2\2O\22"+
		"\3\2\2\2PR\t\3\2\2QP\3\2\2\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T\24\3\2\2\2"+
		"UX\7^\2\2VY\t\4\2\2WY\5\27\f\2XV\3\2\2\2XW\3\2\2\2Y\26\3\2\2\2Z[\7w\2"+
		"\2[\\\5\31\r\2\\]\5\31\r\2]^\5\31\r\2^_\5\31\r\2_\30\3\2\2\2`a\t\5\2\2"+
		"a\32\3\2\2\2bd\t\6\2\2cb\3\2\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2fg\3\2\2"+
		"\2gh\b\16\2\2h\34\3\2\2\2\b\2IKSXe\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}