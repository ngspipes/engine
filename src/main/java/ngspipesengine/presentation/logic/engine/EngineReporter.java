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
import progressReporter.IProgressReporter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EngineReporter implements IProgressReporter {

    private final LinkedBlockingQueue<String> traceQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> errorQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> infoQueue = new LinkedBlockingQueue<>();

    private final AtomicBoolean closed = new AtomicBoolean();


    public void open(){}

    public void reportTrace(String msg) throws ProgressReporterException {
        if(isClosed())
            return;

        try {
            traceQueue.put(msg);
        } catch(InterruptedException ex) {
            throw new ProgressReporterException("Thread was interrupted!", ex);
        }
    }

    public void reportError(String msg) throws ProgressReporterException {
        if(isClosed())
            return;

        try {
            errorQueue.put(msg);
        } catch(InterruptedException ex) {
            throw new ProgressReporterException("Thread was interrupted!", ex);
        }
    }

    public void reportInfo(String msg) throws ProgressReporterException {
        if(isClosed())
            return;

        try {
            infoQueue.put(msg);
        } catch(InterruptedException ex) {
            throw new ProgressReporterException("Thread was interrupted!", ex);
        }
    }

    public void close(){
        closed.set(true);
    }



    public String getTrace(long timeout, TimeUnit unit) throws InterruptedException {
        if(isClosed() && traceQueue.isEmpty())
            return null;

        return traceQueue.poll(timeout, unit);
    }

    public String getError(int timeout, TimeUnit unit) throws InterruptedException {
        if(isClosed() && errorQueue.isEmpty())
            return null;

        return errorQueue.poll(timeout, unit);
    }

    public String getInfo(int timeout, TimeUnit unit) throws InterruptedException {
        if(isClosed() && infoQueue.isEmpty())
            return null;

        return infoQueue.poll(timeout, unit);
    }

    public boolean isClosed(){
        return closed.get();
    }

}
