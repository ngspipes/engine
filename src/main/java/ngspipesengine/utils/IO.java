package ngspipesengine.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import ngspipesengine.exceptions.EngineException;

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
