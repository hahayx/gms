package com.hh.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class DateUtils {

	public static final String[] DatePatterns = { "yyyy", "yyyy-MM", "yyyy-MM-dd", "yyyy-MM-dd HH",
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS" };

	public static final String DefaultDatePattern = "yyyy-MM-dd HH:mm:ss.SSS";

	private static final ThreadLocal<SimpleDateFormat> sf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		};
	};
	private static final ThreadLocal<SimpleDateFormat> yf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		};
	};
	private static final ThreadLocal<SimpleDateFormat> tf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm:ss");
		};
	};
	

	/**
	 * 一天为1000 * 60 * 60 * 24毫秒.
	 */
	public static final long DAY_MILLISECOND = 1000 * 60 * 60 * 24;

	public static String formatDateTime(Date date) {
		if (date == null) {
			return "";
		} else {
			return sf.get().format(date);
		}
	}

	/**
	 * 将yyyy-MM-dd HH:mm:ss的反解析成date
	 * 
	 * @throws ParseException
	 *             解析失败
	 */
	public static Date parseDateFormated(String formatedDate) {
		try {
			return sf.get().parse(formatedDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 将yyyy-MM-dd格式的解析成date
	 */
	public static Date parseDateYDM(String formatedDate) {
		try {
			return yf.get().parse(formatedDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 返回yyyy-MM-dd
	 */
	public static String formatDateYDM(Date dt) {
		return yf.get().format(dt);
	}
	
	/**
	 * 返回HH:mm:ss
	 */
	public static String formatDateHMS(Date dt) {
		return tf.get().format(dt);
	}

	/**
	 * 获取开始日期结束日期之间的天数.
	 * <p>
	 * 注意：计算之前会先截去日期的时间部分
	 * <p>
	 * 例如：计算 2008.2.3-23:59:59 与 2008.2.4-00:00:00 之间的天数, 则先将起止日期处理为 2008.2.3 与
	 * 2008.2.4 ,他们之间相差一天,所以最后返回的结果为1.
	 * 
	 * @param startDate
	 *            开始日期.
	 * @param endDate
	 *            结束日期.
	 * @return 开始日期结束日期之间的天数.
	 * 
	 * @author zhangqi
	 */
	public static long daysBetweenDates(Date startDate, Date endDate) {
		if (null == startDate || null == endDate) {
			return 0;
		}
		/*
		 * 不能直接用时间相减再除,因为夏令时会让有些天只有23小时有些25小时导致错误。
		 * 所以先把日期转换成字符串,再生成那个字符串的GMT时间,再用GMT去计算天数. GMT没有夏令时.
		 */
		String startDateString = yf.get().format(getFirstTimeInDay(startDate));
		String endDateString = yf.get().format(getFirstTimeInDay(endDate));
		DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar gmtCal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
		gmtFormat.setCalendar(gmtCal);
		try {
			Date sDate = gmtFormat.parse(startDateString);
			Date eDate = gmtFormat.parse(endDateString);
			return (eDate.getTime() - sDate.getTime()) / DAY_MILLISECOND;
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 获取date的最开始时间,即00时00分00秒000毫秒.
	 * 
	 * @param date
	 *            Date日期.
	 * @return 获取date的最开始时间.
	 * 
	 * @author zhangqi
	 */
	public static Date getFirstTimeInDay(Date date) {
		if (null == date) {
			return null;
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}
	
	public static Date getNDaysBefore(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -n);
		setBeginingOfDate(calendar);
		return calendar.getTime();
	}
	
	private static void setBeginingOfDate(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
	
	private static Date beginDate = parseDateYDM("2012-06-01"); //传说平台大概那时上线，当做开创时间吧。
	
	public static int getDayIndex() {
		return getDayIndex(new Date());
	}
	
	public static int getDayIndex(Date date) {
		return (int) daysBetweenDates(beginDate, date);
	}
	
}
