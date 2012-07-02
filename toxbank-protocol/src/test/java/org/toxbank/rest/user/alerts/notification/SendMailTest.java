package org.toxbank.rest.user.alerts.notification;


public class SendMailTest {
	private static final String configFile = "conf/tbalert.pref";
	
	/**
	 * @throws Exception
	 */
	public static void main(String[] args) {
		try {
			DefaultAlertNotificationUtility utility = new DefaultAlertNotificationUtility(configFile);
			utility.sendNotification(args.length>0?args[0]:"valid@email.com", "test", "test", "text/html");
		} catch (Exception x) {
			x.printStackTrace();
		}

	}
}
