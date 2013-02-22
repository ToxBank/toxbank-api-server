package org.toxbank.rest.user.alerts.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.toxbank.client.io.rdf.InvestigationIO;
import net.toxbank.client.io.rdf.ProtocolIO;
import net.toxbank.client.io.rdf.TOXBANK;
import net.toxbank.client.resource.AbstractToxBankResource;
import net.toxbank.client.resource.Investigation;
import net.toxbank.client.resource.Protocol;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.protocol.HttpContext;
import org.opentox.rest.RestException;
import org.restlet.data.MediaType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Default implementation of the utilities needed by the alert notification engine
 */
public class DefaultAlertNotificationUtility implements AlertNotificationUtility {
  private static String ADMIN_EMAIL_PROP = "alert.admin.email";
  private static String UI_SERVICE_URL_PROP = "alert.ui.service.url";
  private static String SEARCH_SERVICE_URL_PROP = "alert.search.service.url";
  
  private static String SMTP_AUTH_PROP = "mail.smtp.auth";
  private static String SMTP_USER_PROP = "mail.user";
  private static String SMTP_PASSWORD_PROP = "alert.mail.password";
  
  private String searchServiceUrl;
  private String uiServiceUrl;
  
  private String adminEmail;
  private String mailUser;
  private String mailPassword;
  private boolean useMailAuth; 
  
  private Session mailSession;  
  private Properties config;
  private String configFile;
  private Logger log;
  
