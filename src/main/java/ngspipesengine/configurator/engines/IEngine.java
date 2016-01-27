package ngspipesengine.configurator.engines;

import ngspipesengine.exceptions.EngineException;

public interface IEngine {
	
	public void start() throws EngineException;
	
	public void finish() throws EngineException;
	
	public void clean() throws EngineException;
        
	public void stop() throws EngineException;
}
