package org.toxbank.rest.user.alerts.notification;

import java.util.List;

import net.toxbank.client.resource.Alert;
import net.toxbank.client.resource.User;

public interface INotificationEngine {
	public boolean sendAlerts(User user, List<? extends Alert> alerts, String token) throws Exception;
}
