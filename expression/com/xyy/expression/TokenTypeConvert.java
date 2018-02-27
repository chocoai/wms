package com.xyy.expression;

import com.xyy.util.typeConvert.ITypeConvert;
import com.xyy.util.typeConvert.TypeConvert;

/**
 * Token类型转换器
 * 
 * @author evan
 *
 */
public class TokenTypeConvert implements ITypeConvert {
	static {
		TypeConvert.registerTypeConvertor(TokenType.class,
				new TokenTypeConvert());
	}

	@Override
	public boolean canConvert(Class source, Class target) {
		if (target == TokenType.class) {
			if (source == String.class || source == target) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object convert(Object source, Object context, Object tag1,
			Object tag2) throws Exception {
		if (source instanceof TokenTypeConvert) {
			return source;
		}
		String temp = source.toString();
		if ("TOKEN_TYPE_CELL_REF".equals(temp)) {
			return TokenType.TOKEN_TYPE_CELL_REF;
		} else if ("TOKEN_TYPE_VAR".equals(temp)) {
			return TokenType.TOKEN_TYPE_VAR;
		} else if ("TOKEN_TYPE_FUN".equals(temp)) {
			return TokenType.TOKEN_TYPE_FUN;
		} else if ("TOKEN_TYPE_OPE".equals(temp)) {
			return TokenType.TOKEN_TYPE_OPE;
		} else if ("TOKEN_TYPE_INT".equals(temp)) {
			return TokenType.TOKEN_TYPE_INT;
		} else if ("TOKEN_TYPE_FLOAT".equals(temp)) {
			return TokenType.TOKEN_TYPE_FLOAT;
		} else if ("TOKEN_TYPE_STRING".equals(temp)) {
			return TokenType.TOKEN_TYPE_STRING;
		} else if ("TOKEN_TYPE_CHAR".equals(temp)) {
			return TokenType.TOKEN_TYPE_CHAR;
		} else if ("TOKEN_TYPE_EXPR".equals(temp)) {
			return TokenType.TOKEN_TYPE_EXPR;
		} else if ("TOKEN_TYPE_OTHER".equals(temp)) {
			return TokenType.TOKEN_TYPE_OTHER;
		} else {
			throw new Exception("type convert fail.");
		}

	}

	@Override
	public String convert2Str(Object target) throws Exception {
		if (target != null) {
			return target.toString();
		} else {
			throw new Exception("target null Exception");
		}
	}
	
	
	

}
