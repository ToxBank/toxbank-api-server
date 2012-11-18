package org.toxbank.demo;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

/**
 * This is used as a servlet component instead of the core one, to be able to attach protocols 
 * @author nina
 *
 */
public class TBRESTComponent extends Component {
		public TBRESTComponent() {
			this(null);
		}
		public TBRESTComponent(Context context,Application[] applications) {
			super();
			this.getClients().add(Protocol.FILE);
			this.getClients().add(Protocol.HTTP);
			this.getClients().add(Protocol.HTTPS);
			this.getClients().add(Protocol.RIAP);

			getServers().add(Protocol.RIAP);
			
			for (Application application: applications) {
				application.setContext(context==null?getContext().createChildContext():context);
			    getDefaultHost().attach(application);
			}
		    getInternalRouter().attachDefault(applications[0]);	
		//	getInternalRouter().attach("/notification", NotificationResource.class);

		}
		
		public TBRESTComponent(Context context) {
			this(context,new Application[]{new TBApplication()});
		}
		
		

}
