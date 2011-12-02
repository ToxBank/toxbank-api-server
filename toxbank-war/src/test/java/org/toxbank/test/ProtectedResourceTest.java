package org.toxbank.test;

import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;

import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.dsl.aa.IAuthToken;
import org.opentox.dsl.task.ClientResourceWrapper;


public abstract class ProtectedResourceTest extends ResourceTest implements IAuthToken  {

	protected String getCreator() {
		if ((ssoToken!=null) && (ssoToken.getToken()!=null)) 
			try { return OpenSSOServicesConfig.getInstance().getTestUser();} catch (Exception x) {return null;}
		else return "test";
	}

	protected boolean isAAEnabled() {
		 try {return (OpenSSOServicesConfig.getInstance().isEnabled()); } catch (Exception x) {return true;}
	}
	@Override
	public void setUp() throws Exception {
		setUpAA();
		super.setUp();
	}
	
	public void setUpAA() throws Exception {
		if (isAAEnabled()) {
			ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			if (ssoToken.login(
					OpenSSOServicesConfig.getInstance().getTestUser(),
					OpenSSOServicesConfig.getInstance().getTestUserPass()
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
