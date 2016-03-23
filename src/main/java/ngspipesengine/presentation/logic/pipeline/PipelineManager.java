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

import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.presentation.ui.utils.Uris;
import ngspipesengine.presentation.exceptions.EngineUIException;
import ngspipesengine.presentation.logic.engine.EngineManager;
import ngspipesengine.core.utils.IO;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class PipelineManager {

	private static final String PREVIOUS_PIPELINES = Uris.PREVIOUS_PIPELINES + Uris.SEP + "pipelines.json";
	private static final Object lock = new Object();
	private static Collection<Pipeline> PIPELINES;


	public static void load() throws EngineUIException {
		synchronized (lock){
			try {
				String serializedPipelines = null;

				File previousPipelines = new File(PREVIOUS_PIPELINES);
				if(previousPipelines.exists())
					serializedPipelines = IO.readFile(PREVIOUS_PIPELINES);
				else{
					new File(Uris.PREVIOUS_PIPELINES).mkdirs();
					previousPipelines.createNewFile();
				}


				PIPELINES = PipelineSerializer.deserialize(serializedPipelines);
				validatePipelines(PIPELINES);
			} catch (EngineException | IOException ex) {
				throw new EngineUIException("Error loading pipelines!", ex);
			}
		}
	}

	private static void validatePipelines(Collection<Pipeline> pipelines) throws EngineUIException {
		String defaultEngineName = EngineManager.getDefaultEngineName();
		Collection<String> enginesNames = EngineManager.getEnginesNames();
		enginesNames.add(defaultEngineName);

		pipelines.forEach((pipeline)->{
			if(!enginesNames.contains(pipeline.getEngineName()))
				pipeline.setEngineName(EngineManager.getDefaultEngineName());
		});
	}
	
	public static void save() throws EngineUIException{
		synchronized (lock){
			try{
				String serializedPipelines = PipelineSerializer.serialize(PIPELINES);
				IO.writeToFile(PREVIOUS_PIPELINES, serializedPipelines, false);
			} catch(EngineException ex) {
				throw new EngineUIException("Error saving pipelines!", ex);
			}
		}
	}

	public static Collection<Pipeline> getAll(){
		synchronized (lock){
			return new LinkedList<>(PIPELINES);
		}
	}

	public static void add(Pipeline pipeline){
		synchronized (lock) {
			if(!PIPELINES.contains(pipeline))
				PIPELINES.add(pipeline);
		}
	}
	
	public static void remove(Pipeline pipeline){
		synchronized (lock){
			if(PIPELINES.contains(pipeline))
				PIPELINES.remove(pipeline);
		}
	}

}
