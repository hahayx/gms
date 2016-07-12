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
		int verifyStatus = param.getInt("verifyStatus",0);
		int offset = param.getInt("offset");
		int limit = param.getInt("limit", 30);
		List<Field> whereFields = new ArrayList<Field>();
		if (verifyStatus != -1) {
			whereFields.add(new Field(Game.VerifyStatus, verifyStatus));
		}
		try {
			return GameDao.selectList(whereFields, offset, limit);
		} catch (DbException e) {
			logger.error(e.getMessage(), e);
			return Collections.emptyList();
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
			game.setCreatTime(new Date());
			if (game.getGameId() == 0) {
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
