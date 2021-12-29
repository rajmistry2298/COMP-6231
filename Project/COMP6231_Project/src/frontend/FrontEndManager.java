package frontend;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import FrontEndApp.FrontEnd;
import FrontEndApp.FrontEndHelper;
import service.ActivityLoggerService;
import util.FileConstants;
import util.LocationConstants;

public class FrontEndManager {
	
	 public static void main(String[] args) {
	    	
	        try{
	        	final ActivityLoggerService activityLogger = new ActivityLoggerService(FileConstants.FRONTEND_LOG_FILE_PATH + FileConstants.FRONTEND_LOG);
	            
	        	/* Create & Initialize The ORB 
	             * Get Reference To rootPOA & Activate The POAManager*/
	            final ORB orb = ORB.init(args, null);   
	            final POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
	            rootpoa.the_POAManager().activate();
	       
	            /* Create Servant & Register It With The ORB */
	            final FrontEndImpl frontendImpl = new FrontEndImpl(LocationConstants.FRONTEND, activityLogger);
	       
	            /* Get Object Reference From The Servant */
	            final org.omg.CORBA.Object ref = rootpoa.servant_to_reference(frontendImpl);
	            
	            /* Cast The Reference To a CORBA Reference */
	            final FrontEnd href = FrontEndHelper.narrow(ref);
	       
	            /* NameService Invokes The Transient Name Service  */
	            final org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
	            final NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	            
	            /* Bind Object Reference In Naming */
	            final NameComponent path[] = ncRef.to_name(LocationConstants.FRONTEND);
	            ncRef.rebind(path, href);
	       
	            System.out.println("#========= !! Front End Started !! =========#");
	            orb.run();
	            
	        } catch (Exception e) {
	            System.err.println("ERROR: " + e);
	            e.printStackTrace(System.out);
	        }
	    }
}