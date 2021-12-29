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
 * NA Server Class
 * Create separate server instance for NA location
 * Register those to the registry service to expose interface APIs
 */
public class NAServer {

	public static void main(String[] args) throws Exception {
		try {
			final GameServerImpl naServer = new GameServerImpl(LocationConstants.NORTHAMERICA);
	        final Registry registry = LocateRegistry.createRegistry(PortConstants.NA_REGISTRY_PORT);
	        registry.bind(LocationConstants.NORTHAMERICA, naServer);
	        System.out.println("#================= North-America Server is started =================#");
	        
	        //Creating Log file
	        if (Files.exists(Paths.get("src/server/logs/NA_Server_Log.txt"))) {
				writeLog("NA Server Started!!!");
			} else {
				PrintWriter writer = new PrintWriter("src/server/logs/NA_Server_Log.txt", "UTF-8");
				writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : NA Log File Crated!!");
				writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : NA Server Started!!!");
				writer.close();
			}
		}catch(Exception e) {
			System.out.println("EXCEPTION :: "+ e.getMessage());
		}
	}
	
	public static void writeLog(String logData) throws IOException {
		logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
		Files.write(Paths.get("src/server/logs/NA_Server_Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
	}

}
