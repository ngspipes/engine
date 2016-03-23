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
package ngspipesengine.presentation.ui.pallets;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ngspipesengine.presentation.logic.pipeline.Pipeline;
import ngspipesengine.presentation.ui.controllers.FXMLPipelineListViewItemController;
import ngspipesengine.presentation.ui.utils.Dialog;
import jfxutils.ComponentException;

public class PipelineListPallet extends Pallet<Pipeline> {

	public PipelineListPallet(TextField textfield, ListView<Pipeline> listView) {
		super(textfield, listView);
	}

	@Override
	protected boolean filter(Pipeline pipeline, String pattern) {
		return pipeline.getPipeline().getAbsolutePath().toLowerCase().contains(pattern.toLowerCase());
	}

	@Override
	protected Node getCellRoot(Pipeline pipeline) {
		try {			
			return FXMLPipelineListViewItemController.mount(pipeline);
		} catch (ComponentException e) {
			Dialog.showError("Error loading pipeline list view item!");
		}
		
		return null;
	}

}
