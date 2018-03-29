package cn.atd3.proxy.exception;

public class ProxyException extends RuntimeException {
	protected Integer code;
	protected String name;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2457230722287275134L;

	public ProxyException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ProxyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProxyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProxyException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ProxyException(String name, Integer code, String message) {
		super(message);
		this.name =name;
		this.code =code;
	}
	
	public Integer getCode() {
		return this.code;
	}

	public String getName() {
		return name;
	}
	
	
}
