package com.hh.gms.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller

public class GmsController {

	@RequestMapping(value={"/index.html","/"})
	public ModelAndView index() throws Exception {
		return new ModelAndView("index");
	}
}
