package org.toxbank.rest.user.alerts.notification;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import net.toxbank.client.resource.*;

import org.junit.Test;

public class SimpleNotificationTestCase {  
  Pattern paramStringPattern = Pattern.compile("\\?mod=([0-9]+)");
  
  @Test
  public void testNotification() throws Throwable {
    int protocolCount = 5;
    int investigationCount = 3;
    
    final List<Alert<User>> testAlerts = new ArrayList<Alert<User>>();
    int alertCount = 2;
    for (int i = 0; i < alertCount; i++) {
      testAlerts.add(createTestAlert());
    }

    final List<AbstractToxBankResource> testResources = new ArrayList<AbstractToxBankResource>();
    for (int i = 0; i < protocolCount; i++) {
      testResources.add(createTestProtocol());
    }
    for (int i = 0; i < investigationCount; i++) {
      testResources.add(createTestInvestigation());
    }    
    
    final User testUser = createTestUser();
        
    SimpleNotificationEngine engine = new SimpleNotificationEngine(new AlertNotificationUtility (){
      public void sendNotification(
          String toEmail, 
          String subject, 
          Object content, 
          String mimeType) throws Exception {
        TestCase.assertEquals(testUser.getEmail(), toEmail);
        TestCase.assertEquals(subject, SimpleNotificationEngine.notificationSubject);
        TestCase.assertEquals("text/html", mimeType);
        FileWriter fw = new FileWriter(new File("target/SimpleNotificationTestCase-output.html"));
        fw.write(content.toString());
        fw.close();
      }
      public List<URL> querySearchService(String paramString, String ssoToken) throws Exception {
        List<URL> matchingUrls = new ArrayList<URL>();
        Matcher matcher = paramStringPattern.matcher(paramString);
        TestCase.assertTrue("paramString (" + paramString + ") must match one of test alert patterns", matcher.matches());
        int modulo = Integer.parseInt(matcher.group(1));
        for (int i = 0; i < testResources.size(); i++) {
          if (i % modulo == 0) {
            matchingUrls.add(testResources.get(i).getResourceURL());
          }
        }
        return matchingUrls;
      }
      public List<AbstractToxBankResource> getResources(List<URL> urls) throws Exception {
        List<AbstractToxBankResource> resources = new ArrayList<AbstractToxBankResource>();
        for (URL url : urls) {
          for (AbstractToxBankResource resource : testResources) {
            if (resource.getResourceURL().equals(url)) {
              resources.add(resource);
            }
          }
        }
        return resources;
      }
      public String getUIUrl(Protocol protocol) {
        try {
          return "http://example.com/protocols?protocolUrl=" + 
              URLEncoder.encode(protocol.getResourceURL().toString(), "UTF-8");
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      public String getUIUrl(Investigation investigation) {
        try {
          return "http://example.com/investigations?investigationUrl=" + 
              URLEncoder.encode(investigation.getResourceURL().toString(), "UTF-8");
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }    
    });
    
    engine.sendAlerts(testUser, testAlerts, null);
  }
    
  private String createProtoId(int id, int version) {
    return "SEURAT-Protocol-" + id + "-" + version;
  }

  private String createInvestigationId(int id) {
    return "SEURAT-Investigation-" + id;
  }

  private URL createUrl(Protocol proto) throws Throwable {
    return new URL("http://example.com/protocol/" + proto.getIdentifier());
  }

  private URL createUrl(Investigation investigation, int id) throws Throwable {
    return new URL("http://example.com/investigation/" + id);
  }

  private int nextId = 1;
  
  private Alert<User> createTestAlert() throws Throwable {
    Alert<User> alert = new Alert<User>();
    int id = nextId++;
    
    alert.setTitle("Test Alert " + id);
    alert.setQueryString("?mod=" + (id+1));
    
    return alert;
  }
  
  private User createTestUser() throws Throwable {
    User user = new User();
    int id = nextId++;
    
    user.setEmail("user" + id + "@example.com");
    user.setFirstname("Test");
    user.setLastname("User-" + String.valueOf(id));
    
    return user;
  }
  
  private Protocol createTestProtocol() throws Throwable {    
    Protocol proto = new Protocol();
    int id = nextId++;
    int version = 1;
    int authorCount = 3;
    
    proto.setVersion(version);
    proto.setIdentifier(createProtoId(id, version));
    proto.setResourceURL(createUrl(proto));
    proto.setTitle("Test Protocol " + id + "-" + version);
    proto.setAbstract("An abstract for a test protocol " + id + "-" + version);
    
    for (int i = 0; i < authorCount; i++) {
      proto.addAuthor(createTestUser());
    }
    
    return proto;
  }
  
  private Investigation createTestInvestigation() throws Throwable {
    Investigation investigation = new Investigation();
    int id = nextId++;
    int version = 1;
    int authorCount = 3;
        
    investigation.setSeuratId(createInvestigationId(id));
    investigation.setResourceURL(createUrl(investigation, id));
    investigation.setTitle("Test Investigation " + id + "-" + version);
    investigation.setAbstract("An abstract for a test investigation " + id + "-" + version);
    
    List<User> authors = new ArrayList<User>();
    for (int i = 0; i < authorCount; i++) {
      authors.add(createTestUser());
    }
    investigation.setAuthors(authors);
    
    return investigation;
  }
}
