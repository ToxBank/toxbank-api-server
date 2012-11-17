package org.toxbank.rest.db.exceptions;

import net.idea.modbcum.i.exceptions.AmbitException;

public class InvalidProjectException extends AmbitException {
	private final static String msg = "No project id!";
	/**
	 * 
	 */
	private static final long serialVersionUID = 9054812229642318416L;
	public InvalidProjectException() {
		super(msg);
	}

	public InvalidProjectException(String arg0) {
		super(arg0);
	}

	public InvalidProjectException(Throwable arg0) {
		this(msg,arg0);
	}

	public InvalidProjectException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
