package interfaces;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface GameServer {
	  String createPlayerAccount (String firstName, String lastName, String age, String username, String password, String ipAddress);
	  String playerSignIn (String username, String password);
	  String playerSignOut (String username);
	  String transferAccount (String username, String password, String newIpAddress);
	  String getPlayerStatus ();
	  String suspendAccount (String usernameToSuspend);
}
