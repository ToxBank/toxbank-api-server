package org.toxbank.rest.db.exceptions;

import net.idea.modbcum.i.exceptions.AmbitException;

public class InvalidUserException extends AmbitException {
	private final static String msg = "No user id!";
	/**
	 * 
	 */
	private static final long serialVersionUID = 9054812229642318416L;
	public InvalidUserException() {
		super(msg);
	}

	public InvalidUserException(String arg0) {
		super(arg0);
	}

	public InvalidUserException(Throwable arg0) {
		this(msg,arg0);
	}

	public InvalidUserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
