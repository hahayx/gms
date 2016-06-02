package com.hh.gms.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hh.db.DbException;
import com.hh.db.DbManager;

@Controller

public class GmsController {

	@RequestMapping("/index.html")
	public ModelAndView index() throws Exception {
		DbManager db = DbManager.getDbManager("gms");
		String str = db.executeScalarString("select GameName from Game where GameId = 1");
		System.out.println(str);
		return new ModelAndView("index");
	}
}
