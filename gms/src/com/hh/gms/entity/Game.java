package com.hh.gms.entity;

import java.util.Date;

public class Game {

	public static final String GameId = "GameId";
	public static final String GameName = "GameName";
	public static final String PlayUrl = "PlayUrl";
	public static final String Logo = "Logo";
	public static final String Imgs = "Imgs";
	public static final String CreatTime = "CreatTime";
	public static final String Power = "Power";
	public static final String Intro = "Intro";
	public static final String Mark = "Mark";
	public static final String VerifyStatus = "VerifyStatus";

	private int gameId;
	private String gameName;
	private String playUrl;
	private String logo;
	private String imgs;
	private Date creatTime;
	private int power;
	private String intro;
	private int mark;
	private int verifyStatus;
	
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public String getPlayUrl() {
		return playUrl;
	}
	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String imgs) {
		this.imgs = imgs;
	}
	public Date getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	public int getVerifyStatus() {
		return verifyStatus;
	}
	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}
	
	
	
	
}
