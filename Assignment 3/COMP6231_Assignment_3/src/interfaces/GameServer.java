package interfaces;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService

public interface GameServer {
	  @WebMethod
	  String createPlayerAccount (String firstName, String lastName, String age, String username, String password, String ipAddress);
	  @WebMethod
	  String playerSignIn (String username, String password);
	  @WebMethod
	  String playerSignOut (String username);
	  @WebMethod
	  String transferAccount (String username, String password, String newIpAddress);
	  @WebMethod
	  String getPlayerStatus ();
	  @WebMethod
	  String suspendAccount (String usernameToSuspend);
}
