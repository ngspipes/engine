package ngspipesengine.exceptions;

public class EngineException extends Exception {

	private static final long serialVersionUID = 1L;

	public EngineException(){}
	
	public EngineException(String msg){ super(msg); }
	
	public EngineException(String msg, Throwable cause){ super(msg, cause); }
}
