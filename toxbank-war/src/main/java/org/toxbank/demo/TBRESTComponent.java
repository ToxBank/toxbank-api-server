package org.toxbank.demo;

import net.idea.restnet.c.RESTComponent;

import org.restlet.Application;
import org.restlet.Context;

/**
 * This is used as a servlet component instead of the core one, to be able to attach protocols 
 * @author nina
 *
 */
public class TBRESTComponent extends RESTComponent {
		public TBRESTComponent() {
			this(null);
		}
		public TBRESTComponent(Context context,Application[] applications) {
			super(context,applications);
			
		
		}
		public TBRESTComponent(Context context) {
			this(context,new Application[]{new TBApplication()});
		}
		
		

}
