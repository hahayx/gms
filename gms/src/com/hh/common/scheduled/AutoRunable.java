package com.hh.common.scheduled;

/**
 * @author wulianhai 需要自动运行的方法需要继承这个类~
 */
public abstract class AutoRunable {
	public abstract void run() throws Exception;

	/**
	 * job初始化
	 */
	public void beforeRun() throws Exception {

	}

	/**
	 * job销毁
	 */
	public void beforeDestory() throws Exception {

	}
}
