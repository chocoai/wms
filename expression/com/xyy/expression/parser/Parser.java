package com.xyy.expression.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;

import com.xyy.expression.Grammar;
import com.xyy.expression.GrammarHandler;
import com.xyy.expression.Operator;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatToken;
/**
 * 解析器类
 * @author evan
 *
 */
public class Parser {
	private static GrammarHandler grammarHandler = new GrammarHandler();
	private static ParserHandler parserHandler = new ParserHandler();
	private TreatToken tk;

	public Parser() {
		this.buildTreatTokens();
	}

	public TreatToken getTk() {
		return tk;
	}

	public void setTk(TreatToken tk) {
		this.tk = tk;
	}

	// 构建处理器链
	public void buildTreatTokens() {
		WordToken wordToken = new WordToken(this);
		TreatToken curPos = wordToken;
		FunToken funToken = new FunToken(this);
		curPos.setNext(funToken);
		curPos = funToken;
		Map<String, Operator> opeMaps = parserHandler.buildTokenProcessor();
		for (Operator ope : opeMaps.values()) {
			OperatorToken operatorToken = new OperatorToken(this, ope);
			curPos.setNext(operatorToken);
			curPos = operatorToken;
		}
		OtherToken otherToken = new OtherToken(this);
		curPos.setNext(otherToken);
		curPos = otherToken;
		this.tk = wordToken;
	}

	/**
	 * 特殊符号映射表
	 */
	private static String SYS_SPECIFIC_SYMBOLS = "~";

	private List<Symbol> symbols = new ArrayList<Parser.Symbol>();// 符号表

	/**
	 * 符号表中保留的符号
	 * 
	 * @author Administrator
	 * 
	 */
	private class Symbol {
		private String name;
		private String value;
		private int start;
		private int end;
		private boolean status;

		public Symbol(String name, String value, int start, int end,
				boolean status) {
			super();
			this.name = name;
			this.value = value;
			this.start = start;
			this.end = end;
			this.status = status;
		}

		public int getStart() {
			return start;
		}
	}

	/**
	 * 预解析
	 * 
	 * @param expr
	 * @return
	 */

	private List<Symbol> preParse(String expr) throws Exception {
		StringBuffer sb = new StringBuffer(expr);
		List<Grammar> grammars = grammarHandler.buildGrammars();
		int count = 0;
		for (int i = grammars.size() - 1; i >= 0; i--) {
			Grammar grammar = grammars.get(i);
			Matcher matcher = grammar.buildPattern().matcher(sb.toString());// 注意这里匹配以后，字符中不能再改动了，所以sb.toString()
			count++;
			while (matcher.find()) {
				String value = matcher.group().trim();// 匹配的值
				String symbolName = null;
				boolean isCount = false;
				if (",".equals(value)) {
					symbolName = "$0$0";
					isCount = false;
				} else if ("(".equals(value)) {
					symbolName = "$0$1";
					isCount = false;
				} else if (")".equals(value)) {
					symbolName = "$0$2";
					isCount = false;
				} else {
					symbolName = grammar.getSymPrefix() + count;
					isCount = true;
				}
				int start = matcher.start();
				int end = matcher.end();
				if (grammar.getTokenType() == TokenType.TOKEN_TYPE_FUN) {
					value = value.substring(0, value.length() - 1);
					end--;
				} else if (grammar.getTokenType() == TokenType.TOKEN_TYPE_FLOAT
						|| grammar.getTokenType() == TokenType.TOKEN_TYPE_INT) {
					int l = 0;
					while (l < value.length()) {
						if ("0123456789".contains(value.subSequence(l, l + 1))) {
							value = value.substring(l);
							start += l;
							break;
						}
						l++;
					}
				}
				symbols.add(new Symbol(symbolName, value, start, end, false));// 存入符号表
				if (isCount) {
					count++;
				}
			}
			count = 0;// reset count
			this.processSymbolTalbe(sb);
		}
		String valResultString = sb.toString().replace("~", "")
				.replace("@", "").trim();
		if (!"".equals(valResultString)) {
			throw new Exception("invalide symbol:" + valResultString);
		}
		this.sortSymbolsTalbe();
		return this.symbols;

	}

	/**
	 * 排序得到的符号表
	 */
	private void sortSymbolsTalbe() {
		Collections.sort(this.symbols, new Comparator<Symbol>() {
			@Override
			public int compare(Symbol o1, Symbol o2) {
				if (o2.getStart() > o1.getStart()) {
					return -1;
				} else if (o2.getStart() == o1.getStart()) {
					return 0;
				} else {
					return 1;
				}
			}
		});
	}

	/**
	 * 用特殊符号替换符号位
	 * 
	 * @param sb
	 */
	private void processSymbolTalbe(StringBuffer sb) {
		for (Symbol symbol : this.symbols) {
			sb.replace(symbol.start, symbol.end, this.getSymbolHolder(symbol));
		}
	}

	private String getSymbolHolder(Symbol symbol) {
		StringBuffer sb = new StringBuffer();
		for (int i = symbol.start; i < symbol.end; i++) {
			sb.append(SYS_SPECIFIC_SYMBOLS);
		}
		return sb.toString();
	}

	// 解析expr
	public void parse(String expr, Stack<Token> stTemp, Stack<Token> stOutput)
			throws Exception {
		List<Token> tokens = this.createTokens(this.preParse(expr));
		this.symbols.clear();// 可以清空符号表了
		stOutput.clear();
		stTemp.clear();
		for (Token token : tokens) {
			this.tk.treat(token, stTemp, stOutput);
		}
		while (!stTemp.isEmpty()) {
			stOutput.push(stTemp.pop());
		}
	}

	private List<Token> createTokens(List<Symbol> symbols) throws Exception {
		List<Token> tokens = new ArrayList<Token>();
		for (Symbol symbol : symbols) {
			tokens.add(this.createToken(symbol));
		}
		return tokens;
	}

	/**
	 * 创建Token
	 * 
	 * @param symbol
	 * @return
	 * @throws Exception
	 */
	private Token createToken(Symbol symbol) throws Exception {
		Token token = null;
		String valueString = symbol.value;
		TokenType tokenType = this.geTokenType(symbol.name);
		if (tokenType == TokenType.TOKEN_TYPE_STRING) {
			token = new Token(
					valueString.substring(1, valueString.length() - 1),
					tokenType);
		} else {
			token = new Token(valueString, tokenType);
		}

		return token;
	}

	TokenType geTokenType(String symbolString) {
		String t_s = symbolString.substring(0, 2);
		if ("$1".equals(t_s)) {
			return TokenType.TOKEN_TYPE_STRING;
		} else if ("$2".equals(t_s)) {
			return TokenType.TOKEN_TYPE_FUN;
		} else if ("$3".equals(t_s)) {
			return TokenType.TOKEN_TYPE_VAR;
		} else if ("$4".equals(t_s)) {
			return TokenType.TOKEN_TYPE_FLOAT;
		} else if ("$5".equals(t_s)) {
			return TokenType.TOKEN_TYPE_INT;
		} else if ("$6".equals(t_s)) {
			return TokenType.TOKEN_TYPE_OPE;
		} else if ("$0".equals(t_s)) {
			return TokenType.TOKEN_TYPE_OPE;
		} else if ("$7".equals(t_s)) {
			return TokenType.TOKEN_TYPE_CELL_REF;
		} else {
			return TokenType.TOKEN_TYPE_OTHER;
		}
	}

}
