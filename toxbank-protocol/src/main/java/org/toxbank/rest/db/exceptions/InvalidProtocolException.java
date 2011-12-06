package org.toxbank.rest.db.exceptions;

import net.idea.modbcum.i.exceptions.AmbitException;

public class InvalidProtocolException extends AmbitException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9054812229642318416L;
	public InvalidProtocolException() {
		super("No protocol id!");
	}
}
