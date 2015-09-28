package prototyping;

public class TokenNotFoundException extends Exception{

	private static final long serialVersionUID = 1L;
	private String message = null;

	public TokenNotFoundException() {
		super();
	}

	public TokenNotFoundException(String message) {
		super(message);
		this.message = message;
	}

	public TokenNotFoundException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}