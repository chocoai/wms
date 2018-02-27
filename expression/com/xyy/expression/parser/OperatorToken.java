package com.xyy.expression.parser;

import java.util.Stack;

import com.xyy.expression.Operator;
import com.xyy.expression.Token;
import com.xyy.expression.TokenType;
import com.xyy.expression.TreatToken;

/**
 * 操作符解析器
 * 
 * @author evan
 * 
 */
public class OperatorToken extends TreatToken {
	private Operator ope;

	public OperatorToken(Parser context, Operator ope) {
		super(context);
		this.ope = ope;
	}

	@Override
	public void treat(Token token, Stack<Token> stTemp, Stack<Token> stOutput)
			throws Exception {
		if (token.getTokenType() == TokenType.TOKEN_TYPE_OPE) {
			this.processOperatorToken(token, stTemp, stOutput);
		} else {
			this.next.treat(token, stTemp, stOutput);
		}
	}

	private void processOperatorToken(Token token, Stack<Token> stTemp,
			Stack<Token> stOutput) throws Exception {
		if (token.getTokenStr().equals(this.ope.getOpe())) {
			if (stTemp.isEmpty() || token.getTokenStr().equals("(")) {
				stTemp.push(token);
			} else {
				Token top_token = stTemp.peek();
				Operator o = top_token.getOperator();
				assert o != null;
				while (o.getLevel() >= ope.getLevel()
						|| ope.getOpe().equals(")") || ope.getOpe().equals(",")) {
					Token t1 = stTemp.pop();
					if (t1.getTokenStr().equals("(")) {
						if (this.ope.getOpe().equals(",")) {

							stTemp.push(t1);
							stOutput.push(token);
						} else if (this.ope.getOpe().equals(")")) {
							if (!stTemp.isEmpty()
									&& stTemp.peek().getTokenType() == TokenType.TOKEN_TYPE_FUN) {

								stOutput.push(new Token("rem end_args",
										TokenType.TOKEN_TYPE_REM));
								stOutput.push(stTemp.pop());
							}
						}
						break;
					}
					stOutput.push(t1);
					if (stTemp.isEmpty())
						break;
					o = stTemp.peek().getOperator();
					if (o == null) {
						throw new Exception("operator map to token error.");
					}
				}

				if (!this.ope.getOpe().equals(")")
						&& !this.ope.getOpe().equals(",")) {
					stTemp.push(token);
				}
			}

		} else {
			this.next.treat(token, stTemp, stOutput);
		}
	}
}
