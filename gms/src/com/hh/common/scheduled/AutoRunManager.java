package com.hh.common.scheduled;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.hh.common.utils.DateUtils;


public class AutoRunManager {

	private List<AutoRunable> runableList;
	private ScheduledService service;
	public static final String RUNAUTO_ON_ALL_SERVER_SERVERID = "*";

	public AutoRunManager(String packageName) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IOException {
		if (packageName == null) {
			this.runableList = new ArrayList<AutoRunable>();
			return;
		}
		runableList = this.getAutoRunableObj(this.getPackageClassNameList(packageName));
		service = new ScheduledService(runableList.size());
	}

	/**
	 * 根据.class类名(不包含'.class'字符串),构造出所有继承了@{AutoRunable}的类
	 * 
	 * @throws 类没有找到时候抛出
	 *             ClassNotFoundException
	 * @throws 有
	 * @{RunAuto 注解,没有实现@{AutoRunable}接口的class时，会抛出IlleagAccessError
	 */
	private List<AutoRunable> getAutoRunableObj(List<String> classNameList) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		List<AutoRunable> runableList = new ArrayList<AutoRunable>();

		// 判断类是否有RunAuto注解
		for (String s : classNameList) {
			Class<?> cs = Class.forName(s);
			Annotation an = cs.getAnnotation(RunAuto.class);
			if (an != null) {
				Object obj = cs.newInstance();
				if (obj instanceof AutoRunable) {
					runableList.add(AutoRunable.class.cast(obj));
				} else {
					// 注解加错误了.
					throw new IllegalAccessError(s + "没有继承AutoRunable,不能RunAuto");
				}
			} else {
				// 不是RunAuto注解的
			}
		}
		return runableList;
	}

	// 获取packageName下的所有.class文件名
	private List<String> getPackageClassNameList(String packageName) throws IOException {
		Enumeration<URL> cls = this.getClass().getClassLoader().getResources(packageName.replace(".", "/"));
		List<File> classDirectoryFileList = new ArrayList<File>();
		while (cls.hasMoreElements()) {
			classDirectoryFileList.add(new File(cls.nextElement().getFile()));
		}
		List<String> classNameList = new ArrayList<String>();

		for (File direct : classDirectoryFileList) {
			if (direct.isDirectory()) {
				// 获取出所有.class文件
				for (File f : direct.listFiles(new FileFilter() {
					@Override
					public boolean accept(File dir) {
						return dir.getName().endsWith(".class");
					}
				})) {
					if (f.getName().endsWith(".class")) {
						classNameList.add(packageName + "."
								+ f.getName().substring(0, f.getName().length() - 6));
					} else {
						// do nothing
					}
				}
			} else {
				// do nothing
				// 获取出来的不是目录是不可能的
			}
		}
		return classNameList;
	}

	private void afterRun() throws Exception {
		for (AutoRunable obj : this.runableList) {
			obj.beforeDestory();
		}
	}

	public void run() throws Exception {

		for (final AutoRunable obj : this.runableList) {
			// 获取时间
			final RunAuto ra = obj.getClass().getAnnotation(RunAuto.class);
			String beginTime = null;
			long periodSecs = 0;
			boolean repeat = false;
			TimeUnit timeunit = TimeUnit.SECONDS;
			boolean checkServerId = false;// 判断是否是当前的服务器作为计划任务的承载

			// 通过注解进行参数指定的,若同时实现了@{ScheduledAble}接口,则接口会被忽略
			beginTime = ra.beginTime();
			if (beginTime == null) {
				throw new IllegalAccessError(obj.getClass().getName() + " beginTime is null");
			}

			periodSecs = ra.periodSecs();
			/*
			if (!checkServerId) {
				// 当前server不是承载计划任务的server
				continue;
			}*/
			if (isExpired(ra.expireTime())) {
				continue;
			}

			// 调用初始化
			obj.beforeRun();

			// 注册任务
			ScheduledTask task = new ScheduledTask(beginTime) {
				@Override
				public void run() {
					if (isExpired(ra.expireTime())) {
						return;
					}

					long begin = System.currentTimeMillis();
					long periodSecs = ra.periodSecs();
					long timeoutSecs = periodSecs > ra.timeoutSecs() ? ra.timeoutSecs() : periodSecs;
					try {
						obj.run();
						long end = System.currentTimeMillis();
						long executeTime = (end - begin) / 1000;// 换算为秒

						
					} catch (Throwable e) {
						
						e.printStackTrace();
						
					}
				}

				private int incrReportCountAndGet(String key) {
					AtomicInteger oldReportCount = reportCountMap.get(key);
					if (oldReportCount == null) {
						if (null == (oldReportCount = reportCountMap.putIfAbsent(key, new AtomicInteger(1)))) {
							return 1;
						}
					}
					return oldReportCount.incrementAndGet();
				}

				private final ConcurrentHashMap<String, AtomicInteger> reportCountMap = new ConcurrentHashMap<String, AtomicInteger>();

		
			};
			task.setIsRepeat(repeat);
			task.setRepeatTime(periodSecs, timeunit);
			if (this.service != null) {
				service.registerTask(task);
			}
		}
	}

	private boolean isExpired(String expireTimeStr) {
		
		if (StringUtils.isNotBlank(expireTimeStr)) {
			return DateUtils.parseDateFormated(expireTimeStr).before(new Date());
		}
		return false;
	}

	public void stop() throws Exception {
		this.afterRun();
		if (service != null) {
			service.shutDown();
		}
	}

	

}
