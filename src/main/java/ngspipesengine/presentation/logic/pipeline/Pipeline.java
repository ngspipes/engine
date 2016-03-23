/*-
 * Copyright (c) 2016, NGSPipes Team <ngspipes@gmail.com>
 * All rights reserved.
 *
 * This file is part of NGSPipes <http://ngspipes.github.io/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ngspipesengine.presentation.logic.pipeline;

import ngspipesengine.core.configurator.properties.VMProperties;
import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.presentation.exceptions.EngineUIException;

import java.io.File;

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

	@Override
	public boolean equals(Object o) {
		if(o == this)
			return true;

		if(o == null || !(o instanceof Pipeline))
			return false;

		Pipeline other = (Pipeline)o;

		return 	equals(this.pipeline, other.pipeline) &&
				equals(this.results, other.results) &&
				equals(this.inputs, other.inputs) &&
				equals(this.engineName, other.engineName) &&
				this.from == other.from &&
				this.to == other.to &&
				this.totalSteps == other.totalSteps &&
				this.memory == other.memory &&
				this.processors == other.processors;
	}

	public static boolean equals(Object a, Object b){
		if(a == b)
			return true;

		if((a == null && b != null) || (a != null && b == null))
			return false;

		return a.equals(b);
	}

}
