package ngspipesengine.userInterface.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import ngspipesengine.logic.Pipeline;
import ngspipesengine.logic.PipelineManager;
import ngspipesengine.userInterface.utils.pallets.EngineListPallet;
import ngspipesengine.userInterface.utils.pallets.PipelineListPallet;
import ngspipesengine.utils.Dialog;
import ngspipesengine.utils.EngineUIException;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.Window;
import components.animation.magnifier.ButtonMagnifier;

public class FXMLEngineController implements IInitializable<Void> {

	@FXML
	private Button bPlay;
	@FXML
	private Button bAdd;
	@FXML
	private Button bRemove;
	@FXML
	private Button bInfo;
	@FXML
	private Button bHelp;
	@FXML
	private TextField tFPipelinesFilter;
	@FXML
	private ListView<Pipeline> lvPipelines;
	@FXML
	private TextField tFEnginesFilter;
	@FXML
	private ListView<String> lvEngines;
	@FXML
	private TabPane tPPipelines;
	
	
	private PipelineListPallet pipelinesPallet;
	private EngineListPallet enginesPallet;
	
	@Override
	public void init(Void arg) throws ComponentException {
		bAdd.setOnMouseClicked((e)->onAdd());
		bRemove.setOnMouseClicked((e)->onRemove());
		bPlay.setOnMouseClicked((e)->onPlay());
		bInfo.setOnMouseClicked((e)->onInfo());
		bHelp.setOnMouseClicked((e)->onHelp());
		
		new ButtonMagnifier<>(bAdd).mount();
		new ButtonMagnifier<>(bRemove).mount();
		new ButtonMagnifier<>(bPlay).mount();
		
		Tooltip.install(bAdd, new Tooltip("Add"));
		Tooltip.install(bRemove, new Tooltip("Remove"));
		Tooltip.install(bPlay, new Tooltip("Play"));
		Tooltip.install(bInfo, new Tooltip("Info"));
		Tooltip.install(bHelp, new Tooltip("Help"));
		
		pipelinesPallet = new PipelineListPallet(tFPipelinesFilter, lvPipelines);
		new Thread(()->{
			try {
				for(Pipeline p : PipelineManager.load())
					PipelineManager.add(p);
				
				Platform.runLater(()->pipelinesPallet.load(PipelineManager.getAll()));
			} catch (EngineUIException ex) {
				Platform.runLater(()->Dialog.showError("Error loading previous pipelines!"));
			}
		}).start();
		
		enginesPallet = new EngineListPallet(tFEnginesFilter, lvEngines);
		enginesPallet.load(PipelineManager.getEnginesNames());
		
		tPPipelines.getSelectionModel().selectedIndexProperty().addListener((obs, prev, curr)->{
			if(curr.intValue() == 0)
				turnOnPipelineMode();
			else
				turnOffPipelineMode();
			
			if(curr.intValue()==1)
				enginesPallet.load(PipelineManager.getEnginesNames());
		});
	}
	
	
	private void onAdd(){
		try{
			Window<Parent,?> window = new Window<>((Parent)null, "Load Pipeline");
    		
    		Parent root = (Parent) FXMLLoadPipelineController.mount((pipeline)->{
    			PipelineManager.add(pipeline);
    			try {
    				PipelineManager.save();	
				} catch (Exception e) {
					Dialog.showError("Error saving Pipeline!");
				}
    			window.close();	
    			pipelinesPallet.load(PipelineManager.getAll());
    		});
    		
    		window.setRoot(root);
    		
    		window.open();	
		}catch(ComponentException ex){
			Dialog.showError("Error loading window!");
		}
	}
	
	private void onRemove(){
		Pipeline pipeline = lvPipelines.getSelectionModel().getSelectedItem();
		
		if(pipeline!=null){
			PipelineManager.remove(pipeline);
			pipelinesPallet.load(PipelineManager.getAll());	
		}
	}
	
	private void onPlay(){
		Pipeline pipeline = lvPipelines.getSelectionModel().getSelectedItem();
		
		if(pipeline == null)
			return;
		
		try {
			Tab pipelineTab = new Tab();
			pipelineTab.setClosable(true);
			pipelineTab.setGraphic(new Label(pipeline.getPipeline().getName()));
			Node root = FXMLRunPipelineController.mount(pipeline);
			pipelineTab.setContent(root);
			tPPipelines.getTabs().add(pipelineTab);
			tPPipelines.getSelectionModel().select(pipelineTab);
		} catch (Exception ex) {
			Dialog.showError(ex.getMessage());
		}
	}
	
	private void onInfo(){
		System.out.println("Info");
	}
	
	private void onHelp(){
		System.out.println("Help");
	}
	
	private void turnOnPipelineMode(){
		bPlay.setDisable(false);
		bAdd.setDisable(false);
		bRemove.setDisable(false);
	}
	
	private void turnOffPipelineMode(){
		bPlay.setDisable(true);
		bAdd.setDisable(true);
		bRemove.setDisable(true);
	}
	
}
