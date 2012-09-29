
package zoo.themenroute;


/**
 * This is a custom <code>Exception</code> that is thrown if any exception in 
 * the <code>HTTPRequestHandler</code> class occurs.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
@SuppressWarnings("serial")
public class HTTPRequestException extends Exception {

	private String err_message = "";
	
	/**
	 * Constructor
	 * @param error_message any error message
	 */
	public HTTPRequestException(String error_message) {
		
		this.err_message = error_message;
	}
	
	/**
	 * Returns the error message string.
	 * @return error message
	 */
	public String getErrorMessage() {
		
		return err_message;
	}
}
