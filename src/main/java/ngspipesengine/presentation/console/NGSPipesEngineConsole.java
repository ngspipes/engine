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

import ngspipesengine.core.configurator.engines.VMEngine;
import ngspipesengine.core.exceptions.EngineException;
import ngspipesengine.core.exceptions.ExecutorImageNotFound;
import ngspipesengine.core.utils.Utils;
import ngspipesengine.presentation.exceptions.EnginePresentationException;
import ngspipesengine.presentation.logic.engine.EngineManager;
import ngspipesengine.presentation.logic.pipeline.Pipeline;
import org.apache.commons.cli.ParseException;
import progressReporter.IProgressReporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class NGSPipesEngineConsole {

	private static final String DOWNLOAD_LINK = "http://link.inesc-id.pt/pipes/NGSPipesEngineExecutor.zip";
	private static final String WIKI_LINK = "https://github.com/ngspipes/engine/wiki#2-install-ngspipes-engine";

	
	public static void main(String[] args) {
		if(args[0].equals("clean"))
			clean(args[1]);
		else {
			Arguments arguments = getArguments(args);

			if(arguments == null)
				System.exit(2);

			run(arguments);
		}
	}

	private static void clean(String vmName) {
		try {
			VMEngine.clean(vmName);
		} catch(EngineException e) {
			System.err.println("Error cleaning " + vmName + "!\n" + Utils.getStackTrace(e));
		}
		System.out.println("VM " + vmName + " has been deleted");
	}

	private static Arguments getArguments(String[] args) {
		ArgumentsParser parser = new ArgumentsParser();

		Arguments arguments;
		try {
			arguments = parser.parse(args);
		} catch(ParseException ex){
			System.err.println( "Parsing failed.  Reason: " + ex.getMessage() );
			System.exit(1);
			return null;
		}

		return arguments;
	}

	private static void run(Arguments arguments) {
		try {
			if(!VMEngine.acceptedVBoxVersion()) {
				System.err.println("Your current version of Virtual Box is to old.\n" +
						"Yo should have at least 4.3.20 version");
				System.exit(3);
			}

			VMEngine.register();

			int id = runPipeline(arguments);

			waitForFinish(id);
		} catch (ExecutorImageNotFound ex) {
			StringBuilder sb =  new StringBuilder();
			sb.append("\n");
			sb.append("\t ** Error opening the executor image file **\n");
			sb.append("\t Please:\n");
			sb.append("\t\t 1. download it from here " + DOWNLOAD_LINK + "\n");
			sb.append("\t\t 2. place it inside the working directory as explained here " + WIKI_LINK + "\n");
			sb.append("\n");
			sb.append("\t Thank you for using NGSPipes!");
			System.out.print(sb.toString());
		} catch(Exception ex) {
			System.out.println("::ERROR running engine :: \nstackTrace: " + Utils.getStackTrace(ex));
			System.exit(1);
		}
	}

	private static int runPipeline(Arguments arguments) throws EnginePresentationException {
		IProgressReporter reporter = new ConsoleReporter();
		Pipeline pipeline = getPipeline(arguments);

		int id = EngineManager.run(pipeline, reporter, Throwable::printStackTrace);

		System.out.println(
				String.format("Starting executor with %d CPUs and %d GBytes",
						pipeline.getProcessors(),
						pipeline.getMemory()/1024));

		return id;
	}

	private static Pipeline getPipeline(Arguments arguments) throws EnginePresentationException {
		Pipeline pipeline = new Pipeline(
				new File(arguments.pipesPath),
				new File(arguments.outPath),
				new File(arguments.inputPath),
				arguments.executorName
		);

		if (!arguments.fromStep.equals(ArgumentsParser.DEFAULT_FROM+""))
			pipeline.setFrom(Integer.parseInt(arguments.fromStep));

		if (!arguments.toStep.equals(ArgumentsParser.DEFAULT_TO+""))
			pipeline.setTo(Integer.parseInt(arguments.toStep));

		if (!arguments.mem.equals(ArgumentsParser.DEFAULT_MEM+""))
			pipeline.setMemory(Integer.parseInt(arguments.mem) * 1024);

		if (!arguments.cpus.equals(ArgumentsParser.DEFAULT_CPUS+""))
			pipeline.setProcessors(Integer.parseInt(arguments.cpus));

		return pipeline;
	}

	private static void waitForFinish(int id)throws EnginePresentationException, IOException {
		System.out.println("\t::**Press ENTER to cancel**::");

		try(BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))){
			while(!bf.ready() && EngineManager.getAllRunningPipelines().contains(id))
				try{ Thread.sleep(1000); } catch(InterruptedException ex) {}

			if(bf.ready() && EngineManager.getAllRunningPipelines().contains(id)){
				System.out.println("Canceling execution");
				EngineManager.stop(id);
			}
		}
	}

}
