package org.toxbank.rest.user.alerts.notification;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import net.toxbank.client.io.rdf.InvestigationIO;
import net.toxbank.client.resource.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.protocol.HttpContext;
import org.opentox.rest.RestException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Default implementation of the utilities needed by the alert notification engine
 */
public class DefaultAlertNotificationUtility implements AlertNotificationUtility {
  private static String ADMIN_EMAIL_PROP = "alert.admin.email";
  private static String UI_SERVICE_URL_PROP = "alert.ui.service.url";
  private static String SEARCH_SERVICE_URL_PROP = "alert.search.service.url";
  
  private String searchServiceUrl;
  private String uiServiceUrl;
  
  private String adminEmail;
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
      adminEmail = config.getProperty(ADMIN_EMAIL_PROP);
      searchServiceUrl = config.getProperty(SEARCH_SERVICE_URL_PROP);  
      uiServiceUrl = config.getProperty(UI_SERVICE_URL_PROP);  
      
      mailSession = Session.getInstance(config);
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
      String mimeType) throws Exception {
    if (mailSession != null) {
      Message msg = new MimeMessage(mailSession);
      msg.setSubject(subject);
      msg.setFrom(new InternetAddress(adminEmail));
      msg.setRecipient(RecipientType.TO, new InternetAddress(toEmail));
      msg.setContent(content, mimeType);
      Transport.send(msg);
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
  public List<AbstractToxBankResource> getResources(List<URL> urls, String ssoToken) throws Exception {
    List<AbstractToxBankResource> resultList = new ArrayList<AbstractToxBankResource>();
    
    for (URL url : urls) {
      String urlString = url.toString();
      AbstractToxBankResource resource = null;
      try {        
        if (protocolUrlPattern.matcher(urlString).matches()) {
          resource = retrieveProtocol(url);
        }
        else {
          resource = retrieveInvestigation(url, ssoToken);
        }
        if (resource != null) {
          resultList.add(resource);
        }
      }
      catch (Exception e) {
        throw new RuntimeException("Error getting resource: " + url, e);
      }
    }
    
    return resultList;
  }
  
  private Protocol retrieveProtocol(URL url) throws Exception {
    // implement once decided how to do so
    return null;
  }
  
  private static Pattern investigationUrlPattern = Pattern.compile("(.*)/([0-9]+)");  
  
  private Investigation retrieveInvestigation(URL url, String ssoToken) throws Exception {
    String urlString = url.toString();
    Matcher matcher = investigationUrlPattern.matcher(urlString.toString());
    if (!matcher.matches()) {
      throw new RuntimeException("Invalid investigation url: " + urlString);
    }
    String rootUrl = matcher.group(1);
    String seuratId = "SEURAT-Investigation-" + matcher.group(2);
    
    Model model = ModelFactory.createDefaultModel();

    HttpClient httpClient = createHTTPClient(ssoToken);
    
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
      public void verify(String host, X509Certificate cert) throws SSLException { }
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
