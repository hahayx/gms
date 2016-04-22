package com.hh.common.scheduled;

public class ScheduledException extends RuntimeException {
	private static final long serialVersionUID = 6122546020178310780L;
	
	public ScheduledException(){
		super();
	}
	public ScheduledException(String msg){
		super(msg);
	}
	public ScheduledException(String msg,Throwable t){
		super(msg,t);
	}
}
