package service;

import java.io.*;

public class CounterService implements Serializable {

	private static final long serialVersionUID = 1L;
    
	public static synchronized Integer getCounter(final String filePath) throws ClassNotFoundException {
		Integer count = 0;
    	try {
	        final FileInputStream ipStream = new FileInputStream(filePath);
	        final BufferedInputStream bfipStream = new BufferedInputStream(ipStream);
	        final ObjectInputStream input = new ObjectInputStream(bfipStream);
	        count = (Integer) input.readObject();
	        input.close();
	        bfipStream.close();
	        ipStream.close();
    	} catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    	writeCounter(filePath, ++count);
        return count;
    }
    
    public static synchronized void writeCounter(final String filePath, final Integer count) {
    	try {
        	final FileOutputStream opStream = new FileOutputStream(filePath, false);
            final BufferedOutputStream bfopStream = new BufferedOutputStream(opStream);
            final ObjectOutputStream output = new ObjectOutputStream (bfopStream);
            output.writeObject(count);
            output.close();
            bfopStream.close();
            opStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}