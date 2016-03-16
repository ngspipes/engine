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
package ngspipesengine;

import components.FXMLFile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxutils.ComponentException;
import ngspipesengine.configurator.engines.VMEngine;
import ngspipesengine.dataAccess.Uris;
import ngspipesengine.logic.pipeline.PipelineManager;
import ngspipesengine.utils.Dialog;
import ngspipesengine.utils.EngineUIException;


public class NGSPipesEngineApplication extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setOnCloseRequest((e)->{
			try {
				PipelineManager.save();
				Platform.exit();
			} catch (EngineUIException ex) {
				Dialog.showError(ex.getMessage());
			}
		});
		
		if(!VMEngine.acceptedVBoxVersion())
			Dialog.showWarning("Your current version of Virtual Box is to old.\nYou should have at least 4.3.20 version");
		
		VMEngine.register();
		
		loadWindow(stage);
	}

	private void loadWindow(Stage stage) throws EngineUIException	{
		try{
			
			String path = Uris.FXML_ENGINE;
			FXMLFile<Node, ?> fxmlFile = new FXMLFile<>(path);
			fxmlFile.mount();
			Parent root = (Parent) fxmlFile.getRoot();
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
			
		}catch(ComponentException ex){
			Dialog.showError("Error loading Engine");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
