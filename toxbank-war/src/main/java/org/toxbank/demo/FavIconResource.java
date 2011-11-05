package org.toxbank.demo;


import java.io.IOException;
import java.io.OutputStream;

import net.idea.restnet.i.tools.DownloadTool;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * favicon.ico support
 * @author nina
 *
 */
public class FavIconResource extends ServerResource {
	@Override
	public Representation get(Variant variant) throws ResourceException {
		return new OutputRepresentation(MediaType.IMAGE_PNG) {
			@Override
			public void write(OutputStream outputStream)
					throws IOException {
				try {
					DownloadTool.download(getClass().getClassLoader().getResourceAsStream("/images/16x16.png"), outputStream);
				outputStream.close();				
				} catch (Exception x) {
					
				}
				
			}
		};
	}
}
