package ngspipesengine.utils;

public class EngineUIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public EngineUIException(){}
	
	public EngineUIException(String msg){ super(msg); }
	
	public EngineUIException(String msg, Throwable cause){ super(msg, cause); }
	
}
