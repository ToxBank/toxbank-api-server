package org.toxbank.rest.protocol.metadata;

import java.net.URI;

import org.toxbank.resource.IDocument;

public class Document implements IDocument {
	URI uri;
	
	public Document(URI uri) {
		setURI(uri);
	}
	@Override
	public URI getURI() {
		return uri;
	}

	@Override
	public void setURI(URI uri) {
		this.uri = uri;
	}
	@Override
	public String toString() {

		return uri.toString();
	}
}
