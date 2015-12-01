package ngspipesengine.logic;

import java.io.File;

import ngspipesengine.configurator.properties.VMProperties;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.EngineUIException;

public class Pipeline {
	
	private final File pipeline;
	public File getPipeline() { return pipeline; }
	
	private File results;
	public File getResults() { return results; }
	public void setResults(File results) { 
		this.results = results; 
		properties.setOutputsPath(results.getAbsolutePath());
	}
	
	private File inputs;
	public File getInputs() { return inputs; }
	public void setInputs(File inputs) { 
		this.inputs = inputs;
		properties.setInputsPath(inputs.getAbsolutePath());
	}
	
	private String engineName;
	public String getEngineName() { return engineName; }
	public void setEngineName(String engineName) { 
		this.engineName = engineName; 
		properties.setEngineName(engineName);
	}
	
	private int from;
	public int getFrom() { return from; }
	public void setFrom(int from) { 
		this.from = from;
		properties.setFrom(from);
		setMemory(properties.getMemory());
	}
	
	private int to;
	public int getTo() { return to; }
	public void setTo(int to) {
		this.to = to;
		properties.setTo(to);
		setMemory(properties.getMemory());
	}
	
	private int totalSteps;
	public int getTotalSteps() { return totalSteps; }
	
	private int memory;
	public int getMemory(){ return memory; }
	public void setMemory(int memory){
		this.memory = memory;
		properties.setMemory(memory);
	}
	
	private int processors;
	public int getProcessors(){ return processors; }
	public void setProcessors(int processors){
		this.processors = processors;
		properties.setProcessorsNumber(processors);
	}
	
	public final VMProperties properties;
	
	public Pipeline(File pipeline, File results, File inputs, String engineName, int from, int to, int memory, int processors) throws EngineUIException {
		this.pipeline = pipeline;
		try{
			this.properties = new VMProperties(pipeline.getAbsolutePath());	
		}catch(EngineException ex){
			throw new EngineUIException("Error loading pipeline!",ex);
		}
		
		setResults(results);
		setInputs(inputs);
		setEngineName(engineName);
		setFrom(from);
		setTo(to);
		setMemory(memory);
		setProcessors(processors);
		this.totalSteps = properties.getTotalSteps();
	}
	
	public Pipeline(File pipeline, File results, File inputs, String engineName) throws EngineUIException {
		this.pipeline = pipeline;
		try{
			this.properties = new VMProperties(pipeline.getAbsolutePath());	
		}catch(EngineException ex){
			throw new EngineUIException("Error loading pipeline!",ex);
		}

		setResults(results);
		setInputs(inputs);
		setEngineName(engineName);
		setFrom(1);
		setTo(properties.getTotalSteps());
		
		this.totalSteps = properties.getTotalSteps();
		this.memory = properties.getMemory();
		this.processors = properties.getProcessorsNumber();
	}

}
