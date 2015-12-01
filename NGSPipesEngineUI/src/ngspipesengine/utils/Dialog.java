package ngspipesengine.utils;

import java.io.File;
import java.util.Optional;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import components.Window;



public class Dialog {

    public static void showError(String msg) {
        show(AlertType.ERROR, "Error", msg);
    }

    public static void showWarning(String msg) {
        show(AlertType.WARNING, "Warning", msg);
    }

    public static void showConfirmation(String msg) {
        show(AlertType.CONFIRMATION, "Confirmation", msg);
    }

    public static void showInfo(String msg) {
        show(AlertType.INFORMATION, "Information", msg);
    }

    private static void show(AlertType alertType, String title, String msg) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    public static String getValue(String title, String headerText, String contextText) {
        TextInputDialog dialog = new TextInputDialog("");

        if (title != null && title.length() != 0) 
            dialog.setTitle(title);
        
        if (headerText != null && headerText.length() != 0) 
            dialog.setHeaderText(headerText);
        
        if (contextText != null && contextText.length() != 0) 
            dialog.setContentText(contextText);
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) 
            return result.get();
        else 
        	return null;
    }

    public static File getDirectory(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        return chooser.showDialog(null);
    }

    public static File getFile(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        return chooser.showOpenDialog(null);
    }
    
    public static Window<Parent, Void> getLoadingWindow(String title) {
    	ProgressIndicator progressIndicator = new ProgressIndicator();
    	progressIndicator.setProgress(-1.0);	
    	return new Window<Parent, Void>(progressIndicator, title);
    }
    
}
