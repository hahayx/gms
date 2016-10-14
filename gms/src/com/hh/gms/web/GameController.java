package com.hh.gms.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hh.common.data.MapData;
import com.hh.common.utils.ServletUtil;
import com.hh.gms.entity.Game;
import com.hh.gms.service.GameService;

@Controller
@RequestMapping("/game")
public class GameController {

	@RequestMapping("/list")
	public ModelAndView list(HttpServletRequest req) throws Exception {
		MapData param = ServletUtil.getParams(req);
		List<Game> list = GameService.list(param);
		int count = GameService.count(param);
		return new ModelAndView("game/list").
				addAllObjects(param).
				addObject("list", list).
				addObject("totalCount", count);
	}
	
	@RequestMapping("/insertOrUpdate")
	public MapData insertOrUpdate(HttpServletRequest req) {
		MapData param = ServletUtil.getParams(req);
		return GameService.updateOrInsert(param);
	}
	
	@RequestMapping("/game")
	public static ModelAndView game(int gameId) {
		if (gameId == 0) {
			return new ModelAndView("game/add");
		}
		return new ModelAndView("game/game").addObject("game", GameService.getGame(gameId));
	}
	
}
