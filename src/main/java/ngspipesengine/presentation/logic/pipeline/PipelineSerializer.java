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


import ngspipesengine.presentation.exceptions.EnginePresentationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

public class PipelineSerializer {

    private static final String PIPELINE_JSON_KEY = "Pipeline";
    private static final String RESULTS_JSON_KEY = "Results";
    private static final String INPUTS_JSON_KEY = "Inputs";
    private static final String ENGINE_NAME_JSON_KEY = "EngineName";
    private static final String FROM_JSON_KEY = "From";
    private static final String TO_JSON_KEY = "To";
    private static final String MEMORY_JSON_KEY = "Memory";
    private static final String PROCESSORS_JSON_KEY = "Processors";

    public static Collection<Pipeline> deserialize(String data) throws EnginePresentationException {
        Collection<Pipeline> pipelines = new LinkedList<>();

        try {
            if(data != null){
                JSONArray array = new JSONArray(data);

                for(int i=0; i<array.length(); ++i)
                    pipelines.add(deserialize(array.getJSONObject(i)));
            }
        }catch (JSONException ex) {
            throw new EnginePresentationException("Error deserialize pipelines", ex);
        }

        return pipelines;
    }

    public static Pipeline deserialize(JSONObject data) throws EnginePresentationException {
        try {
            String pipeline = data.getString(PIPELINE_JSON_KEY);
            String results = data.getString(RESULTS_JSON_KEY);
            String inputs = data.getString(INPUTS_JSON_KEY);
            String engineName = data.getString(ENGINE_NAME_JSON_KEY);
            int from = data.getInt(FROM_JSON_KEY);
            int to = data.getInt(TO_JSON_KEY);
            int memory = data.getInt(MEMORY_JSON_KEY);
            int processors = data.getInt(PROCESSORS_JSON_KEY);

            return new Pipeline(new File(pipeline), new File(results), new File(inputs), engineName, from, to, memory, processors);
        } catch(JSONException ex) {
            throw new EnginePresentationException("Error deserialize pipeline!", ex);
        }
    }


    public static String serialize(Collection<Pipeline> pipelines) throws EnginePresentationException {
        JSONArray data = new JSONArray();

        for(Pipeline pipeline : pipelines)
            data.put(serialize(pipeline));

        return data.toString();
    }

    public static JSONObject serialize(Pipeline pipeline) throws EnginePresentationException {
        JSONObject data = new JSONObject();

        try {
            data.put(PIPELINE_JSON_KEY, pipeline.getPipeline().getAbsolutePath());
            data.put(RESULTS_JSON_KEY, pipeline.getResults().getAbsolutePath());
            data.put(INPUTS_JSON_KEY, pipeline.getInputs().getAbsolutePath());
            data.put(ENGINE_NAME_JSON_KEY, pipeline.getEngineName());
            data.put(FROM_JSON_KEY, pipeline.getFrom());
            data.put(TO_JSON_KEY, pipeline.getTo());
            data.put(MEMORY_JSON_KEY, pipeline.getMemory());
            data.put(PROCESSORS_JSON_KEY, pipeline.getProcessors());
        }catch (JSONException ex) {
            throw new EnginePresentationException("Error serialize pipeline", ex);
        }

        return data;
    }

}
