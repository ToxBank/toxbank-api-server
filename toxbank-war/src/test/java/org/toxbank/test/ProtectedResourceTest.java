package org.toxbank.test;

import java.io.InputStream;
import java.util.Properties;

import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;

import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.dsl.aa.IAuthToken;
import org.opentox.dsl.task.ClientResourceWrapper;


public abstract class ProtectedResourceTest extends ResourceTest implements IAuthToken  {
	Properties properties = new Properties();
	protected String getCreator() {
		if ((ssoToken!=null) && (ssoToken.getToken()!=null)) 
			try { return OpenSSOServicesConfig.getInstance().getTestUser();} catch (Exception x) {return null;}
		else return "test";
	}

	protected boolean isAAEnabled() {
   		InputStream in = null;
		try {
			properties = new Properties();
			in = this.getClass().getClassLoader().getResourceAsStream("org/toxbank/rest/config/toxbank.properties");
			properties.load(in);
			return Boolean.parseBoolean(properties.get("toxbank.protected").toString());	
		} catch (Exception x) {
			try {in.close(); } catch (Exception xx) {}	
		}
		return false;
	}	
	@Override
	public void setUp() throws Exception {
		setUpAA();
		super.setUp();
	}
	
	public void setUpAA() throws Exception {
		if (isAAEnabled()) {
			ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			String username=properties.getProperty("toxbank.aa.user");
			String pass=properties.getProperty("toxbank.aa.pass");
			if (ssoToken.login(
					username,
					pass
					)) {
				ClientResourceWrapper.setTokenFactory(this);
			} else
				throw new Exception(String.format("Error logging to SSO (%s)",OpenSSOServicesConfig.getInstance().getTestUser()));
		}
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		try {
			ClientResourceWrapper.setTokenFactory(null);
			if (ssoToken!= null) ssoToken.logout();
		} catch (Exception x) {
		}
	}
	@Override
	public String getToken() {
		return ssoToken==null?null:ssoToken.getToken();
	}
	
	
}
