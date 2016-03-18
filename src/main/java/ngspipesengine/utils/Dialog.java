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
package ngspipesengine.utils;

import components.Window;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;



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

    public static ButtonType getPermissionToStopEngine() {
        String title = "Stop Engine";
        String header = "Permission to stop Engine";
        String text = "This execution has not finished. Do you want to stop execution?";

        return getPermission(title, header, text);
    }

    public static ButtonType getPermissionToStopEngines() {
        String title = "Stop Engines";
        String header = "Permission to stop Engine";
        String text = "There are pipelines executing. Do you want to stop all Engines?";

        return getPermission(title, header, text);
    }

    public static ButtonType getPermission(String title, String header, String text) {
        Alert alert = new Alert(AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get();
    }

}
