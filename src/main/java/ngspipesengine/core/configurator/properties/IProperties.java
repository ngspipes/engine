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

import java.util.List;
import java.util.Map;

import ngspipesengine.core.exceptions.EngineException;

public interface IProperties {

	void set(String... args) throws EngineException;
	
	void unset(String... args) throws EngineException;

	Map<String, List<String>> getSetups();

	String getInputsPath();
	
	String getOutputsPath();
	
	void setInputsPath(String input);
	
	void setOutputsPath(String output);
	
	void setFrom(int from);
	
	void setTo(int from);
	
	int getFrom();
	
	int getTo();
	
	void setPort(int port);

}
