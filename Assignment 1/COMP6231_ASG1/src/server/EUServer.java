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
 * EU Server Class
 * Create separate server instance for EU location
 * Register those to the registry service to expose interface APIs
 */
public class EUServer {

	public static void main(String[] args) throws Exception {
		try {
			final GameServerImpl euServer = new GameServerImpl(LocationConstants.EUROPE);
	        final Registry registry = LocateRegistry.createRegistry(PortConstants.EU_REGISTRY_PORT);
	        registry.bind(LocationConstants.EUROPE, euServer);
	        System.out.println("#================= Europe Server is started =================#");

	      //Creating Log file
	        if (Files.exists(Paths.get("src/server/logs/EU_Server_Log.txt"))) {
				writeLog("EU Server Started!!!");
			} else {
				PrintWriter writer = new PrintWriter("src/server/logs/EU_Server_Log.txt", "UTF-8");
				writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : EU Log File Crated!!");
				writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : EU Server Started!!!");
				writer.close();
			}

		} catch(Exception e) {
			System.out.println("EXCEPTION :: "+ e.getMessage());
		}
	}
	
	public static void writeLog(String logData) throws IOException {
		logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
		Files.write(Paths.get("src/server/logs/EU_Server_Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
	}

}
