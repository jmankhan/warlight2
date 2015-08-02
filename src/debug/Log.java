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
	 * Writes output to default log file, appending a newline
	 * @param output
	 */
	public static void log(String output) {
		writer.append(output + "\n");
	}

	/**
	 * Writes output to specified file, appending a newline
	 * @param file
	 * @param output
	 */
	public static void log(File file, String output) {
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.append(output + "\n");
			pw.close();
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
	}
	/**
	 * Closes writer stream
	 */
	public static void close() {
		writer.close();
	}
}
