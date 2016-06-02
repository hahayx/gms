package com.hh.db.util;

import java.util.List;


public class Field implements Cloneable{
	
	private String name;
	private Object value;
	private String operation;
	public Field(String name,String operation, Object value) {
		this.name = name;
		this.operation=operation;
		this.value = value;
	}
	public Field(String name, Object value) {
		this(name, "=", value);
	}
	public Field(String name) {
		this.name = name;
	}
	public String getOperation() {
		return operation;
	}
	public Field setOperation(String operation) {
		this.operation = operation;
		return this;
	}
	
	public String getName() {
		return name;
	}

	public Field setName(String name) {
		this.name = name;
		return this;
	}
	public Field setInOperValue(List<? extends Object> value) {
		this.operation = "in";
		this.value=value;
		return this;
	}
	public Field setLikeOperValue(String value) {
		this.operation = "like";
		this.value=value;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public Field setValue(Object value) {
		this.value = value;
		this.operation="=";
		return this;
	}
	public String toString() {
		return "name="+name+";value="+value+";oper="+operation;
	}
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
