package org.toxbank.demo.task;

import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.SimpleTaskResource;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.reporters.TaskHTMLReporter;
import net.idea.restnet.c.resource.TaskResource;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.Task.TaskStatus;
import net.idea.restnet.rdf.reporter.TaskRDFReporter;

import org.restlet.Request;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.protocol.TBHTMLBeauty;

public class TBTaskResource extends TaskResource<String> {

	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
	}
	
	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertor<Object>(storage,getHTMLBeauty()) {
			@Override
			public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterRDF(
					Variant variant, Request request, ResourceDoc doc)
					throws AmbitException, ResourceException {
				return new TaskRDFReporter(storage,request,variant.getMediaType(),doc);
			}
			@Override
			public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterHTML(
					Request request, ResourceDoc doc, HTMLBeauty htmlbeauty)
					throws AmbitException, ResourceException {
				return	new TaskHTMLReporter(storage,request,doc,htmlbeauty) {
					
					@Override
					public void header(Writer output, Iterator query) {
						try {
							
							
							String max = getRequest().getResourceRef().getQueryAsForm().getFirstValue(AbstractResource.max_hits);
							max = max==null?"10":max;
							
							if (htmlBeauty==null) htmlBeauty = new TBHTMLBeauty();
							htmlBeauty.writeHTMLHeader(output, htmlBeauty.getTitle(), getRequest(),"",
									getDocumentation()
									);//,"<meta http-equiv=\"refresh\" content=\"10\">");
							output.write("\n<h3>Tasks:\n");
							for (TaskStatus status :TaskStatus.values())
								output.write(String.format("<a href='%s%s?search=%s&%s=%s'>%s</a>&nbsp;\n",
										baseReference,SimpleTaskResource.resource,status,AbstractResource.max_hits,max,status));
							output.write("</h3>\n");
							output.write("<table class='datatable' id='tasktable'>\n");
							output.write("<thead><th>Start time</th><th>Elapsed time,ms</th><th>Task</th><th>Name</th><th></th><th>Status</th><th>Status Message</th><th></th></thead>\n");
							output.write("<tbody>\n");
						} catch (Exception x) {
							
						}
					}
					@Override
					public void footer(Writer output, Iterator query) {
						try {
							output.write("</tbody>\n");
						} catch (Exception x) {
							
						}
						super.footer(output, query);
					}
				};
			}
		};
	}
}
