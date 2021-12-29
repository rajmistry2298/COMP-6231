package exceptions;

public class InvalidUdpRequestType extends Exception {

	private static final long serialVersionUID = 1L;

	public String getMessage(String requestType) {
        return "Invalid request type: " + requestType;
    }
}
