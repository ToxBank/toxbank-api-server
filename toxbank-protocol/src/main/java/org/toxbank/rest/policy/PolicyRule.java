package org.toxbank.rest.policy;

import java.util.Hashtable;

/**
 * Local policy
 * @author nina
 *
 */
public class PolicyRule<T> {
	protected String resource;

	protected T subject;

	protected Hashtable<Method, Boolean> action = new Hashtable<Method, Boolean>();
	private enum Method {
		GET,PUT,POST,DELETE,HEAD,OPTIONS
	}
	public PolicyRule() {
		
	}
	public PolicyRule(T subject,String url,boolean get,boolean post, boolean put, boolean delete) {
		setSubject(subject);
		setResource(url);
		setAllowDELETE(delete);
		setAllowGET(get);
		setAllowPOST(post);
		setAllowPUT(put);
	}
	
	public T getSubject() {
		return subject;
	}
	public void setSubject(T subject) {
		this.subject = subject;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}

	private boolean isAllow(Method method) {
		Boolean o = action.get(method);
		return o==null?false:o.booleanValue();
	}
	
	public boolean isAllow(String method) {
		try {
			Boolean o = action.get(Method.valueOf(method));
			return o==null?false:o.booleanValue();
		} catch (Exception x) { return false;}
	}
	private void setAllow(Method method, boolean value) {
		action.put(method,value);
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
