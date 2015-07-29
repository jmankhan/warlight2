package debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Logging class that writes to file for debugging purposes
 * @author jalal
 *
 */
public class Log {
	
	private static PrintWriter writer;
	
	public Log() {
		try { 
			File file = new File("/home/jalal/workspace/warlight2-engine/Log.txt");
			file.createNewFile();
			
			writer = new PrintWriter(file);
			
		} catch(IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Writes parameter to file, appending a newline
	 * @param output
	 */
	public static void log(String output) {
		writer.append(output + "\n");
	}
	
	/**
	 * Closes writer stream
	 */
	public static void close() {
		writer.close();
	}
}
