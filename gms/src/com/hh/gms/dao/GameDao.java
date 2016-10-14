package com.hh.gms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hh.db.DbException;
import com.hh.db.ResultObjectBuilder;
import com.hh.db.util.DbUtil;
import com.hh.db.util.Field;
import com.hh.db.util.SqlInputData;
import com.hh.gms.entity.Game;

public class GameDao {

	private static String db = "gms";
	private static String table = "Game";
	
	public static ResultObjectBuilder<Game> builder = new ResultObjectBuilder<Game>() {

		@Override
		public Game build(ResultSet rs) throws SQLException {
			Game game = new Game();
			game.setGameId(rs.getInt(Game.GameId));
			game.setGameName(rs.getString(Game.GameName));
			game.setPlayUrl(rs.getString(Game.PlayUrl));
			game.setLogo(rs.getString(Game.Logo));
			game.setImgs(rs.getString(Game.Imgs));
			game.setCreatTime(rs.getDate(Game.CreatTime));
			game.setPower(rs.getInt(Game.Power));
			game.setCid(rs.getInt(Game.Cid));
			game.setIntro(rs.getString(Game.Intro));
			game.setVerifyStatus(rs.getInt(Game.VerifyStatus));
			return game;
		}
		
	};
	
	public static boolean insert(Game game) throws DbException {
		return DbUtil.getInstance(db).insert(new SqlInputData(table).
				addInsertField(Game.GameId, game.getGameId()).
				addInsertField(Game.GameName, game.getGameName()).
				addInsertField(Game.PlayUrl, game.getPlayUrl()).
				addInsertField(Game.Logo, game.getLogo()).
				addInsertField(Game.Imgs, game.getImgs()).
				addInsertField(Game.CreatTime, game.getCreatTime()).
				addInsertField(Game.Power, game.getPower()).
				addInsertField(Game.Mark, game.getMark()).
				addInsertField(Game.Cid, game.getCid()).
				addInsertField(Game.Intro, game.getIntro()).
				addInsertField(Game.VerifyStatus, game.getVerifyStatus())) > 0;
	}
	
	public static boolean update(Game game) throws DbException {
		List<Field> updateFields = new ArrayList<Field>();
		updateFields.add(new Field(Game.GameName, game.getGameName()));
		updateFields.add(new Field(Game.PlayUrl, game.getPlayUrl()));
		updateFields.add(new Field(Game.Logo, game.getLogo()));
		updateFields.add(new Field(Game.Imgs, game.getImgs()));
		updateFields.add(new Field(Game.Intro, game.getIntro()));
		updateFields.add(new Field(Game.Power, game.getPower()));
		updateFields.add(new Field(Game.Cid, game.getCid()));
		updateFields.add(new Field(Game.Mark, game.getMark()));
		updateFields.add(new Field(Game.VerifyStatus, game.getPower()));
		return update(game.getGameId(), updateFields);
	}
	
	public static boolean update(int gameId,List<Field> updateFields) throws DbException {
		return DbUtil.getInstance(db).update(new SqlInputData(table).
				addUpdateField(updateFields).
				addWhereField(Game.GameId, gameId)) >0;
	}
	
	public static Game select(int gameId) throws DbException {
		return DbUtil.getInstance(db).select(new SqlInputData(table).
				addWhereField(Game.GameId, gameId), builder);
	}
	
	public static List<Game> selectList(List<Field> whereFields,int offset,int limit) throws DbException {
		return DbUtil.getInstance(db).selectList(new SqlInputData(table,offset,limit).
				addOrderField(Game.Power,"desc").
				addWhereField(whereFields), builder);
	}
	
	public static int count(List<Field> whereFields) throws DbException {
		return DbUtil.getInstance(db).selectInt(new SqlInputData(table).addWhereField(whereFields));
	}
	
}
