package com.hh.common.scheduled;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface RunAuto {
	String desc() default "描述";// 功能描述

	/**
	 * pattern:yyyy-MM-dd HH:mm:ss
	 */
	String beginTime() default "2012-6-15 05:30:00";

	/**
	 * 重复运行时候的周期
	 */
	long periodSecs() default 0L;

	/**
	 * 执行超时时间,超过这个时间会报警哦.单位是秒. 当periodSecs < timeoutSecs
	 * 时timeoutSecs以periodSecs为准
	 */
	long timeoutSecs() default 2 * 60 * 60L;

	/**
	 * job过期时间.为""表示永不过期.pattern:yyyy-MM-dd HH:mm:ss
	 */
	String expireTime() default "";

	/**
	 * 承载计划任务的serverId,为""表示此job不执行
	 */
	String runServerId() default "";
}
