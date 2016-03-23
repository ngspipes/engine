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
package ngspipesengine.presentation.ui.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import ngspipesengine.core.exceptions.EngineException;

public class IO {

    public static String readFileAbsolutePath(URL absolutePath, String lineSeparator) throws EngineException{
        BufferedReader reader = null;
        StringBuilder fileContent = new StringBuilder();
        String line;
        try {
                reader = new BufferedReader(new InputStreamReader(absolutePath.openStream(), "UTF-8"));

                while((line = reader.readLine()) != null)
                        fileContent.append(line).append(lineSeparator);
        } catch (IOException e) {
                throw new RuntimeException("Error trying to read file \n" + e.getMessage());
        } finally {
                closeBufferedReader(reader);
        }

        return fileContent.toString();
    }

    public static String readFileAbsolutePath(URL absolutePath) throws EngineException{
    	return readFileAbsolutePath(absolutePath, "\n");
    }
    
    public static String readFile(String filePath) throws MalformedURLException, EngineException{
        return readFileAbsolutePath(getURL(filePath));
    }
    
    public static void writeToFile(String filePath, String content) throws EngineException {
        writeToFile(filePath, content, false);
    }	

    public static void writeToFile(String filePath, String content, boolean addToFileContent) throws EngineException {
        FileOutputStream fos = null;

        try{
                fos  = new FileOutputStream(filePath, addToFileContent);
                fos.write(content.getBytes());

        } catch(IOException e) {
                throw new EngineException("File Output error!", e);
        } finally {
                closeFileWriter(fos);
        }
    }	
    
    private static URL getURL(String filePath) throws MalformedURLException{
    	File f = new File(filePath);
    	
    	if(f.exists())
    		return f.toURI().toURL();
    	
        return IO.class.getClassLoader().getResource(filePath);
    }

    private static void closeBufferedReader(BufferedReader br) throws EngineException {
        try {
                if (br != null)
                        br.close();
        } catch (IOException e) {
                throw new EngineException("Error closing file", e);
        }	
    }
    
    private static void closeFileWriter(FileOutputStream pw) throws EngineException {
        try {
                if (pw != null)
                        pw.close();
        } catch (Exception e) {
            throw new EngineException("Error closing file", e);
        }	
    }
}
