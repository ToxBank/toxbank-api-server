package org.toxbank.demo.task;

import java.util.logging.Logger;

import net.idea.restnet.aa.opensso.policy.CallablePolicyCreator;
import net.idea.restnet.aa.opensso.policy.PolicyProtectedTask;
import net.idea.restnet.c.task.TaskStorage;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.TaskResult;

public class TBTaskStorage extends TaskStorage<String> {

	public TBTaskStorage(String name, Logger logger) {
		super(name, logger);
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
