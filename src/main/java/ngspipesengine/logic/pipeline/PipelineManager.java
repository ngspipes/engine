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
package ngspipesengine.logic.pipeline;

import ngspipesengine.logic.engine.EngineManager;
import ngspipesengine.utils.EngineUIException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PipelineManager {
	
	private static final Object lock = new Object();
	private static final Preferences STORE = Preferences.userNodeForPackage(PipelineManager.class);
	private static final String KEY = "Pipelines";
	private static Collection<Pipeline> PIPELINES;


	public static void load() throws EngineUIException {
		synchronized (lock){
			try {
				String data = STORE.get(KEY, null);
				Collection<Pipeline> pipelines = PipelineSerializer.deserialize(data);
				validatePipelines(pipelines);
				PIPELINES =  pipelines;
			} catch (EngineUIException ex) {
				throw new EngineUIException("Error loading pipelines!", ex);
			}
		}
	}

	private static void validatePipelines(Collection<Pipeline> pipelines) {
		Collection<String> enginesNames = EngineManager.getEnginesNames();

		for(Pipeline pipeline : pipelines){
			if(!enginesNames.contains(pipeline.getEngineName()))
				pipeline.setEngineName(EngineManager.getEngineDefaultName());
		}
	}
	
	public static void save() throws EngineUIException{
		synchronized (lock){
			try{
				STORE.clear();
				STORE.put(KEY, PipelineSerializer.serialize(PIPELINES));
				STORE.flush();
			} catch(BackingStoreException | EngineUIException ex) {
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
