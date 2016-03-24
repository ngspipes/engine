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
package ngspipesengine.presentation.logic.engine;

import exceptions.ProgressReporterException;
import ngspipesengine.core.configurator.engines.IEngine;
import ngspipesengine.core.configurator.engines.VMEngine;
import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.presentation.exceptions.EnginePresentationException;
import ngspipesengine.presentation.logic.pipeline.Pipeline;
import ngspipesengine.presentation.utils.WorkQueue;
import progressReporter.IProgressReporter;
import progressReporter.SocketReporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EngineRunner {

    private static final String TRACE_TAG = SocketReporter.TRACE_TAG;
    private static final String ERROR_TAG = SocketReporter.ERROR_TAG;
    private static final String INFO_TAG = SocketReporter.INFO_TAG;
    private static final int ACCEPT_CLIENT_TIMEOUT = 1000;
    private static final int SOCKET_PORT = 0;



    private final CountDownLatch finished = new CountDownLatch(1);
    private final CountDownLatch runningPipeline = new CountDownLatch(1);
    private final AtomicBoolean stop = new AtomicBoolean();
    private IEngine engine;
    private ServerSocket socket;
    private Socket client;

    private final Pipeline pipeline;
    private final IProgressReporter reporter;
    private final Consumer<EnginePresentationException> onException;



    public EngineRunner(Pipeline pipeline, IProgressReporter reporter, Consumer<EnginePresentationException> onException){
        this.pipeline = pipeline;
        this.reporter = reporter;
        this.onException = onException;
    }



    public void start() {
        try{
            WorkQueue.run(this::runServer);
        } catch(RuntimeException ex) {
            finished.countDown();
            stop.set(true);
            throw ex;
        }
    }

    public void stop()throws EngineException {
        stop.set(true);
        stopVM();
    }

    public boolean finished(){
        return finished.getCount() == 0;
    }

    public void waitForFinish() throws InterruptedException {
        finished.await();
    }



    private void runServer() {
        try{
            if(initServer()){
                initEngine();

                try {
                    attendClient();
                } catch (ProgressReporterException | IOException e) {
                    waitPipelineRunning();
                    try{ stopVM(); } catch (EngineException ex) {
                        onException.accept(new EnginePresentationException("Error stopping Engine!", ex));
                    }
                    onException.accept(new EnginePresentationException("Error on communication channel!", e));
                }
            }
        }finally{
            close();
        }
    }

    private boolean initServer() {
        try {
            socket = new ServerSocket(SOCKET_PORT);
            socket.setSoTimeout(ACCEPT_CLIENT_TIMEOUT);
            reporter.open();
            return true;
        } catch (IOException | ProgressReporterException e) {
            onException.accept(new EnginePresentationException("Error initializing server!", e));
            return false;
        }
    }

    private void initEngine() {
        pipeline.properties.setPort(socket.getLocalPort());
        WorkQueue.run(()->{
            try{
                runPipeline();
            } catch(EngineException ex) {
                onException.accept(new EnginePresentationException("Error running pipeline!", ex));
            }
        });
    }

    private void attendClient() throws IOException, ProgressReporterException {
        client = acceptClient();

        if(client!=null)
            answerClient();
    }

    public void waitPipelineRunning(){
        try {
            runningPipeline.await();
            Thread.sleep(2000);
        } catch (InterruptedException ex) {}
    }

    private void runPipeline() throws EngineException {
        try {
            engine = new VMEngine(pipeline.properties);
            runningPipeline.countDown();
            engine.start();
        } catch(EngineException ex) {
            if(runningPipeline.getCount() != 0)
                runningPipeline.countDown();

            throw ex;
        } finally {
            try{
                engine.finish();
            } catch(EngineException e) {
                throw e;
            } finally{
                finished.countDown();
                stop.set(true);
            }
        }
    }

    private Socket acceptClient() throws IOException {
        while(!stop.get()){
            try{
                return socket.accept();
            }catch(SocketTimeoutException ex){
                /*Timout is set to 1000 seconds, to leave accept() and check while condition*/
            }
        }

        return null;
    }

    private void answerClient() throws IOException, ProgressReporterException {
        Reader stream = new InputStreamReader(client.getInputStream());

        try(BufferedReader in = new BufferedReader(stream)){
            String line;
            while ((line = in.readLine()) != null && !stop.get())
                report(line);
        }
    }

    private void report(String line) throws ProgressReporterException {
        if(line.startsWith(TRACE_TAG))
            reporter.reportTrace(line.substring(TRACE_TAG.length()));
        else if(line.startsWith(ERROR_TAG))
            reporter.reportError(line.substring(ERROR_TAG.length()));
        else if(line.startsWith(INFO_TAG))
            reporter.reportInfo(line.substring(INFO_TAG.length()));
        else
            reporter.reportInfo(line);
    }


    private void stopVM() throws EngineException {
        if(engine != null)
            engine.stop();
    }


    private void close() {
        try{
            closeSocket();
        } catch(IOException e) {
            onException.accept(new EnginePresentationException("Error closing EngineRunner socket!", e));
        }

        try{
            closeClient();
        } catch(IOException e) {
            onException.accept(new EnginePresentationException("Error closing EngineRunner client!", e));
        }

        try{
            closeReporter();
        } catch(ProgressReporterException e) {
            onException.accept(new EnginePresentationException("Error closing EngineRunner reporter!", e));
        }
    }

    private void closeSocket() throws IOException {
        if(socket != null && !socket.isClosed())
            socket.close();
    }

    private void closeClient() throws IOException {
        if(client != null && !client.isClosed())
            client.close();
    }

    private void closeReporter() throws ProgressReporterException {
        if(reporter != null)
                reporter.close();
    }

}
