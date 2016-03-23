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
package ngspipesengine.core.configurator.engines;

import ngspipesengine.core.configurator.properties.LinuxProperties;
import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.presentation.ui.utils.Uris;
import ngspipesengine.presentation.ui.utils.Utils;

public class LinuxEngine extends Engine {

	private static final String rootCommand = "sudo";
	private static final String TAG = "LinuxEngine";
	
	// GET LINUX TYPE cat /etc/issue
	
	
	public LinuxEngine(LinuxProperties props) throws EngineException {
		super(props);
	}

	
	public String getRootCommand() { return rootCommand; }

	@Override
	public void finish() throws EngineException {
		props.getLog().debug(TAG, "Finishing");
		System.out.println("Finishing engine");
		props.getLog().debug(TAG, "Finishing success");
		props.stopLog();
	}

	@Override
	public void clean() throws EngineException {}

	@Override
	protected void cloneEngine() throws EngineException {}

	@Override
	protected void internalStart() throws EngineException {
		props.getLog().debug(TAG, "Starting to run");

		Utils.executeCommand(getExecuteScriptCommand(), props.getLog(), TAG, "Error trying running script");

		props.getLog().debug(TAG, "Running success");
	}

	@Override
	protected String getRunnerCommand() throws EngineException {
		return getRunCommand(Uris.PIPELINES_FOLDER_PATH + props.getPipelineFolder(),
				Uris.ENGINE_PATH + Uris.SEP, props.getCompiler().getName(), 
				props.getCompiler().getMainArguments(props.getSpecificsArguments()));
	}


	
	private String getExecuteScriptCommand() {
		return rootCommand + " " + String.format(Utils.EXECUTE_SCRIPT_COMMAND, getScriptPath());
	}


	private String getScriptPath() {
		return Uris.PIPELINES_FOLDER_PATH + props.getPipelineFolder() + "execute.sh";
	}


	@Override
	public void stop() throws EngineException {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void recoverState() throws EngineException {
		// TODO Auto-generated method stub
		
	}
		
}
