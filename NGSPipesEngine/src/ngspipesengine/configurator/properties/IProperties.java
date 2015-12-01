package ngspipesengine.configurator.properties;

import java.util.List;
import java.util.Map;

import ngspipesengine.exceptions.EngineException;

public interface IProperties {

	public void set(String... args) throws EngineException;
	
	public void unset(String... args) throws EngineException;

	public Map<String, List<String>> getSetups();

	public String getInputsPath();
	
	public String getOutputsPath();
	
	public void setInputsPath(String input);
	
	public void setOutputsPath(String output);
	
	public void setFrom(int from);
	
	public void setTo(int from);
	
	public int getFrom();
	
	public int getTo();
	
	public void setPort(int port);

}
