package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameServer extends Remote {

	/**
     * Creates a Player Account if everything is correct and returns result whether account successfully created Or not
     */
	public String createPlayerAccount(String firstName,String lastName,String age,String username,String password,String ipAddress) throws RemoteException;
	
	/**
     * Sets Player's Status to online if it exit and offline and returns confirmation or descriptive error
     */
	public String playerSignIn(String username,String password) throws RemoteException;
	
	/**
     * Sets Player's Status to offline if it exit and online and returns confirmation or descriptive error
     */
	public String playerSignOut(String username) throws RemoteException;
	
	/**
     * Gets All Player's Status from all 3 servers and returns count for each server's online and offline players
     */
	public String getPlayerStatus() throws RemoteException;

}
