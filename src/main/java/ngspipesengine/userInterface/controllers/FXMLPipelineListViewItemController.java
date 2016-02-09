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
