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
package ngspipesengine.logic.engine;

import ngspipesengine.exceptions.EngineException;
import ngspipesengine.logic.pipeline.Pipeline;
import ngspipesengine.utils.EngineUIException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EngineManager {

    private static final Object LOCK = new Object();

    private static int id = 0;

    private static final int CLEAN_INTERVAL = 5*1000;
    private static final Timer TIMER = new Timer("EngineManager runners cleaner", true);

    private static final Map<Integer, EngineRunner> RUNNERS = new HashMap<>();



    static{
        TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanRunners();
            }
        }, CLEAN_INTERVAL, CLEAN_INTERVAL);
    }



    public static int run(Pipeline pipeline, EngineUIReporter reporter) throws EngineUIException {
        if(pipeline == null || reporter == null)
            throw new IllegalArgumentException("Pipeline and reporter can not be null!");

        synchronized(LOCK) {
            EngineRunner runner = new EngineRunner(pipeline, reporter);
            runner.start();
            RUNNERS.put(++id, runner);
            return id;
        }
    }

    public static void stop(int id) throws EngineUIException {
        synchronized (LOCK){
            EngineRunner runner = RUNNERS.get(id);

            if(runner == null)
                return;

            try{
                if(!runner.finished())
                    runner.stop();
            } catch(EngineException ex) {
                throw new EngineUIException("Error stopping Engine!", ex);
            }
        }
    }

    public static void stopAllRunningPipelines() throws EngineUIException {
        EngineUIException ex = null;

        synchronized (LOCK){
            for(Integer id : getAllRunningPipelines()){
                try{
                    stop(id);
                } catch (EngineUIException e) {
                    ex = e;
                }
            }
        }

        if(ex != null)
            throw ex;
    }

    public static Collection<Integer> getAllRunningPipelines() {
        synchronized (LOCK){
            Stream<Integer> ids = RUNNERS.keySet().parallelStream();

            ids = ids.filter((id)->!RUNNERS.get(id).finished());

            return ids.count() == 0? new LinkedList<>() : ids.collect(Collectors.toList());
        }
    }

    private static void cleanRunners(){
        synchronized (LOCK){
            for(Integer id : RUNNERS.keySet()){
                EngineRunner runner = RUNNERS.get(id);
                if(runner.finished())
                    RUNNERS.remove(id);
            }
        }
    }

}
