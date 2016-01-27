package ngspipesengine.userInterface.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import ngspipesengine.dataAccess.Uris;
import ngspipesengine.logic.Pipeline;
import ngspipesengine.userInterface.controllers.FXMLConfigurePipelineController.Data;
import ngspipesengine.utils.Dialog;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;
import components.Window;
import components.animation.magnifier.ButtonMagnifier;


public class FXMLPipelineListViewItemController implements IInitializable<Pipeline>{
	
	
	public static Node mount(Pipeline pipeline)  throws ComponentException {
		String fXMLPath = Uris.FXML_PIPELINE_LIST_VIEW_ITEM;
		
		FXMLFile<Node, Pipeline> fxmlFile = new FXMLFile<>(fXMLPath, pipeline);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}

	
	@FXML
	private Label lName;
	@FXML
	private Button bConfigure;
	
	
	private Pipeline pipeline;
	
	@Override
	public void init(Pipeline pipeline) {
		this.pipeline = pipeline;
		load();
	}
	
	private void load(){
		lName.setText(pipeline.getPipeline().getAbsolutePath());
		Tooltip.install(lName, new Tooltip(pipeline.getPipeline().getAbsolutePath()));
		
		Tooltip.install(bConfigure, new Tooltip("Configure"));
		bConfigure.setOnMouseClicked((e)->{
			try{
				Window<Parent,?> window = new Window<>((Parent)null, "Configure");
	    		
	    		Parent root = (Parent) FXMLConfigurePipelineController.mount(new Data(pipeline, window::close));
	    		
	    		window.setRoot(root);
	    		
	    		window.open();	
			}catch (ComponentException ex) {
				Dialog.showError("Error loading configuration widow!");
			}
		});
		new ButtonMagnifier<>(bConfigure).mount();
	}
	
	

}
