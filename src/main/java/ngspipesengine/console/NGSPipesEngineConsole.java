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
package ngspipesengine.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import ngspipesengine.configurator.engines.VMEngine;
import ngspipesengine.configurator.properties.VMProperties;
import ngspipesengine.exceptions.EngineException;
import ngspipesengine.exceptions.ExecutorImageNotFound;
import ngspipesengine.utils.Utils;

import org.json.JSONException;
import org.apache.commons.cli.*;

public class NGSPipesEngineConsole {


	public static final String FROM_STEP = "from";
	public static final String TO_STEP = "to";
	public static final String EXECUTOR_NAME = "executor";
	private static final String APP_NAME = "NGSPipes Engine";
	private static final String DOWNLOAD_LINK = "http://link.inesc-id.pt/pipes/NGSPipesEngineExecutor.zip";
	private static final String WIKI_LINK = "https://github.com/ngspipes/engine/wiki#2-install-ngspipes-engine";
	private static String DEFAULT_EXECUTOR_NAME = "NGSPipesEngineExecutor";
	private static int DEFAULT_FROM = -1;
	private static int DEFAULT_TO = -1;
	public static final String PIPES_PATH = "pipes";
	public static final String IN_PATH = "in";
	public static final String OUT_PATH = "out";
	public static final String CPUS = "cpus";
	public static final String MEM = "mem";
	static ServerSocket serverSocket;
	
	public static void main(String[] args) {

		Options options = new Options();
		options.addOption(PIPES_PATH, true, "Pipeline path (mandatory)");
		options.addOption(IN_PATH, true, "Input absolute pathname (mandatory)");
		options.addOption(OUT_PATH, true, "Output absolute pathname (mandatory)");
		options.addOption(EXECUTOR_NAME, true, "Executor image name");
		options.addOption(CPUS, true, "Assigned cores");
		options.addOption(MEM, true, "Assigned memory");
		options.addOption(FROM_STEP, true, "Initial pipeline step");
		options.addOption(TO_STEP, true, "Final pipeline step");

		// create the parser
		CommandLineParser parser = new DefaultParser();
		CommandLine cmdLine = null;
		try {
			// parse the command line arguments
			cmdLine = parser.parse( options, args );

			// check mandatory arguments
			if (!cmdLine.hasOption(PIPES_PATH) || !cmdLine.hasOption(IN_PATH) || !cmdLine.hasOption(OUT_PATH)) {
				System.err.println("Missing mandatory arguments.");
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( APP_NAME, options );
				System.exit(-1);
			}
		}
		catch( ParseException exp ) {
			// oops, something went wrong
			System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
			System.exit(1);
		}

		try {
			if(!VMEngine.acceptedVBoxVersion()) {
				System.out.println("Your current version of Virtual Box is to old.\n" +
						"Yo should have at least 4.3.20 version");
				System.exit(0);
			}
			VMEngine.register();

			switch(args[0]) {
				case "clean" : clean(args[1]);
				default : runEngine(cmdLine);
			}

		} catch (ExecutorImageNotFound ex) {
			System.out.println();
			System.out.println("\t ** Error opening the executor image file **");
			System.out.println("\t Please:");
			System.out.println("\t\t 1. download it from here " + DOWNLOAD_LINK);
			System.out.println("\t\t 2. place it inside the working directory as explained here " + WIKI_LINK);
			System.out.println();
			System.out.println("\t Thank you for using NGSPipes!");
		} catch(Exception ex) {
			System.out.println("::ERROR running engine :: \nstackTrace: " + Utils.getStackTrace(ex));
		} finally {
			System.exit(1);
		}
	}

	private static void runEngine(CommandLine cmdLine) throws JSONException, EngineException, IOException {
		VMProperties vmProperties = getVMProperties(cmdLine);
		VMEngine vm = new VMEngine(vmProperties);

		System.out.println(
				String.format("Starting executor with %d CPUs and %d GBytes",
						vmProperties.getProcessorsNumber(),
						vmProperties.getMemory()/1024));

		vm.start();
		initServer(vmProperties);
		
		System.out.println("Wait to finish engine");
		vm.finish();
	}

	private static void clean(String vmName) throws EngineException {
		try {
			VMEngine.clean(vmName);
		} catch(EngineException e) {
			throw new EngineException("Error cleaning engine", e);
		}
		System.out.println("VM " + vmName + " has been deleted");
	}

	private static VMProperties getVMProperties(CommandLine props) throws JSONException, EngineException, IOException {
		serverSocket =	new ServerSocket(0);

		// build initial VMProperties with optional arguments
		VMProperties propsVM = null;
		propsVM = new VMProperties(
				props.hasOption(EXECUTOR_NAME) ? props.getOptionValue(EXECUTOR_NAME) : DEFAULT_EXECUTOR_NAME,
				props.getOptionValue(PIPES_PATH),
				props.hasOption(FROM_STEP) ? Integer.parseInt(props.getOptionValue(FROM_STEP)) : DEFAULT_FROM,
				props.hasOption(TO_STEP) ? Integer.parseInt(props.getOptionValue(TO_STEP)) : DEFAULT_TO
		);

		// mandatory arguments
		propsVM.setInputsPath(props.getOptionValue(IN_PATH));
		propsVM.setOutputsPath(props.getOptionValue(OUT_PATH));
		propsVM.setPort(serverSocket.getLocalPort());

		// more optional arguments
		if (props.hasOption(CPUS)) {
			propsVM.setProcessorsNumber(Integer.parseInt(props.getOptionValue(CPUS)));
		}
		if (props.hasOption(MEM)) {
			propsVM.setMemory(Integer.parseInt(props.getOptionValue(MEM)) * 1024);
		}
		
		return propsVM;
	}

	public static void initServer(VMProperties vmProperties) throws IOException {

		try {
			Socket clientSocket = serverSocket.accept();   
			System.out.println("Connection accepted with: " + clientSocket.getInetAddress());
			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			
			String inputLine;
			while ((inputLine = in.readLine()) != null) 
				System.out.println(inputLine);
			
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port "
					+ serverSocket.getLocalPort() + " or listening for a connection");
			System.out.println(e.getMessage());
		} finally {
			if(!serverSocket.isClosed())
				serverSocket.close();
		}
	}	
}
