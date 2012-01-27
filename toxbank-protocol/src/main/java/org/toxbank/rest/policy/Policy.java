package org.toxbank.rest.policy;

import java.util.Hashtable;

/**
 * Local policy
 * @author nina
 *
 */
public class Policy {
	protected String url;
	protected String name;
	protected Hashtable<Method, Boolean> ops = new Hashtable<Method, Boolean>();
	private enum Method {
		GET,PUT,POST,DELETE,HEAD,OPTIONS
	}
	public Policy() {
		setName(name);
	}
	public Policy(String name,String url,boolean get,boolean post, boolean put, boolean delete) {
		setName(name);
		setUrl(url);
		setAllowDELETE(delete);
		setAllowGET(get);
		setAllowPOST(post);
		setAllowPUT(put);
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private boolean isAllow(Method method) {
		Boolean o = ops.get(method);
		return o==null?false:o.booleanValue();
	}
	
	public boolean isAllow(String method) {
		try {
			Boolean o = ops.get(Method.valueOf(method));
			return o==null?false:o.booleanValue();
		} catch (Exception x) { return false;}
	}
	private void setAllow(Method method, boolean value) {
		ops.put(method,value);
	}
	public boolean isAllowGET() {
		return isAllow(Method.GET);
	}
	
	public void setAllowGET(boolean allow) {
		setAllow(Method.GET,allow);
	}
	public boolean isAllowPOST() {
		return isAllow(Method.POST);
	}
	public void setAllowPOST(boolean allow) {
		setAllow(Method.POST,allow);
	}
	public boolean isAllowPUT() {
		return isAllow(Method.PUT);
	}
	public void setAllowPUT(boolean allow) {
		setAllow(Method.POST,allow);
	}
	public boolean isAllowDELETE() {
		return isAllow(Method.DELETE);
	}
	public void setAllowDELETE(boolean allow) {
		setAllow(Method.DELETE,allow);
	}
	
}
