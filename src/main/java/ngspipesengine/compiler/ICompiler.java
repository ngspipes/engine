package ngspipesengine.compiler;

import ngspipesengine.exceptions.EngineException;

@FunctionalInterface
public interface ICompiler {
	
	public void compile() throws EngineException;
}
