package com.hh.gms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hh.common.data.MapData;
import com.hh.db.DbException;
import com.hh.db.util.Field;
import com.hh.gms.dao.GameDao;
import com.hh.gms.entity.Game;

public class GameService {

	private static Logger logger = LoggerFactory.getLogger(GameService.class);
	
	public static List<Game> list(MapData param) {
		int cid = param.getInt("cid",0);
		int offset = param.getInt("offset");
		int limit = param.getInt("limit", 10);
		List<Field> whereFields = new ArrayList<Field>();
		if (cid != 0) {
			whereFields.add(new Field(Game.Cid,"&",1<<cid));
		}
		param.set("cid", cid).set("offset", offset).set("limit", limit);
		try {
			return GameDao.selectList(whereFields, offset, limit);
		} catch (DbException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return Collections.emptyList();
		}
	}
	
	public static int count(MapData param) {
		int cid = param.getInt("cid",0);
		List<Field> whereFields = new ArrayList<Field>();
		if (cid != 0) {
			whereFields.add(new Field(Game.Cid,"&",1<<cid));
		}
		try {
			return GameDao.count(whereFields);
		} catch (DbException e) {
			logger.error(e.getMessage(), e);
			return 0;
		}
	}
	
	public static MapData updateOrInsert(MapData param) {
		try {
			Game game = new Game();
			game.setGameId(param.getInt("gameId"));
			game.setGameName(param.getString("gameName"));
			game.setLogo(param.getString("logo"));
			game.setIntro(param.getString("intro"));
			game.setImgs(param.getString("imgs"));
			game.setPlayUrl(param.getString("playUrl"));
			game.setMark(param.getInt("mark"));
			game.setPower(param.getInt("power"));
			game.setCid(param.getInt("cid"));
			if (game.getGameId() == 0) {
				game.setCreatTime(new Date());
				GameDao.insert(game);
			}else {
				GameDao.update(game);
			}
			return new MapData().set("code", 0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new MapData().set("code", -1).set("info", e.getMessage());
		}	
	}
	
	public static Game getGame(int gameId) {
		try {
			return GameDao.select(gameId);
		} catch (DbException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
}
