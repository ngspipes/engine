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

import exceptions.ProgressReporterException;
import javafx.application.Platform;
import ngspipesengine.configurator.engines.IEngine;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.utils.Dialog;
import ngspipesengine.utils.WorkQueue;
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

public class EngineRunner {

    private static final String TRACE_TAG = SocketReporter.TRACE_TAG;
    private static final String ERROR_TAG = SocketReporter.ERROR_TAG;
    private static final String INFO_TAG = SocketReporter.INFO_TAG;
    private static final int ACCEPT_CLIENT_TIMEOUT = 1000;
    private static final int SOCKET_PORT = 0;



    private final CountDownLatch finished = new CountDownLatch(1);
    private final CountDownLatch runningPipeline = new CountDownLatch(1);
    private final AtomicBoolean stop = new AtomicBoolean();
    private final EngineUIReporter reporter;
    private final IEngine engine;
    private ServerSocket socket;
    private Socket client;



    public EngineRunner(IEngine engine, EngineUIReporter reporter){
        this.engine = engine;
        this.reporter = reporter;
    }



    public void start() {
        try{
            WorkQueue.run(this::runServer);
            WorkQueue.run(this::runPipeline);
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
        try {
            initSocket();

            reporter.open();

            attendClient();
        } catch (ProgressReporterException | IOException e) {
            waitPipelineRunning();
            try{ stopVM(); } catch (EngineException ex) {}
            Platform.runLater(()->Dialog.showError("Error on communication channel!"));
        } finally{
            close();
        }
    }

    private void initSocket() throws IOException {
        socket = new ServerSocket(SOCKET_PORT);
        socket.setSoTimeout(ACCEPT_CLIENT_TIMEOUT);
    }

    private void attendClient() throws IOException, ProgressReporterException {
        client = acceptClient();

        if(client!=null)
            answerClient();
    }

    public void waitPipelineRunning(){
        try {
            runningPipeline.await();
            Thread.sleep(1000);
        } catch (InterruptedException ex) {}
    }

    private void runPipeline() {
        runningPipeline.countDown();

        try {
            engine.start();
        } catch(EngineException ex) {
            stop.set(true);
            Platform.runLater(()-> Dialog.showError(ex.getMessage()));
        } finally {
            try{
                engine.finish();
            } catch(EngineException e) {
                Platform.runLater(()-> Dialog.showError(e.getMessage()));
            }
            finished.countDown();
            stop.set(true);
        }
    }

    private Socket acceptClient() throws IOException {
        while(!stop.get()){
            try{
                return socket.accept();
            }catch(SocketTimeoutException ex){}
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


    private void close(){
        IOException socketException = closeSocket();
        IOException clientException = closeClient();
        ProgressReporterException reporterException = closeReporter();

        if(socketException != null || clientException != null || reporterException != null)
            Platform.runLater(()->Dialog.showError("Error closing EngineRunner!"));
    }

    private IOException closeSocket() {
        if(socket != null && !socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {
                return e;
            }
        }

        return null;
    }

    private IOException closeClient() {
        if(client != null && !client.isClosed()){
            try {
                client.close();
            } catch (IOException e) {
                return e;
            }
        }

        return null;
    }

    private ProgressReporterException closeReporter() {
        reporter.close();
        return null;
    }

}
