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

import java.io.File;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import ngspipesengine.dataAccess.Uris;
import ngspipesengine.logic.Pipeline;
import ngspipesengine.logic.PipelineManager;
import ngspipesengine.utils.Dialog;
import ngspipesengine.utils.EngineUIException;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;
import components.Window;
import components.animation.magnifier.ButtonMagnifier;




public class FXMLLoadPipelineController implements IInitializable<Consumer<Pipeline>> {

	public static Node mount(Consumer<Pipeline> onFinish)  throws ComponentException {
		String fXMLPath = Uris.FXML_LOAD_PIPELINE;
		
		FXMLFile<Node, Consumer<Pipeline>> fxmlFile = new FXMLFile<>(fXMLPath, onFinish);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	
	@FXML
	private TextField tFPipeline;
	@FXML
	private TextField tFResults;
	@FXML
	private TextField tFInputs;
	@FXML
	private Button bPipeline;
	@FXML
	private Button bResults;
	@FXML
	private Button bInputs;
	@FXML
	private Button bOk;
	@FXML 
	private ComboBox<String> cBEngines;
	
	private Consumer<Pipeline> onFinish;

	@Override
	public void init(Consumer<Pipeline> onFinish) {
		this.onFinish = onFinish;
		load();
	}
	
	private void load(){
		setButtonOkClick();
		setButtonPipelineClick();
		setButtonResultsClick();
		setButtonInputsClick();
		
		new ButtonMagnifier<Button>(bPipeline).mount();
		new ButtonMagnifier<Button>(bResults).mount();
		new ButtonMagnifier<Button>(bInputs).mount();
		new ButtonMagnifier<Button>(bOk).mount();
		
		Tooltip.install(bPipeline, new Tooltip("Search"));
		Tooltip.install(bResults, new Tooltip("Search"));
		Tooltip.install(bInputs, new Tooltip("Search"));
		Tooltip.install(bOk, new Tooltip("Ok"));
		
		loadComboBox();
	}

	private void setButtonPipelineClick() {
		bPipeline.setOnMouseClicked((e)->{
			File pipeline = Dialog.getFile("Pipeline");
			
			if(pipeline != null)
				tFPipeline.setText(pipeline.getAbsolutePath());
		});
	}
	
	private void setButtonResultsClick() {
		bResults.setOnMouseClicked((e)->{
			File results = Dialog.getDirectory("Results folder");
			
			if(results != null)
				tFResults.setText(results.getAbsolutePath());
		});
	}

	private void setButtonInputsClick() {
		bInputs.setOnMouseClicked((e)->{
			File inputs = Dialog.getDirectory("Inputs folder");
			
			if(inputs != null)
				tFInputs.setText(inputs.getAbsolutePath());	
		});
	}

	private void setButtonOkClick() {
		bOk.setOnMouseClicked((e)->{
			String pipeline = tFPipeline.getText();
			String results = tFResults.getText();
			String inputs = tFInputs.getText();
			String engineName = cBEngines.getSelectionModel().getSelectedItem();
			
			if(pipeline.isEmpty() || !new File(pipeline).exists()){
				ngspipesengine.utils.Dialog.showError("Invalid Pipeline directory!");
				return;
			}
			if(results.isEmpty() || !new File(results).exists()){
				ngspipesengine.utils.Dialog.showError("Invalid Results folder directory!");
				return;
			}
			if(inputs.isEmpty() || !new File(inputs).exists()){
				ngspipesengine.utils.Dialog.showError("Invalid Inputs folder directory!");
				return;
			}
			
			try {
				Window<?, ?> loadingWindow = Dialog.getLoadingWindow("Loading pipeline");
				loadingWindow.open();
				
				new Thread(()->{
					try{
						Pipeline p = new Pipeline(new File(pipeline), new File(results), new File(inputs), engineName);
						Platform.runLater(()->{
							loadingWindow.close();
							onFinish.accept(p);
						});
					}catch(EngineUIException ex){
						Platform.runLater(()->Dialog.showError(ex.getMessage()));
					}
				}).start();
			} catch (ComponentException ex) {
				Dialog.showError("Error loading load window!");
			}
		});
	}

	private void loadComboBox(){
		ObservableList<String> names = FXCollections.observableArrayList(PipelineManager.getEnginesNames());
		cBEngines.setItems(names);
		cBEngines.getSelectionModel().select(0);
	}
	
}
