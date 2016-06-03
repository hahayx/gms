package com.hh.gms.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hh.common.data.MapData;
import com.hh.common.utils.ServletUtil;
import com.hh.gms.service.GameService;

@Controller("/game")
public class GameController {

	@RequestMapping("/list")
	public ModelAndView list(HttpServletRequest req) throws Exception {
		MapData param = ServletUtil.getParams(req);
		return new ModelAndView("game/list").addObject("list", GameService.list(param));
	}
	
}
