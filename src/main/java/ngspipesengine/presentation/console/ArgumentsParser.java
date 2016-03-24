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
import ngspipesengine.core.configurator.properties.VMProperties;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

public class ArgumentsParser {

    private static final String APP_NAME = "NGSPipes Engine";

    public static final String PIPES_PATH = "pipes";
    public static final String IN_PATH = "in";
    public static final String OUT_PATH = "out";
    public static final String EXECUTOR_NAME = "executor";
    public static final String CPUS = "cpus";
    public static final String MEM = "mem";
    public static final String FROM_STEP = "from";
    public static final String TO_STEP = "to";

    public static final String DEFAULT_EXECUTOR_NAME = "NGSPipesEngineExecutor";
    public static final int DEFAULT_CPUS = 0;
    public static final int DEFAULT_MEM = 0;
    public static final int DEFAULT_FROM = -1;
    public static final int DEFAULT_TO = -1;



    private final CommandLineParser parser = new DefaultParser();
    private final Options options = new Options();


    public ArgumentsParser(){
        options.addOption(PIPES_PATH, true, "Pipeline path (mandatory)");
        options.addOption(IN_PATH, true, "Input absolute pathname (mandatory)");
        options.addOption(OUT_PATH, true, "Output absolute pathname (mandatory)");
        options.addOption(EXECUTOR_NAME, true, "Executor image name");
        options.addOption(CPUS, true, "Assigned cores");
        options.addOption(MEM, true, "Assigned memory");
        options.addOption(FROM_STEP, true, "Initial pipeline step");
        options.addOption(TO_STEP, true, "Final pipeline step");
    }


    public Arguments parse(String[] args) throws ParseException {
        CommandLine cmdLine = parser.parse( options, args );

        // check mandatory arguments
        if (!validateMandatoryArguments(cmdLine) || !validateArguments(cmdLine)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( APP_NAME, options );
            return null;
        }

        return createArguments(cmdLine);
    }

    private boolean validateMandatoryArguments(CommandLine cmdLine){
        if(     !cmdLine.hasOption(PIPES_PATH) ||
                !cmdLine.hasOption(IN_PATH) ||
                !cmdLine.hasOption(OUT_PATH)){

            System.err.println("Missing mandatory arguments.");
            return false;
        }

        return  true;
    }

    private boolean validateArguments(CommandLine cmdLine){
        return  validatePipesPath(cmdLine.getOptionValue(PIPES_PATH, "")) &&
                validateInPath(cmdLine.getOptionValue(IN_PATH, "")) &&
                validateOutPath(cmdLine.getOptionValue(OUT_PATH, "")) &&
                validateExecutorName(cmdLine.getOptionValue(EXECUTOR_NAME, "")) &&
                validateCpus(cmdLine.getOptionValue(CPUS, "")) &&
                validateMem(cmdLine.getOptionValue(MEM, "")) &&
                validateFromStep(cmdLine.getOptionValue(FROM_STEP, "")) &&
                validateToStep(cmdLine.getOptionValue(TO_STEP, ""));
    }

    public boolean validatePipesPath(String path){
        boolean valid = true;

        if(path == null || path.isEmpty()) {
            System.err.println("Invalid pipeline path!");
            valid = false;
        }

        if(!new File(path).exists()){
            System.err.println("Nonexistent pipeline path!");
            valid = false;
        }

        return valid;
    }

    public boolean validateInPath(String path){
        boolean valid = true;

        if(path == null || path.isEmpty()) {
            System.err.println("Invalid input path!");
            valid = false;
        }

        if(!new File(path).exists()){
            System.err.println("Nonexistent input path!");
            valid = false;
        }

        return valid;
    }

    public boolean validateOutPath(String path){
        boolean valid = true;

        if(path == null || path.isEmpty()) {
            System.err.println("Invalid output path");
            valid = false;
        }

        if(!new File(path).exists()){
            System.err.println("Nonexistent output path");
            valid = false;
        }

        return valid;
    }

    public boolean validateExecutorName(String name){
        boolean valid = true;

        if(name != null && !name.isEmpty()){
            if(!name.equals(VMProperties.BASE_VM_NAME)){
                try{
                    if(!VMEngine.getVMsName().contains(name)) {
                        System.err.println("Nonexistent executor!");
                        valid = false;
                    }
                } catch(IOException ex) {
                    System.err.println("Error validating executor name!\n" + ex.getMessage());
                }
            }
        }

        return valid;
    }

    public boolean validateCpus(String cpus){
        boolean valid = true;

        if(cpus != null && !cpus.isEmpty()) {
            try{
                int number = Integer.parseInt(cpus);

                if(number<=0){
                    System.err.println("Cpus value must be a positive!");
                    valid = false;
                }

            } catch (NumberFormatException ex) {
                System.err.println("Invalid cpus value! It must be an int.");
                valid = false;
            }
        }

        return valid;
    }

    public boolean validateMem(String mem){
        boolean valid = true;

        if(mem != null && !mem.isEmpty()) {
            try{
                int number = Integer.parseInt(mem);

                if(number<=0){
                    System.err.println("Mem value must be a positive!");
                    valid = false;
                }

            } catch (NumberFormatException ex) {
                System.err.println("Invalid mem value! It must be an int.");
                valid = false;
            }
        }

        return valid;
    }

    public boolean validateFromStep(String from){
        boolean valid = true;

        if(from != null && !from.isEmpty()) {
            try{
                int number = Integer.parseInt(from);

                if(number<=0){
                    System.err.println("From step value must be a positive!");
                    valid = false;
                }

            } catch (NumberFormatException ex) {
                System.err.println("Invalid from step value! It must be an int.");
                valid = false;
            }
        }

        return valid;
    }

    public boolean validateToStep(String to){
        boolean valid = true;

        if(to != null && !to.isEmpty()) {
            try{
                int number = Integer.parseInt(to);

                if(number<=0){
                    System.err.println("To step value must be a positive!");
                    valid = false;
                }

            } catch (NumberFormatException ex) {
                System.err.println("Invalid to step value! It must be an int.");
                valid = false;
            }
        }

        return valid;
    }

    private Arguments createArguments(CommandLine cmdLine){
        return new Arguments(
                cmdLine.getOptionValue(PIPES_PATH),
                cmdLine.getOptionValue(IN_PATH),
                cmdLine.getOptionValue(OUT_PATH),
                cmdLine.getOptionValue(EXECUTOR_NAME, DEFAULT_EXECUTOR_NAME),
                cmdLine.getOptionValue(CPUS, DEFAULT_CPUS+""),
                cmdLine.getOptionValue(MEM, DEFAULT_MEM+""),
                cmdLine.getOptionValue(FROM_STEP, DEFAULT_FROM+""),
                cmdLine.getOptionValue(TO_STEP, DEFAULT_TO+""));
    }

}
