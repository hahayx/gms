package com.hh.db.util;


/**
 * 表达式field
 * @author huangyongsheng
 *
 */
public class ExpressionField extends Field{
	
	private String exp;
	public ExpressionField(String exp) {
		super(null);
		this.exp=exp;
	}
	public String getExp() {
		return exp;
	}
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
