package ngspipesengine.userInterface.utils.pallets;

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
import ngspipesengine.configurator.engines.VMEngine;
import ngspipesengine.dataAccess.Uris;
import ngspipesengine.utils.Dialog;
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
