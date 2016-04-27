package com.hh.db;

public class DbConnectionInfo {
	public String name;	
	public boolean enabled;
		
	public String driver;
	public String connectionString;
	public int poolMin;
	public int poolMax;
	
	public boolean testConnectionOnCheckin;
	
	@Override
	public String toString() {
		return connectionString;
	}
}
