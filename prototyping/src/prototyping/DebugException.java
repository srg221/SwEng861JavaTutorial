package prototyping;

public class DebugException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message = null;

	public DebugException() {
		super();
	}

	public DebugException(String message) {
		super(message);
		this.message = message;
	}

	public DebugException(Throwable cause) {
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
