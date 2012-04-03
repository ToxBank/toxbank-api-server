package org.toxbank.rest.user.alerts.notification;

import java.util.List;

import net.toxbank.client.resource.Alert;
import net.toxbank.client.resource.User;

public class SimpleNotificationEngine implements INotificationEngine {

	@Override
	public boolean sendAlerts(User user, List<? extends Alert> alerts) throws Exception {
		System.out.println(user.getUserName());
		for (Alert alert : alerts)
			System.out.println(alert);
		return true;
	}

}
