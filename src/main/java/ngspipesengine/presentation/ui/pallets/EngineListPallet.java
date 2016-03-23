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

import java.util.Collection;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ngspipesengine.core.configurator.engines.VMEngine;
import ngspipesengine.presentation.dataAccess.Uris;
import ngspipesengine.presentation.ui.utils.Dialog;
import components.animation.magnifier.ImageMagnifier;



public class EngineListPallet extends Pallet<String>{

	public EngineListPallet(TextField textfield, ListView<String> listView) {
		super(textfield, listView);
	}

	@Override
	protected boolean filter(String item, String pattern) {
		return item.contains(pattern);
	}

	@Override
	protected Node getCellRoot(String item) {
		HBox root = new HBox();
		ImageView deleteImage = new ImageView(new Image(Uris.DELETE_IMAGE));
		root.getChildren().add(deleteImage);
		root.getChildren().add(new Label(item));
		root.setAlignment(Pos.CENTER_LEFT);
		
		new ImageMagnifier<>(deleteImage).mount();
		Tooltip.install(deleteImage, new Tooltip("Delete"));
		
		deleteImage.setOnMouseClicked((e)->{
			try {
				VMEngine.clean(item);	
			} catch (Exception ex) {
				Dialog.showError("Error deleting Engine " + item);
			}
			
			Collection<String> items = super.getCurrentItems();
			items.remove(item);
			super.load(items);
		});
		
		return root;
	}

}
