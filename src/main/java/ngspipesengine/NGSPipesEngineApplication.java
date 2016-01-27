package ngspipesengine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ngspipesengine.configurator.engines.VMEngine;
import ngspipesengine.dataAccess.Uris;
import ngspipesengine.logic.PipelineManager;
import ngspipesengine.utils.Dialog;
import ngspipesengine.utils.EngineUIException;
import jfxutils.ComponentException;

import components.FXMLFile;


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
//			stage.setResizable(false);
			
		}catch(ComponentException ex){
			Dialog.showError("Error loading Engine");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
