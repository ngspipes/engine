package ngspipesengine.logic;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import ngspipesengine.configurator.engines.VMEngine;
import ngspipesengine.configurator.properties.VMProperties;
import ngspipesengine.utils.EngineUIException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PipelineManager {
	
	private static final Object lock = new Object();
	private static final Preferences STORE = Preferences.userNodeForPackage(PipelineManager.class);
	private static final String KEY = "Pipelines";
	private static final String PIPELINE_JSON_KEY = "Pipeline";
	private static final String RESULTS_JSON_KKEY = "Results";
	private static final String INPUTS_JSON_KEY = "Inputs";
	private static final String ENGINE_NAME_JSON_KEY = "EngineName";
	private static final String FROM_JSON_KEY = "From";
	private static final String TO_JSON_KEY = "To";
	private static final String MEMORY_JSON_KEY = "Memory";
	private static final String PROCESSORS_JSON_KEY = "Processors";
	private static Collection<Pipeline> PIPELINES = new LinkedList<>();


	public static Collection<String> getEnginesNames(){
		try {
			 return VMEngine.getVMsName();	
		} catch (Exception e) {
			Collection<String> names = new LinkedList<>();
			names.add(VMProperties.BASE_VM_NAME); 
			return names;
		}
	}

	public static Collection<Pipeline> load() throws EngineUIException {
		try {
			return deserializeData();	
		} catch (Exception ex) {
			throw new EngineUIException("Error loading pipelines!", ex);
		}
	}
	
	public static void save() throws EngineUIException{
		try{
			STORE.clear();
			STORE.put(KEY, serializeData());
			STORE.flush();
		}catch(Exception ex){
			throw new EngineUIException("Error saving pipelines!",ex);
		}
	}
	
	private static String serializeData() throws JSONException{
		JSONArray data = new JSONArray();
		
		for(Pipeline pipeline : PIPELINES)
			data.put(serialize(pipeline));
		
		return data.toString();
	}
	
	private static JSONObject serialize(Pipeline pipeline) throws JSONException {
		JSONObject data = new JSONObject();
		
		data.put(PIPELINE_JSON_KEY, pipeline.getPipeline().getAbsolutePath());
		data.put(RESULTS_JSON_KKEY, pipeline.getResults().getAbsolutePath());
		data.put(INPUTS_JSON_KEY, pipeline.getInputs().getAbsolutePath());
		data.put(ENGINE_NAME_JSON_KEY, pipeline.getEngineName());
		data.put(FROM_JSON_KEY, pipeline.getFrom());
		data.put(TO_JSON_KEY, pipeline.getTo());
		data.put(MEMORY_JSON_KEY, pipeline.getMemory());
		data.put(PROCESSORS_JSON_KEY, pipeline.getProcessors());
		
		return data;
	}
	
	private static Collection<Pipeline> deserializeData() throws JSONException, EngineUIException {
		String data = STORE.get(KEY, null);
		Collection<Pipeline> pipelines = new LinkedList<>();
		
		if(data != null){
			JSONArray jA = new JSONArray(data);
			
			for(int i=0; i<jA.length(); ++i)
				pipelines.add(deserialize(jA.getJSONObject(i)));
		}
			
		return pipelines;
	}
	
	private static Pipeline deserialize(JSONObject data) throws JSONException, EngineUIException {
		String pipeline = data.getString(PIPELINE_JSON_KEY);
		String results = data.getString(RESULTS_JSON_KKEY);
		String inputs = data.getString(INPUTS_JSON_KEY);
		String engineName = data.getString(ENGINE_NAME_JSON_KEY);
		int from = data.getInt(FROM_JSON_KEY);
		int to = data.getInt(TO_JSON_KEY);
		int memory = data.getInt(MEMORY_JSON_KEY);
		int processors = data.getInt(PROCESSORS_JSON_KEY);
		
		return new Pipeline(new File(pipeline), new File(results), new File(inputs), engineName, from, to, memory, processors); 
	}
	
	public static Collection<Pipeline> getAll(){
		return PIPELINES;
	}

	public static void add(Pipeline pipeline){
		synchronized (lock) {
			PIPELINES.add(pipeline);	
		}
	}
	
	public static void remove(Pipeline pipeline){
		PIPELINES.remove(pipeline);
	}

}
