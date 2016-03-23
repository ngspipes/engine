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
package ngspipesengine.presentation.console;


public class Arguments {

    public String pipesPath;
    public String inputPath;
    public String outPath;
    public String executorName;
    public String cpus;
    public String mem;
    public String fromStep;
    public String toStep;

    public Arguments() { }

    public Arguments(String pipesPath, String inPath, String outPath,
                     String executorName, String cpus, String mem, String fromStep, String toStep){
        this.pipesPath = pipesPath;
        this.inputPath = inPath;
        this.outPath = outPath;
        this.executorName = executorName;
        this.cpus = cpus;
        this.mem = mem;
        this.fromStep = fromStep;
        this.toStep = toStep;
    }

}
