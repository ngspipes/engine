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

import ngspipesengine.configurator.engines.VMEngine;
import ngspipesengine.configurator.properties.VMProperties;
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


	public static Collection<String> getEnginesNames(){
		try {
			 return VMEngine.getVMsName();	
		} catch (Exception e) {
			Collection<String> names = new LinkedList<>();
			names.add(VMProperties.BASE_VM_NAME); 
			return names;
		}
	}

	public static void load() throws EngineUIException {
		try {
			String data = STORE.get(KEY, null);

			synchronized (lock){
				PIPELINES =  PipelineSerializer.deserialize(data);
			}
		} catch (EngineUIException ex) {
			throw new EngineUIException("Error loading pipelines!", ex);
		}
	}
	
	public static void save() throws EngineUIException{
		try{
			STORE.clear();

			synchronized (lock){
				STORE.put(KEY, PipelineSerializer.serialize(PIPELINES));
			}

			STORE.flush();
		}catch(BackingStoreException | EngineUIException ex){
			throw new EngineUIException("Error saving pipelines!",ex);
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
