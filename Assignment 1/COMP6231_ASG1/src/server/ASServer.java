package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

import server.GameServerImpl;
import util.LocationConstants;
import util.PortConstants;

/**
 * AS Server Class
 * Create separate server instance for AS location
 * Register those to the registry service to expose interface APIs
 */
public class ASServer{

	public static void main(String[] args)  throws Exception{
		try {
			final GameServerImpl asServer = new GameServerImpl(LocationConstants.ASIA);
	        final Registry registry = LocateRegistry.createRegistry(PortConstants.AS_REGISTRY_PORT);
	        registry.bind(LocationConstants.ASIA, asServer);
	        System.out.println("#================= Asia Server is started =================#");

	      //Creating Log file
	        if (Files.exists(Paths.get("src/server/logs/AS_Server_Log.txt"))) {
				writeLog("AS Server Started!!!");
			} else {
				PrintWriter writer = new PrintWriter("src/server/logs/AS_Server_Log.txt", "UTF-8");
				writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : AS Log File Crated!!");
				writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : AS Server Started!!!");
				writer.close();
			}
		} catch(Exception e) {
			System.out.println("EXCEPTION :: "+ e.getMessage());
		}
		
	}
	
	public static void writeLog(String logData) throws IOException {
		logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
		Files.write(Paths.get("src/server/logs/AS_Server_Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
	}

}
