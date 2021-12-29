package util;

public class FileConstants {
    public static final String COUNTERS_FILE_PATH = "./counters/";
    public static final String REQUEST_COUNTER_FILE_NAME = "RequestCounter.txt";
    public static final String REQUEST_COUNTER_FILE_PATH = COUNTERS_FILE_PATH + REQUEST_COUNTER_FILE_NAME;
	public static final String CLIENT_LOG_FILE_PATH = "./clientlogs/";
	public static final String SERVER_LOG_FILE_PATH = "./serverlogs/";
	public static final String FRONTEND_LOG_FILE_PATH = "./frontendlogs/";
	public static final String FILE_TYPE = ".log";
	public static final String FRONTEND_LOG = "FrontEnd" + FILE_TYPE;
	public static int REQUEST_COUNT = 0;
	public static int BYZANTINE_COUNT = 0;
}