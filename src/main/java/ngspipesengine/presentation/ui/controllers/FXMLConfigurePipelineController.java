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
package ngspipesengine.presentation.ui.controllers;

import com.sun.management.OperatingSystemMXBean;
import components.FXMLFile;
import components.animation.magnifier.ButtonMagnifier;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;
import ngspipesengine.presentation.dataAccess.Uris;
import ngspipesengine.presentation.exceptions.EngineUIException;
import ngspipesengine.presentation.logic.engine.EngineManager;
import ngspipesengine.presentation.logic.pipeline.Pipeline;
import ngspipesengine.presentation.ui.controllers.FXMLConfigurePipelineController.Data;
import ngspipesengine.presentation.ui.utils.Dialog;
import sun.management.ManagementFactoryHelper;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;


public class FXMLConfigurePipelineController implements IInitializable<Data> {

	public static Node mount(Data data)  throws ComponentException {
		String fXMLPath = Uris.FXML_CONFIGURE_PIPELINE;

		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);

		fxmlFile.build();

		return fxmlFile.getRoot();
	}

	public static class Data{
		public final Pipeline pipeline;
		public final Runnable onConfirm;

		public Data(Pipeline pipeline, Runnable onConfirm){
			this.pipeline = pipeline;
			this.onConfirm = onConfirm;
		}
	}


	@FXML
	private TextField tFResults;
	@FXML
	private Button bResults;
	@FXML
	private TextField tFInputs;
	@FXML
	private Button bInputs;
	@FXML 
	private ComboBox<String> cBEngines;
	@FXML 
	private ComboBox<Integer> cBFrom;
	@FXML 
	private ComboBox<Integer> cBTo;
	@FXML
	private Label lMemoryQuantity;
	@FXML
	private Slider sMemoryQuantity;
	@FXML
	private Label lProcessorsQuantity;
	@FXML
	private Slider sProcessorsQuantity;
	@FXML
	private ImageView iVMemoryWarning;
	@FXML
	private Button bConfirm;

	private Pipeline pipeline;
	private Runnable onConfirm;

	@Override
	public void init(Data data) throws ComponentException {
		this.pipeline = data.pipeline;
		this.onConfirm = data.onConfirm;
		load();
	}

	private void load() throws ComponentException {
		setButtonConfirmClick();
		setButtonResultsClick();
		setButtonInputsClick();

		new ButtonMagnifier<>(bResults).mount();
		new ButtonMagnifier<>(bInputs).mount();
		new ButtonMagnifier<>(bConfirm).mount();

		Tooltip.install(bResults, new Tooltip("Search"));
		Tooltip.install(bInputs, new Tooltip("Search"));
		Tooltip.install(bConfirm, new Tooltip("Confirm"));
		Tooltip.install(iVMemoryWarning, new Tooltip("Minimize memory may not allow the entire execution of pipeline!"));

		tFResults.setText(pipeline.getResults().getAbsolutePath());
		tFInputs.setText(pipeline.getInputs().getAbsolutePath());

		try{
			loadNamesComboBox();
		} catch(EngineUIException ex) {
			throw new ComponentException(ex.getMessage(), ex);
		}

		loadFromComboBox();
		loadToComboBox();
		loadMemory();
		loadProcessors();
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

	private void loadNamesComboBox() throws EngineUIException {
		String defaultEngineName = EngineManager.getDefaultEngineName();
		Collection<String> enginesNames = EngineManager.getEnginesNames();
		enginesNames.add(defaultEngineName);

		cBEngines.setItems(FXCollections.observableArrayList(enginesNames));
		cBEngines.getSelectionModel().select(pipeline.getEngineName());
	}

	private void loadFromComboBox(){
		cBFrom.setItems(FXCollections.observableArrayList(getNumberOptions()));
		cBFrom.getSelectionModel().select(pipeline.getFrom()-1);
		cBFrom.getSelectionModel().selectedItemProperty().addListener((obs, prev, curr)->{
			int from = curr;
			int to = cBTo.getSelectionModel().getSelectedItem();
			updateMemory(from, to);
		});
	}

	private void loadToComboBox(){
		cBTo.setItems(FXCollections.observableArrayList(getNumberOptions()));
		cBTo.getSelectionModel().select(pipeline.getTo()-1);
		cBTo.getSelectionModel().selectedItemProperty().addListener((obs, prev, curr)->{
			int from  = cBFrom.getSelectionModel().getSelectedItem();
			int to = curr;
			updateMemory(from, to);
		});
	}

	private void updateMemory(int from, int to){
		int prevFrom = pipeline.getFrom();
		int prevTo = pipeline.getTo();

		if(to >= from){
			pipeline.setFrom(from);
			pipeline.setTo(to);

			sMemoryQuantity.setValue(pipeline.getMemory());

			pipeline.setFrom(prevFrom);
			pipeline.setTo(prevTo);
		}else{
			Dialog.showError("From can't be greater than To!");
		}
	}

	public void loadMemory(){
		sMemoryQuantity.setMin(1);
		sMemoryQuantity.setMax(getMaxMemory());
		sMemoryQuantity.setValue(pipeline.getMemory());
		sMemoryQuantity.valueProperty().addListener((obs, prev, curr)->{
			lMemoryQuantity.setText(curr.intValue()+"");
		});

		lMemoryQuantity.setText(pipeline.getMemory()+"");
	}

	private int getMaxMemory(){
		OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactoryHelper.getOperatingSystemMXBean();
		double max = pipeline.getMemory();

		if(max<bean.getTotalPhysicalMemorySize())
			max = bean.getTotalPhysicalMemorySize()*Math.pow(10,-6);

		return new Double(max).intValue();
	}

	public void loadProcessors(){
		sProcessorsQuantity.setMin(1);
		sProcessorsQuantity.setMax(getMaxProcessors());
		sProcessorsQuantity.setValue(pipeline.getProcessors());
		sProcessorsQuantity.valueProperty().addListener((obs, prev, curr)->{
			lProcessorsQuantity.setText(curr.intValue()+"");
		});

		lProcessorsQuantity.setText(pipeline.getProcessors()+"");
	}

	private int getMaxProcessors(){
		return Runtime.getRuntime().availableProcessors();
	}

	private void setButtonConfirmClick() {
		bConfirm.setOnMouseClicked((e)->{
			if(!validateValues())
				return;

			String results = tFResults.getText();
			String inputs = tFInputs.getText();
			String engineName = cBEngines.getSelectionModel().getSelectedItem();
			int from = cBFrom.getSelectionModel().getSelectedItem();
			int to = cBTo.getSelectionModel().getSelectedItem();
			int memory = new Double(sMemoryQuantity.getValue()).intValue();
			int processors = new Double(sProcessorsQuantity.getValue()).intValue();

			pipeline.setResults(new File(results));
			pipeline.setInputs(new File(inputs));
			pipeline.setEngineName(engineName);
			pipeline.setFrom(from);
			pipeline.setTo(to);
			pipeline.setMemory(memory);
			pipeline.setProcessors(processors);

			onConfirm.run();
		});
	}

	private boolean validateValues(){
		String results = tFResults.getText();
		String inputs = tFInputs.getText();
		int from = cBFrom.getSelectionModel().getSelectedItem();
		int to = cBTo.getSelectionModel().getSelectedItem();

		if(results != null && !results.isEmpty()){
			if(!new File(results).exists()) {
				Dialog.showError("Invalid Results folder directory!");
				return false;
			}
		}

		if(inputs != null && !inputs.isEmpty()){
			if(!new File(inputs).exists()){
				Dialog.showError("Invalid Inputs folder directory!");
				return false;
			}
		}

		if(from>to){
			Dialog.showError("From can't be greater than To!");
			return false;
		}

		return true;
	}

	private Collection<Integer> getNumberOptions(){
		Collection<Integer> options = new LinkedList<>();

		for(int i=0; i<pipeline.getTotalSteps(); ++i)
			options.add(i+1);

		return options;
	}

}
