package com.hh.common.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hh.db.DbManager;

public class GmsListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			DbManager.init("/tmp/config.xml");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
