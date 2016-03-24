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
import ngspipesengine.core.utils.IO;
import ngspipesengine.presentation.exceptions.EnginePresentationException;
import ngspipesengine.presentation.logic.engine.EngineManager;
import ngspipesengine.presentation.ui.utils.Uris;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class PipelineManager {

	private static final String PREVIOUS_PIPELINES_PATH = Uris.PREVIOUS_PIPELINES + Uris.SEP + "previous_pipelines.json";
	private static final Object lock = new Object();
	private static Collection<Pipeline> PIPELINES;


	public static void load() throws EnginePresentationException {
		synchronized (lock){
			try {
				String serializedPipelines = getSerializedPipelines();

				PIPELINES = PipelineSerializer.deserialize(serializedPipelines);

				cleanPipelines(PIPELINES);
			} catch (EngineException | IOException ex) {
				throw new EnginePresentationException("Error loading pipelines!", ex);
			} finally{
				if(PIPELINES == null)
					PIPELINES = new LinkedList<>();
			}
		}
	}

	private static String getSerializedPipelines() throws EngineException, IOException {
		File previousPipelines = new File(PREVIOUS_PIPELINES_PATH);

		if(previousPipelines.exists())
			return IO.readFile(PREVIOUS_PIPELINES_PATH);

		new File(Uris.PREVIOUS_PIPELINES).mkdirs();
		previousPipelines.createNewFile();
		return null;
	}

	private static void cleanPipelines(Collection<Pipeline> pipelines) throws EnginePresentationException {
		validateEngineNames(pipelines);

		if(filterNonexistentDirectoriesOrFiles(pipelines))
			throw new EnginePresentationException("Some previous pipelines were discarded because pipeline, output or input directories doesn't exist.");
	}

	private static void validateEngineNames(Collection<Pipeline> pipelines) throws EnginePresentationException {
		String defaultEngineName = EngineManager.getDefaultEngineName();
		Collection<String> enginesNames = EngineManager.getEnginesNames();
		enginesNames.add(defaultEngineName);

		pipelines.forEach((pipeline) -> {
			if(!enginesNames.contains(pipeline.getEngineName()))
				pipeline.setEngineName(EngineManager.getDefaultEngineName());
		});
	}

	private static boolean filterNonexistentDirectoriesOrFiles(Collection<Pipeline> pipelines) {
		boolean hasPipelinesWithNonexistentDirs = false;

		for(Pipeline pipeline : new LinkedList<>(pipelines)){
			if(!pipeline.getPipeline().exists() || !pipeline.getInputDir().exists() || !pipeline.getOutputDir().exists()){
				pipelines.remove(pipeline);
				hasPipelinesWithNonexistentDirs = true;
			}
		}

		return hasPipelinesWithNonexistentDirs;
	}



	public static void save() throws EnginePresentationException {
		synchronized (lock){
			try{
				String serializedPipelines = PipelineSerializer.serialize(PIPELINES);
				IO.writeToFile(PREVIOUS_PIPELINES_PATH, serializedPipelines, false);
			} catch(EngineException ex) {
				throw new EnginePresentationException("Error saving pipelines!", ex);
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
