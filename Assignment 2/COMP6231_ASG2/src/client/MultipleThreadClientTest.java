package client;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import GameServerApp.GameServer;
import GameServerApp.GameServerHelper;
import util.LocationConstants;

public class MultipleThreadClientTest implements Runnable{
	
	private GameServer server = null;
	
	public MultipleThreadClientTest(final String ipAddress, final String args[]) {
		setServer(args, ipAddress);
	}
	
	public static void main(String[] args) {
		System.out.println("*************************** This Test Case Is To Check Concurrency ***************************");
		System.out.println("-> 3 Threads Will Be Created For NORTH-AMERICA Server Which Will Perform Following Operations.");
		System.out.println("1. Create Player Account : USERNAME :'newusername' & PASSWORD : 'newpassword'");
		System.out.println("2. SignIn In This New Player Account.");
		System.out.println("3. Get Player Status.");
		System.out.println("4. Sigout From This New Account.");
		System.out.println("5. Transfer This New Player Account From NA Server To ASIA Server.");
		System.out.println("6. Try To Suspend This New Account On NA Server.");
		System.out.println("7. Suspend Player Account 'rajmistry2298' From NA Server.");
		System.out.println("8. Try To Transfer Account'rajmistry2298' To EUROPE Server.");
		System.out.println("All The Threads will Perform This all Operations And Order OF Threads Is Not Fixed.So After The");
		System.out.println("Completion Of All This Operations By All Threads Result Should Be As Below :");
		System.out.println("=>One Thread Will Be Able To Create New Account. Other 2 Will get Already Exisit Message.");
		System.out.println("=>One Thread Will Be Able To SignIn Successfully. Other 2 Will get Already SignedIn Message.");
		System.out.println("=>Get Player Status: Either All Servers Have Accounts Offline Or All Are Offline Except 1 On NA");
		System.out.println("=>One Thread Will Be Able To Sign Out Successfully. Other 2 Will get Already Signed Out Message.");
		System.out.println("=>One Thread Will Be Able To Transfer Successfully. Other 2 Will get Not Exist Message.");
		System.out.println("=>For Suspend: All Threads Will get Not Exist Message.");
		System.out.println("=>One Thread Will Be Able To Suspend Account Successfully. Other 2 Will get Not Exist Message.");
		System.out.println("=>For Transfer: All Threads Will get Not Exist Message.");
		System.out.println("*************************************************************************************************");
		
		
		final MultipleThreadClientTest player0Thread = new MultipleThreadClientTest("132.2.2.2", args);
        final Thread t = new Thread(player0Thread);
        t.start();
        System.out.println("Thread 't' Started");
        
		final MultipleThreadClientTest player1Thread = new MultipleThreadClientTest("132.4.55.5", args);
        final Thread t1 = new Thread(player1Thread);
        t1.start();
        System.out.println("Player 't1' Thread Started");
        
        final MultipleThreadClientTest player2Thread = new MultipleThreadClientTest("132.4.4.4", args);
        final Thread t2 = new Thread(player2Thread);
        t2.start();
        System.out.println("Player 't2' Thread Started");
        
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("\n After All Operations Performed If This Test Is Run on Initial Servers where each server has 5");
        System.out.println(" Initial Player Accounts Then At the end it should be EXPECTED to have player status: ");
        System.out.println(" NA : Online:0 Offline:4., EU : Online:0 Offline:5., AS : Online:0 Offline:6");
        System.out.println("\n\nAt Last After All The Threads Finished Their Execution Actual Player Status Is: ");
        final MultipleThreadClientTest normalObject = new MultipleThreadClientTest("93.3.3.5", args);
        System.out.println(normalObject.server.getPlayerStatus());
	}
	
	public void setServer(String [] args,String ipAddress) {
		try {
			ORB orb = ORB.init(args, null);
		    org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
		    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		    
			if(ipAddress.split("\\.")[0].equals("132")) { 	
	    		server = (GameServer) GameServerHelper.narrow(ncRef.resolve_str(LocationConstants.NORTHAMERICA));
	        }
	        else if(ipAddress.split("\\.")[0].equals("93")) {
	        	server = (GameServer) GameServerHelper.narrow(ncRef.resolve_str(LocationConstants.EUROPE));
	        }
	        else if(ipAddress.split("\\.")[0].equals("182")) {
				server = (GameServer) GameServerHelper.narrow(ncRef.resolve_str(LocationConstants.ASIA));
	        }
		}catch (NotFound | CannotProceed | InvalidName | org.omg.CORBA.ORBPackage.InvalidName e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void run() {
		System.out.println("Thread :"+Thread.currentThread().getId()+" : "+server.createPlayerAccount("new", "user", "22", "newusername", "newpassword", "132.2.2.2"));
		System.out.println("Thread :"+Thread.currentThread().getId()+" : " +server.playerSignIn("newusername", "newpassword"));
		System.out.println("Thread :"+Thread.currentThread().getId()+" : " +server.getPlayerStatus());
		System.out.println("Thread :"+Thread.currentThread().getId()+" : " +server.playerSignOut("newusername"));
		System.out.println("Thread :"+Thread.currentThread().getId()+" : "+server.transferAccount("newusername", "newpassword" , "182.2.2.2"));
		System.out.println("Thread :"+Thread.currentThread().getId()+" : "+server.suspendAccount("newusername"));		
		System.out.println("Thread :"+Thread.currentThread().getId()+" : "+server.suspendAccount("rajmistry2298"));
		System.out.println("Thread :"+Thread.currentThread().getId()+" : "+server.transferAccount("rajmistry2298", "R@mistry" , "93.3.3.3"));	
	}
}