package com.hh.gms.service;

import java.util.ArrayList;
import java.util.Collections;
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
		int type = param.getInt("type");
		int verifyStatus = param.getInt("verifyStatus");
		int offset = param.getInt("offset");
		int limit = param.getInt("limit", 30);
		List<Field> whereFields = new ArrayList<Field>();
		if (type != 0) {
			whereFields.add( new Field(Game.GameType, "&", 1<<type));
		}
		try {
			return GameDao.selectList(whereFields, offset, limit);
		} catch (DbException e) {
			return Collections.emptyList();
		}
	}
	
}
