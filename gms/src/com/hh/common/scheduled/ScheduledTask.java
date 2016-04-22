package com.hh.common.scheduled;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public abstract class ScheduledTask implements Runnable {
	protected int month = -1;
	protected int day = -1;
	protected int hour = -1;
	protected int minute = -1;
	protected int second = -1;
	protected long interval = 0;
	protected int year;
	//是否为重复任务
	private boolean isRepeat = false;
	
	//something define
	private final static int MiSecond = 1;
	private final static int MiSecondInSecond = 1000;
	private final static int MiSecondInMinute = MiSecondInSecond*60;
	private final static int MiSecondInHour = MiSecondInMinute*60;
	private final static int MiSecondInDay = MiSecondInHour*24;
	
	private static HashMap<TimeUnit, Integer> timeValueMap = new HashMap<TimeUnit, Integer>();
	static{
		timeValueMap.put(TimeUnit.MILLISECONDS, MiSecond);
		timeValueMap.put(TimeUnit.SECONDS,MiSecondInSecond);
		timeValueMap.put(TimeUnit.MINUTES,MiSecondInMinute);
		timeValueMap.put(TimeUnit.HOURS,MiSecondInHour);
		timeValueMap.put(TimeUnit.DAYS,MiSecondInDay);
	}
	
	/**
	 * 构造一个不重复运行的定时任务
	 * @param _month 1~12
	 * @param _day 1~31
	 * @param _hour 0~23
	 * @param _minute 0~59
	 * @param _second 0~59
	 */
	public ScheduledTask(int _year,int _month,int _day,int _hour,int _minute,int _second){
		setYear(_year);
		setMonth(_month);
		setDay(_day);
		setHour(_hour);
		setMinute(_minute);
		setSecond(_second);
	}
	
	/**
	 * 根据字符串来确定开始时间，格式如下
	 * year-month-day hh:minute:second
	 * 其中second部分为可选部分
	 * @param dateString
	 */
	public ScheduledTask(String dateString){
		initTime(dateString);
	}
	
	private void initTime(String dateString){
		String[] dateType = dateString.split(" ");
		if(dateType.length!=2){
			throw new IllegalArgumentException("dateString illegal");
		}
		String[] datePart = dateType[0].split("-");
		String[] timePart = dateType[1].split(":");
		if(datePart.length!=3){
			throw new IllegalArgumentException("datePart of dateString illegal");
		}
		if(timePart.length <2){
			throw new IllegalArgumentException("timePart of dateString illegal");
		}else if(timePart.length ==3){
			setSecond(Integer.valueOf(timePart[2]));
		}
		setYear(Integer.valueOf(datePart[0]));
		//月份时间为0-11
		setMonth(Integer.valueOf(datePart[1]));
		setDay(Integer.valueOf(datePart[2]));
		setHour(Integer.valueOf(timePart[0]));
		setMinute(Integer.valueOf(timePart[1]));
	}
	
	/**
	 * 构造一个不重复运行的定时任务
	 * @param _month 1~12
	 * @param _day 1~31
	 * @param _hour 0~23
	 * @param _minute 0~59
	 */
	public ScheduledTask(int _year,int _month,int _day,int _hour,int _minute){
		setYear(_year);
		setMonth(_month);
		setDay(_day);
		setHour(_hour);
		setMinute(_minute);
		setSecond(0);
	}

	/**
	 * 调用该方法把任务设定为重复任务
	 * @param value
	 * @param timeUnit
	 */
	
	public void setRepeatTime(long value,TimeUnit timeUnit){
		Integer timeValue = timeValueMap.get(timeUnit);
		if(timeValue == null){
			//throw a new exception
		}
		//check value >0
		this.interval = value*timeValue;
		this.isRepeat = true;
	}
	
	private void setYear(int _year){
		year = _year;
	}
	
	private void setMonth(int _month){
		month = _month -1;
	}
	
	private void setDay(int _day){
		this.day = _day;
	}
	
	private void setHour(int _hour){
		this.hour = _hour;
	}
	
	private void setMinute(int _minute){
		this.minute = _minute;
	}
	
	private void setSecond(int _second){
		this.second = _second;
	}
	
	
	/**
	 * 可以将重复任务设为非重复任务，但是反过来，必须先设定任务的重复间隔
	 * @param _isRepeat
	 */
	public void setIsRepeat(boolean _isRepeat) {
		if(_isRepeat && interval>0){
			this.isRepeat = _isRepeat;
		}else if(_isRepeat == false){
			this.isRepeat = _isRepeat;
		}
	}
	
	/**
	 * 获取该任务的开始时间，精确到分钟
	 * @return
	 */
	public Calendar getStartTime(){
		Calendar startTime = Calendar.getInstance();
		startTime.set(Calendar.YEAR, this.year);
		startTime.set(Calendar.MONTH, this.month);
		startTime.set(Calendar.DAY_OF_MONTH, this.day);
		startTime.set(Calendar.HOUR_OF_DAY,this.hour);
		startTime.set(Calendar.MINUTE,this.minute);
		startTime.set(Calendar.SECOND,this.second);
		return startTime;
	}
	
	/**
	 * 获取该任务的时间间隔
	 * @return
	 * 返回值=0,表示非重复任务
	 */
	public long getInterval(){
		if(!isRepeat){
			return 0;
		}
		return interval;
	}
}
