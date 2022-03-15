import java.util.Formatter;

public class DataParser {
	/**
	 * Parses the byte array to match request data format ie. a read request of file test.txt and mode netascii would return: "01 test.txt netascii"
	 * @param data the byte array to parse
	 * @return the formatted string 
	 */
	public static String parseRequest(byte[] data) {
		
		String all=null;
		try {
			String text = new String(data).substring(2);
			all = new Formatter().format("%o%o%s", data[0], data[1],text).toString();
		}catch(IndexOutOfBoundsException ex){
			ex.printStackTrace();
		}
		
		return all;
	}
	
}
