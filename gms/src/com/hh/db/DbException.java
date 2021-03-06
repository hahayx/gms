package com.hh.db;

public class DbException extends Exception {


	private static final long serialVersionUID = 1L;

	public DbException() {
		super();
	}

	public DbException(String msg) {
		super(msg);
	}

	public DbException(String msg, Throwable t) {
		super(msg, t);
	}
}