package com.hh.common.scheduled;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.*;



public class ScheduledService {
	
	private long timeOut = -1;
	
	private ScheduledExecutorService service;
	
	public ScheduledService(int threadNum){
		service = Executors.newScheduledThreadPool(threadNum);
	}
	
	/**
	 * 批量注量任务
	 * @param taskList
	 * @return
	 * 返回定期任务列表
	 */
	
	public List<ScheduledFuture<?>> registerTask(List<ScheduledTask> taskList){
		if(taskList == null){
			throw new NullPointerException("taskList is null");
		}
		List<ScheduledFuture<?>> secheduledFutureList = new ArrayList<ScheduledFuture<?>>();
		for(ScheduledTask task:taskList){
			secheduledFutureList.add(registerTask(task));
		}
		return secheduledFutureList;
	}
	
	/**
	 * 注册单个定期任务
	 * @param task
	 * @return
	 * 如果添加不成功，会返回一个null对象
	 */
	public ScheduledFuture<?> registerTask(ScheduledTask task){
		if(task == null){
			throw new NullPointerException("task is null");
		}
		ScheduledFuture<?> scheduleFuture;
		//重复任务,与非重复任务添加方法不一样
		if(task.getInterval()>0){
			scheduleFuture = addRepeatTask(task);
		}else{
			scheduleFuture =addNonRepeatTask(task);
		}
		return scheduleFuture;
	}
	
	private ScheduledFuture<?> addRepeatTask(ScheduledTask task){
		Calendar taskStartTime = task.getStartTime();
		long delay = taskStartTime.getTime().getTime() - System.currentTimeMillis();
		long interval = task.getInterval();
		ScheduledFuture<?> scheduleFuture;
		if(delay<0){
			double temDelay = Math.abs(delay);
			double temInterval = Math.abs(interval);
			int circle =(int)Math.ceil(temDelay/temInterval);
			delay = delay + circle*interval;
		}
		scheduleFuture = service.scheduleAtFixedRate((Runnable)task,delay, interval,TimeUnit.MILLISECONDS); 
		return scheduleFuture;
	}
	
	private ScheduledFuture<?> addNonRepeatTask(ScheduledTask task){
		Calendar taskStartTime = task.getStartTime();
		long delay = taskStartTime.getTime().getTime() - System.currentTimeMillis();
		//如果delay<0，会直接执行，增加不确定性，因此不注册该任务
		System.out.println("non repeat " + delay);
		if(delay >=0){
			return service.schedule((Runnable)task, delay, TimeUnit.MILLISECONDS);
		}
		//service.
		return null;
	}
	
	//目前考虑的是把停任务的操作抛给使用者，而不是由service来完成
//	public boolean cancleTask(){
//		//目前没有考虑好到底是由service停任务还是把scheduledFuture返回给调用者，由调用者去停任务
//		return false;
//	}
	
	/**
	 * 当调用此方法后，不会再提供注册服务
	 * 在执行的任务未执行完之前，一直阻塞线程
	 * 默认超时时间为1分钟
	 * @throws InterruptedException 
	 */
	public void shutDown() throws InterruptedException{
		//try to cancle all the repeat task
		//关于这个shutDown，不会停止当前执行的任务
		//不考虑突然进程被杀的情况
		if(service.isShutdown()){
			return;
		}
		service.shutdown();
		service.awaitTermination(timeOut, TimeUnit.MINUTES);
	}
	
	public void shutDown(int timeout,TimeUnit timeUnit) throws InterruptedException, ScheduledException{
		if(service.isShutdown()){
			return;
		}
		if(timeout<0){
			throw new ScheduledException("timeout value smaller than 0");
		}else{
			service.shutdown();
			service.awaitTermination(timeout,timeUnit);
		}
	}
	
}
