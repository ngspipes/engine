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
import ngspipesengine.core.configurator.properties.VMProperties;
import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.core.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EngineSupplier {

	private static final String TAG = "EngineSupplier";
	private static final Map<String, Function<String[], IEngine>> ENGINE_SUPPlIER;
	
	static {
		ENGINE_SUPPlIER = new HashMap<>();
		ENGINE_SUPPlIER.put(Utils.WIN_OS_TYPE, EngineSupplier::getDefaultEngine);
		ENGINE_SUPPlIER.put(Utils.UNIX_OS_TYPE, EngineSupplier::getLinuxEngine);

	}
	
	private static IEngine getDefaultEngine(String[] props) {
		VMProperties properties = null;
		try {
			properties = getEngineProperties(props);
			return new VMEngine(properties);
		} catch (Exception e) {
			properties.getLog().error(TAG, "Error getting engine: " + e.getMessage());
		}
		return null;
	}
	
	private static IEngine getLinuxEngine(String[] props) {
		LinuxProperties properties = null;
		try {
			properties = getLinuxEngineProperties(props);
			return new LinuxEngine(properties);
		} catch (Exception e) {
			properties.getLog().error(TAG, "Error getting engine: " + e.getMessage());
		}
		return null;
	}
	
	public static IEngine getEngine(String[] props) {
		return ENGINE_SUPPlIER.get(Utils.getOSType()).apply(props);
	}

	private static LinuxProperties getLinuxEngineProperties(String[] props) throws NumberFormatException, EngineException {
		// TO UPDATE IS BAD
		LinuxProperties propsLinux;
		
		if(props.length == 6)
			propsLinux = new LinuxProperties(props[0], Integer.parseInt(props[4]), Integer.parseInt(props[5]));
		
		if(props.length == 5)
			propsLinux = new LinuxProperties(props[0], Integer.parseInt(props[4]));

		propsLinux = new LinuxProperties(props[0]);
		
		propsLinux.setInputsPath(props[1]);
		propsLinux.setOutputsPath(props[2]);
		
		return propsLinux;
	}

	private static VMProperties getEngineProperties(String[] props) throws NumberFormatException, EngineException {
		
		VMProperties propsVM;

		if(props.length == 8)
			propsVM = new VMProperties(props[3], props[0], Integer.parseInt(props[6]), Integer.parseInt(props[7]));
		if(props.length == 7)
			propsVM = new VMProperties(props[3], props[0], Integer.parseInt(props[6]));

		propsVM = new VMProperties(props[3], props[0]);

		propsVM.setInputsPath(props[1]);
		propsVM.setOutputsPath(props[2]);
		propsVM.setProcessorsNumber(Integer.parseInt(props[4]));
		propsVM.setProcessorsNumber(Integer.parseInt(props[5]));
		
		return propsVM;
	}
}
