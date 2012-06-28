package org.toxbank.rest.user.alerts.notification;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.toxbank.client.resource.*;

/**
 * Handles the querying of alerts and notification of users via email when an
 * alert has been activated.
 */
@SuppressWarnings("rawtypes")
public class SimpleNotificationEngine implements INotificationEngine {
  public static final String notificationSubject = "ToxBank Alert Updates";
  
  private static final String EMAIL_SERVICE_NAME = "mailto";
  private static final String configFile = "conf/tbalert.pref";
      
  private AlertNotificationUtility utility;
  
  /**
   * Uses the default configuration from the config file tbalert.pref
   */
  public SimpleNotificationEngine() {
    this(new DefaultAlertNotificationUtility(configFile));
  }
  
  /**
   * @param utility implements the dependencies needed by the engine
   */
  public SimpleNotificationEngine(AlertNotificationUtility utility) {
    this.utility = utility;
  }
    
  /**
   * @return the resource path to the configuration file
   */
  public String getConfigFile() {
    return configFile;
  }
  
	/**
	 * @param user the user potentially being alerted
	 * @param alerts the list of relevant alerts
	 * @param token a opensso security token that allows access to various services
	 * @return ? 
	 */
  @Override
  public boolean sendAlerts(User user, List<? extends Alert> alerts, String token) throws Exception {
    String email = getEmail(user);
    if (email != null) {
      List<AlertResult> results = new ArrayList<AlertResult>();
      for (Alert alert : alerts) {
        AlertResult result = queryAlert(user, alert, token);
        if (result != null) {
          results.add(result);
        }
      }
      
      if (results.size() > 0) {
        sendNotification(email, results, token);		  
      }
    }
    
    return true;
  }
  
  private static class AlertResult {
    public final Alert alert;
    public final List<URL> matchingUrls;
    public AlertResult(Alert alert, List<URL> matchingUrls) {
      this.alert = alert;
      this.matchingUrls = matchingUrls;
    }
  }

  private AlertResult queryAlert(User user, Alert alert, String token) throws Exception {
    List<URL> urls = new ArrayList<URL>();
    switch (alert.getQuery().getType()) {
    case FREETEXT:
      String paramString = createParamString(alert);
      urls = utility.querySearchService(paramString, token);
      break;
    case SPARQL:
      // not supported yet
      break;
    default:
      throw new IllegalArgumentException("Unsupported alert type: " + alert.getQuery().getType());
    }
    
    if (urls.size() > 0) {
      return new AlertResult(alert, urls);
    }
    else {
      return null;
    }
  }

  private String createParamString(Alert alert) {
    StringBuilder sb = new StringBuilder();
    sb.append(alert.getQueryString());
    if (alert.getSentAt() > 0) {
      if (alert.getQueryString().indexOf('?') >= 0) {
        sb.append("&");
      }
      else {
        sb.append("?");
      }
      sb.append("timeModified=");
      sb.append(alert.getSentAt());
    }
    return sb.toString();
  }
  
  private String getEmail(User user) {
    for (Account account : user.getAccounts()) {
      if (EMAIL_SERVICE_NAME.equals(account.getService())) {
        return account.getAccountName();
      }
    }
    return null;
  }
      
  private void sendNotification(String userEmail, List<AlertResult> results, String token) throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>\n");
    appendStyle(sb);
    sb.append("<body>\n");
    appendIntro(sb);
    for (AlertResult result : results) {
      sb.append("<div class='alert_result'>\n");
      appendSummary(sb, result.alert);
      List<AbstractToxBankResource> resources = utility.getResources(result.matchingUrls, token);
      for (AbstractToxBankResource resource : resources) {
        if (resource instanceof Protocol) {
          appendSummary(sb, (Protocol)resource);          
        }
        else if (resource instanceof Investigation) {
          appendSummary(sb, (Investigation)resource);
        }
        else {
          throw new RuntimeException("Unsupported resource type: " + resource.getClass());
        }
      }
      
      sb.append("</div>\n");
    }
    sb.append("</body>\n");
    sb.append("</html>\n");

