package service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

import util.FieldConstants;

public class ActivityLoggerService {

    private FileWriter fw = null;
    private BufferedWriter bw = null;
    private PrintWriter pw = null;

    public ActivityLoggerService(final String activityLoggerFile) throws IOException {
        fw = new FileWriter(activityLoggerFile, true);
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
    }

    public synchronized void log(final String messageType, final String message) {
    	try {
    		final String dataLog = DateFormat.getDateTimeInstance().format(new Date()) + FieldConstants.SEPARATOR_ARROW + 
								   messageType + FieldConstants.SEPARATOR_COLON + 
								   message;
    		pw.println(dataLog);
    		System.out.println(dataLog);
    		bw.flush();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}