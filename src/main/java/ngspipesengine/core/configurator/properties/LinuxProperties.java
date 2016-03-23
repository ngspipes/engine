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
package ngspipesengine.core.configurator.properties;

import ngspipesengine.core.exceptions.EngineException;

public class LinuxProperties extends Properties {
	
	public static final String BASE_CLASS_NAME = "PipeBaseClassLinux.txt";
	
	

	protected LinuxProperties(String baseEngineName, String engineName,
			String pipelinePath, int from, int to) throws EngineException {
		super(baseEngineName, engineName, pipelinePath, from, to);
	}

	public LinuxProperties(String pipelinePath, int from, int to) throws EngineException {
		this("", "", pipelinePath, from, to);
	}

	public LinuxProperties(String pipelinePath, int from) throws EngineException {
		this(pipelinePath, from, -1);
	}

	public LinuxProperties(String pipelinePath) throws EngineException {
		this(pipelinePath, -1, -1);
	}
	
	@Override
	protected String getBaseClassName() throws EngineException {
		return BASE_CLASS_NAME;
	}	
	
	@Override
	public void set(String... args) throws EngineException {}

	@Override
	public void unset(String... args) throws EngineException {}

	@Override
	public String getSpecificsArguments() throws EngineException {
		return "";
	}

	@Override
	protected void internalUpdate() {}
	
}