    utility.sendNotification(userEmail, notificationSubject, sb.toString(), "text/html");
  }

  private void appendStyle(StringBuilder sb) {
    sb.append("<style>\n");
    
    sb.append("html {\n");
    sb.append("  font-family: arial;\n");
    sb.append("  font-size: 14px;\n");
    sb.append("}\n");
    
    sb.append("a:visited, a:link, a:hover {\n");
    sb.append("  color: #333399;\n");
    sb.append("  text-decoration: none;\n");
    sb.append("  font-weight: bold;\n");
    sb.append("}\n");

    sb.append("a:hover {\n");
    sb.append("  color: #8d8dc1;\n");
    sb.append("}\n");
    
    sb.append(".alert_result {\n");
    sb.append("  width: 550px;\n");
    sb.append("  margin: 5px;\n");
    sb.append("  padding: 10px;\n");
    sb.append("  border: 1px solid #eaeaea;\n");
    sb.append("}\n");
    
    sb.append(".summary {\n");
    sb.append("  margin: 5px;\n");
    sb.append("  padding: 10px;\n");
    sb.append("  border: 1px solid #eaeaea;\n");
    sb.append("  font-size: 13px;\n");
    sb.append("  width: 540px;\n");
    sb.append("}\n");
    
    sb.append(".alert_summary {\n");
    sb.append("  background-color: #ebebff;\n");
    sb.append("}\n");

    sb.append(".protocol_summary {\n");
    sb.append("  background-color: #e6f3f4;\n");
    sb.append("}\n");

    sb.append(".investigation_summary {\n");
    sb.append("  background-color: #f8f8f8\n");
    sb.append("}\n");

    sb.append(".label {\n");
    sb.append("  font-weight: bold;\n");
    sb.append("  padding-right: 8px;\n");
    sb.append("  width: 80px;\n");
    sb.append("}\n");
    
    sb.append(".summary td {\n");
    sb.append("  padding-bottom: 6px;\n");
    sb.append("  font-size: 13px;\n");
    sb.append("}\n");
    
    sb.append("</style>\n");
  }
  
  private void appendIntro(StringBuilder sb) {
    sb.append("<div class='intro'>");
    sb.append("Some updates have been made to the ToxBank data warehouse which match your email alert criteria.");
    sb.append("</div>\n");
  }
  
  private void appendTableValue(StringBuilder sb, String value) {
    sb.append("    <tr>\n");
    sb.append("      <td colspan='2' class='value'>");
    sb.append(value);
    sb.append("</div>\n");
  }
  
  private void appendTableValue(StringBuilder sb, String label, String value) {
    sb.append("    <tr>\n");
    sb.append("      <td class='label'>");
    sb.append(label);
    sb.append("</td>\n");
    sb.append("      <td class='value'>");
    sb.append(value);
    sb.append("</td>\n");
    sb.append("    </tr>\n");
  }

  private void appendTableValue(StringBuilder sb, String label, String link, String text) {
    sb.append("    <tr>\n");
    sb.append("      <td class='label'>");
    sb.append(label);
    sb.append("</td>\n");
    sb.append("      <td class='value'>\n");
    sb.append("        <a href='");
    sb.append(link);
    sb.append("'>");
    sb.append(text);
    sb.append("</a>");
    sb.append("      </td>\n");
    sb.append("    </tr>\n");
  }

  private void appendSummary(StringBuilder sb, Alert alert) {
    sb.append("<div class='summary alert_summary'>\n");
    sb.append("  <table class='summary_table'>\n");
    appendTableValue(sb, "Alert Title:", alert.getTitle());
    appendTableValue(sb, "Alert Query:", alert.getQueryString());
    sb.append("  </table>\n");
    sb.append("</div>\n");
  }

  private void appendSummary(StringBuilder sb, Protocol protocol) {
    sb.append("<div class='summary protocol_summary'>\n");
    sb.append("  <table class='summary_table'>\n");
    appendTableValue(sb, "Protocol:", utility.getUIUrl(protocol).toString(), protocol.getTitle());
    appendTableValue(sb, "Protocol ID:", 
        String.format("%s (version %d)", protocol.getIdentifier(), protocol.getVersion()));
    
    if (protocol.getAuthors().size() > 0) {
      String authorsString = shortName(protocol.getAuthors().get(0));
      if (protocol.getAuthors().size() > 1)  {
        authorsString += " et al";
      }
      appendTableValue(sb, "Authors:", authorsString);
    }
    
    appendTableValue(sb, protocol.getAbstract());
    
    sb.append("  </table>\n");
    sb.append("</div>\n");
  }

  private void appendSummary(StringBuilder sb, Investigation investigation) {
    sb.append("<div class='summary investigation_summary'>\n");
    sb.append("  <table class='summary_table'>\n");
    appendTableValue(sb, "Investigation:", utility.getUIUrl(investigation).toString(), investigation.getTitle());
    appendTableValue(sb, "Protocol ID:", investigation.getSeuratId());
    
    if (investigation.getAuthors().size() > 0) {
      String authorsString = shortName(investigation.getAuthors().get(0));
      if (investigation.getAuthors().size() > 1)  {
        authorsString += " et al";
      }
      appendTableValue(sb, "Authors:", authorsString);
    }
    
    appendTableValue(sb, investigation.getAbstract());
    
    sb.append("  </table>\n");
    sb.append("</div>\n");
  }

  private static String shortName(User user) {
    StringBuilder sb = new StringBuilder();
    if (user.getFirstname() != null && user.getLastname() != null) {
      sb.append(user.getFirstname().charAt(0));
      sb.append(".");
      sb.append(user.getLastname());
    }
    else if (user.getUserName() != null) {
      sb.append(user.getUserName());
    }
    else if (user.getLastname() != null) {
      sb.append(user.getLastname());
    }
    else {
      sb.append("Unknown");
    }
    
    return sb.toString();
  }
}
