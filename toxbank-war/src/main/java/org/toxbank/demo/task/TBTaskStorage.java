package org.toxbank.demo.task;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.idea.restnet.aa.opensso.policy.CallablePolicyCreator;
import net.idea.restnet.aa.opensso.policy.PolicyProtectedTask;
import net.idea.restnet.c.task.TaskStorage;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.TaskResult;

public class TBTaskStorage extends TaskStorage<String> {
	protected ScheduledThreadPoolExecutor notificationTimer;
	protected TBNotifier notifier;
	
	public TBTaskStorage(String name, Logger log) {
		super(name, Logger.getLogger(TBTaskStorage.class.getName()));
		notifier = new TBNotifier();
		TimerTask notificationTasks  = new TimerTask() {
			
			@Override
			public void run() {
				try {
					String task = notifier.call();
					System.out.println(task);
					logger.log(Level.INFO, task);
				} catch (Exception x) {
					logger.log(Level.SEVERE, "Error launching notifications!", x);
				}
				
			}
		};
		notificationTimer = new ScheduledThreadPoolExecutor(1);
		notificationTimer.scheduleWithFixedDelay(notificationTasks, 1, 30,TimeUnit.SECONDS);
	}
	
	@Override
	public synchronized void shutdown(long timeout, TimeUnit unit)
			throws Exception {
		if (!notificationTimer.isShutdown()) {
			notificationTimer.awaitTermination(timeout, unit);
			notificationTimer.shutdown();
		}	
		super.shutdown(timeout, unit);
	}

	
	@Override
	protected Task<TaskResult, String> createTask(String user,ICallableTask callable) {
		
		return new PolicyProtectedTask(user,!(callable instanceof CallablePolicyCreator)) {
			@Override
			public synchronized void setPolicy() throws Exception {

				super.setPolicy();
			}
		};
	}
}
