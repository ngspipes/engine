package ngspipesengine.configurator.engines;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import ngspipesengine.configurator.properties.LinuxProperties;
import ngspipesengine.configurator.properties.VMProperties;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.Utils;

public class EngineSupplier {

	private static String TAG = "EngineSupplier";
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
