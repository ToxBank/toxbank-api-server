package org.toxbank.rest.user.alerts.notification;

import java.net.URL;
import java.util.List;

import net.toxbank.client.resource.*;

/**
 * Interface to something that can send submissions
 */
public interface AlertNotificationUtility {
  /**
   * @param toEmail the user to be notified
   * @param subject the subject of the notification
   * @param content the content
   * @param mimeType the mime type of the content
   */
  public void sendNotification(
      String toEmail, 
      String subject,
      Object content,
      String mimeType) throws Exception;
  
  /**
   * Queries the search service
   * @param paramString the parameter string to pass to the search service
   * @param ssoToken an sso token to use when connecting to the search service
   * @return list of urls matching the query
   */
  public List<URL> querySearchService(String paramString, String ssoToken) throws Exception;
  
  /**
   * Retrieves the list of resources matching the given urls
   * @param urls the list of urls of toxbank resources
   * @return list of resources matching the urls
   */
  public List<AbstractToxBankResource> getResources(List<URL> urls) throws Exception;
  
  /**
   * Gets the url to access the protocol through the UI
   * @param protocol the protocol
   * @return a url which can be used to view the protocol through the UI - empty string
   * if none
   */
  public String getUIUrl(Protocol protocol);
  
  /**
   * Gets the url to access the investigatino through the UI
   * @param investigation the investigation
   * @return a url which can be used to view the investigation through the UI - empty string
   * if none
   */
  public String getUIUrl(Investigation investigation);
}
