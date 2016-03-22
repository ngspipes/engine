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
package ngspipesengine.userInterface.controllers;

import components.Window;
import components.animation.magnifier.ButtonMagnifier;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import jfxutils.ComponentException;
import jfxutils.IInitializable;
import ngspipesengine.exceptions.EngineUIException;
import ngspipesengine.logic.engine.EngineManager;
import ngspipesengine.logic.engine.EngineUIReporter;
import ngspipesengine.logic.pipeline.Pipeline;
import ngspipesengine.logic.pipeline.PipelineManager;
import ngspipesengine.userInterface.utils.pallets.EngineListPallet;
import ngspipesengine.userInterface.utils.pallets.PipelineListPallet;
import ngspipesengine.utils.Dialog;
import ngspipesengine.utils.WorkQueue;

import java.util.Collection;
import java.util.LinkedList;

public class FXMLEngineController implements IInitializable<Void> {

	@FXML
	private SplitPane root;
	@FXML
	private Button bPlay;
	@FXML
	private Button bAdd;
	@FXML
	private Button bRemove;
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
		
		new ButtonMagnifier<>(bAdd).mount();
		new ButtonMagnifier<>(bRemove).mount();
		new ButtonMagnifier<>(bPlay).mount();
		
		Tooltip.install(bAdd, new Tooltip("Add"));
		Tooltip.install(bRemove, new Tooltip("Remove"));
		Tooltip.install(bPlay, new Tooltip("Play"));
		
		pipelinesPallet = new PipelineListPallet(tFPipelinesFilter, lvPipelines);
		WorkQueue.run(()->{
			try {
				PipelineManager.load();

				Platform.runLater(()->pipelinesPallet.load(PipelineManager.getAll()));
			} catch (EngineUIException ex) {
				Platform.runLater(()->Dialog.showError("Error loading previous pipelines!"));
			}
		});
		
		enginesPallet = new EngineListPallet(tFEnginesFilter, lvEngines);
		loadEnginesNames();
		
		tPPipelines.getSelectionModel().selectedIndexProperty().addListener((obs, prev, curr)->{
			if(curr.intValue() == 0)
				turnOnPipelineMode();
			else
				turnOffPipelineMode();
			
			if(curr.intValue()==1)
				loadEnginesNames();
		});
	}


	private void loadEnginesNames(){
		try {
			Collection<String> enginesNames = EngineManager.getEnginesNames();
			enginesPallet.load(enginesNames);
		} catch(EngineUIException e) {
			Dialog.showError(e.getMessage());
			enginesPallet.load(new LinkedList<>());
		}
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
			EngineUIReporter reporter = new EngineUIReporter();
			int id = EngineManager.run(pipeline, reporter);

			Tab pipelineTab = new Tab();

			pipelineTab.setClosable(true);
			pipelineTab.setOnCloseRequest((e) -> stopExecution(id));

			pipelineTab.setGraphic(new Label(pipeline.getPipeline().getName()));
			Node root = FXMLRunPipelineController.mount(new FXMLRunPipelineController.Data(pipeline, reporter, ()->stopExecution(id)));

			pipelineTab.setContent(root);
			tPPipelines.getTabs().add(pipelineTab);
			tPPipelines.getSelectionModel().select(pipelineTab);
			this.root.getScene().getWindow().sizeToScene();
		} catch (Exception ex) {
			Dialog.showError(ex.getMessage());
		}
	}

	private void stopExecution(int id){
		try{
			if(EngineManager.getAllRunningPipelines().contains(id)){
				EngineManager.stop(id);
				Dialog.showInfo("Running Engine was stopped");
			}
		}catch (Exception ex){
			Platform.runLater(()->Dialog.showError(ex.getMessage()));
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