  /**
   * @param configFile the resource path to the configuration file
   */
  public DefaultAlertNotificationUtility(String configFile) {
    this.configFile = configFile;
    
    log = Logger.getLogger("org.toxbank.rest.user.alerts.notification");
    config = new Properties();
    try {
      config.load(getClass().getClassLoader().getResourceAsStream(configFile));
      log.log(Level.INFO, "Mail session properties loaded "+config);
      adminEmail = config.getProperty(ADMIN_EMAIL_PROP);
      searchServiceUrl = config.getProperty(SEARCH_SERVICE_URL_PROP);  
      if (searchServiceUrl.endsWith("/")) {
        searchServiceUrl = searchServiceUrl.substring(0, searchServiceUrl.length()-1);
      }
      if (!searchServiceUrl.endsWith("/search")) {
        searchServiceUrl += "/search";
      }
      uiServiceUrl = config.getProperty(UI_SERVICE_URL_PROP);  
      
      mailUser = config.getProperty(SMTP_USER_PROP);
      mailPassword = config.getProperty(SMTP_PASSWORD_PROP);
      useMailAuth = "true".equalsIgnoreCase(config.getProperty(SMTP_AUTH_PROP));
      
      /*
       * A workaround to trust all certificates. Better to import the cert in the server key store
	   * Now we have the cert imported.	
      MailSSLSocketFactory socketFactory= new MailSSLSocketFactory();
      socketFactory.setTrustAllHosts(true);
      config.put("mail.smtp.ssl.socketFactory", socketFactory);
             */
      if (useMailAuth) {
        Authenticator auth = new Authenticator() {
          public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(mailUser, mailPassword);
          }
        };
        mailSession = Session.getInstance(config, auth);
      }
      else {
        mailSession = Session.getInstance(config);
      }
    }
    catch (Exception e) {
      log.log(Level.SEVERE, "Error getting alerts configuration", e);
    }
  }
  
  @Override
  public void sendNotification(
      String toEmail, 
      String subject, 
      Object content, 
      String mimeType) throws MessagingException {
    if (mailSession != null) {
      Message msg = new MimeMessage(mailSession);
      msg.setSubject(subject);
      msg.setFrom(new InternetAddress(adminEmail));
      msg.setRecipient(RecipientType.TO, new InternetAddress(toEmail));
      msg.setContent(content, mimeType);
      
      if (useMailAuth) {
        Transport tr = mailSession.getTransport();
        try {
          tr.connect();
          msg.saveChanges();
          tr.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception x) {
        	log.log(Level.SEVERE, "Error sending message to "+toEmail ,x);
        }
        finally {
          try { tr.close(); } catch (Exception e) {}
        }
      }
      else {
        Transport.send(msg);
      }
    }
    else {
      log.log(Level.SEVERE, "Tried to send alert notification but the mail session has not been configured");
    }
  } 
  
  private static Pattern protocolUrlPattern = Pattern.compile(".+/protocol/.+");
  
  private static final Pattern urlPattern = Pattern.compile(".*<string>(.+)<\\/string>.*");
  private static final List<String> ignoredLines = Arrays.asList(
      "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",
      "<list>",
      "</list>",
      "<list/>"
      );

  @Override
  public List<URL> querySearchService(String paramString, String ssoToken) throws Exception {
    if (!isSearchServiceConfigured()) {
      throw new RuntimeException("The alert.search.service.url has not been configured in " + configFile);
    }
    
    URL searchUrl = new URL(searchServiceUrl + paramString);
    try {
      URLConnection conn = searchUrl.openConnection();
      conn.setRequestProperty("Accept", "application/xml");
      InputStream is = conn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      List<URL> resultUrls = new ArrayList<URL>();
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if (line.length() > 0) {
          Matcher urlMatcher = urlPattern.matcher(line);
          if (urlMatcher.matches()) {
            String urlString = urlMatcher.group(1);
            resultUrls.add(new URL(urlString));
          }
          else if (!ignoredLines.contains(line)){
            log.info("Line from search service did not match anything: " + line);
          }
        }
      }
      return resultUrls;
    }
    catch (Exception e) {
      throw new RuntimeException("Error connecting to search url: " + searchUrl, e);
    }
  }
  
  @Override
  public List<AbstractToxBankResource> getResources(List<URL> urls, String ssoToken) throws RestException {
    List<AbstractToxBankResource> resultList = new ArrayList<AbstractToxBankResource>();
    /**
     * Initializing HTTP client and 
     */
    HttpClient httpClient = createHTTPClient(ssoToken);
    Model modelProtocols = ModelFactory.createDefaultModel();
    for (URL url : urls) {
      String urlString = url.toString();
      AbstractToxBankResource resource = null;
      //we could have separate threads at least for both resources
      if (protocolUrlPattern.matcher(urlString).matches()) 
	      try {
	        retrieveProtocol(httpClient, url,modelProtocols);
	      } catch (RestException x) {      
	    	  log.log(Level.WARNING, String.format("Error [%d %s] retrieving protocol %s",x.getStatus(),x.getMessage(),url), x);
	      } catch (Exception x) {
	    	  log.log(Level.WARNING, String.format("Error retrieving protocol %s",url), x);
	        //do we need to throw exception? some of the resources could be protected, 
	        //even if the URIs are returned by the search service
	        //throw new RuntimeException("Error getting resource: " + url, e);
	      }
      else
	      try {
	        resource = retrieveInvestigation(httpClient, url);
	        if (resource != null) 
	          resultList.add(resource);
	      } catch (Exception e) {
	    	  log.log(Level.WARNING, String.format("Error retrieving investigation %s",url), e);
	    	  //throw new RuntimeException("Error getting resource: " + url, e);
	      }
    }

    /**
     * now read the protocols directly into the resultList
     */
    ProtocolIO protocolIO = new ProtocolIO();
    ResIterator resourceIterator = modelProtocols.listResourcesWithProperty(RDF.type, TOXBANK.PROTOCOL);
    while (resourceIterator.hasNext()) {
      Protocol item = protocolIO.fromJena(modelProtocols,resourceIterator.next());
      resultList.add(item);
    }

    //close the client
    try {
      if (httpClient !=null) {
        httpClient.getConnectionManager().shutdown();
        httpClient = null;
      }
    } catch (Exception x) {
    	log.log(Level.WARNING, x.getMessage(), x);
    }

    return resultList;
  }
  
  /**
   * @param httpClient
   * @param url
   * @return
   * @throws Exception
   */
  private int retrieveProtocol(HttpClient httpClient, URL url, Model model) throws Exception {
	    String urlString = url.toString();
	    Matcher matcher = protocolUrlPattern.matcher(urlString.toString());
	    if (!matcher.matches()) {
	      throw new RuntimeException("Invalid protocol url: " + urlString);
	    }
	    
	    HttpGet httpGet = new HttpGet(urlString);
	    httpGet.addHeader("Accept", MediaType.APPLICATION_RDF_XML.toString());
	    httpGet.addHeader("Accept-Charset", "utf-8");
	    
	    InputStream in = null;
	    try {
	      HttpResponse response = httpClient.execute(httpGet);
	      HttpEntity entity  = response.getEntity();
	      in = entity.getContent();
	      if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
	    	  model.read(in, null, "RDF/XML");
	    	  return 1;
	      }
	      else {
	        throw new RestException(response.getStatusLine().getStatusCode(),response.getStatusLine().getReasonPhrase());
	      }
	    }
	    finally {
	      if (in != null) {
	        try { in.close(); } catch (Exception e) { }
	      }
	    }
  }
  
  private static Pattern investigationUrlPattern = Pattern.compile("(.*)/([0-9\\-a-f]+)");  
  
  private Investigation retrieveInvestigation(HttpClient httpClient, URL url) throws Exception {
    String urlString = url.toString();
    Matcher matcher = investigationUrlPattern.matcher(urlString.toString());
    if (!matcher.matches()) {
      throw new RuntimeException("Invalid investigation url: " + urlString);
    }
    String rootUrl = matcher.group(1);
    String seuratId = "SEURAT-Investigation-" + matcher.group(2);
    
    Model model = ModelFactory.createDefaultModel();

    HttpGet httpGet = new HttpGet(urlString + "/metadata");
    httpGet.addHeader("Accept", "application/rdf+xml");
    httpGet.addHeader("Accept-Charset", "utf-8");
    
    InputStream in = null;
    try {
      HttpResponse response = httpClient.execute(httpGet);
      HttpEntity entity  = response.getEntity();
      in = entity.getContent();
      if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
        model.read(in, rootUrl.toString(), "RDF/XML");
      }
      else {
        throw new RestException(response.getStatusLine().getStatusCode(),response.getStatusLine().getReasonPhrase());
      }
    }
    finally {
      if (in != null) {
        try { in.close(); } catch (Exception e) { }
      }
    }
    
    InvestigationIO io = new InvestigationIO();
    List<Investigation> results = io.fromJena(model);
    
    if (results.size() == 0) {
      return null;
    }
    else {
      Investigation investigation = results.get(0);
      investigation.setSeuratId(seuratId);
      return investigation;
    }
  }
  
  public boolean isSearchServiceConfigured() {
    return searchServiceUrl != null && !SEARCH_SERVICE_URL_PROP.equals(searchServiceUrl);
  }

  public boolean isUiServiceConfigured() {
    return uiServiceUrl != null && !UI_SERVICE_URL_PROP.equals(uiServiceUrl);
  }
  
  @Override
  public String getUIUrl(Protocol protocol) {
    if (protocol == null || protocol.getResourceURL() == null || !isUiServiceConfigured()) {
      return "";
    }
    try {
      return uiServiceUrl + "/protocols?protocolUrl=" + 
          URLEncoder.encode(protocol.getResourceURL().toString(), "UTF-8");
    }
    catch (Exception e) {
      log.info("Could not encode url: " + protocol.getResourceURL().toString());
      return "";
    }
  }

  @Override
  public String getUIUrl(Investigation investigation) {
    if (investigation == null || investigation.getResourceURL() == null || !isUiServiceConfigured()) {
      return "";
    }
    try {
      return uiServiceUrl + "/investigations?investigationUrl=" + 
          URLEncoder.encode(investigation.getResourceURL().toString(), "UTF-8");
    }
    catch (Exception e) {
      log.info("Could not encode url: " + investigation.getResourceURL().toString());
      return "";
    }
  }
  
  protected HttpClient createHTTPClient(final String token) {
    try {
      ClientConnectionManager cm = createFullyTrustingClientManager();
      DefaultHttpClient cli = new DefaultHttpClient(cm);
      cli.addRequestInterceptor(new HttpRequestInterceptor() {
        @Override
        public void process(HttpRequest request, HttpContext context)
            throws HttpException, IOException {
          request.addHeader("subjectid", token);
        }
      });
      return cli;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private ClientConnectionManager createFullyTrustingClientManager() throws Exception {
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {                
            }
            public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
            }
        }
    };
    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, trustAllCerts, new SecureRandom());
    
    SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext, new X509HostnameVerifier() {
      public void verify(String host, SSLSocket ssl) throws IOException { }
      public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException { }
      public boolean verify(String arg0, SSLSession arg1) {
        return true;
      }
      @Override
      public void verify(String host, java.security.cert.X509Certificate cert)
          throws SSLException {
      }
    });
    Scheme httpsScheme = new Scheme("https", 443, sslSocketFactory);
    Scheme httpScheme = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(httpsScheme);
    schemeRegistry.register(httpScheme);
    
    ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);

    return cm;
  }  
}
