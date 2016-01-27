package ngspipesengine.userInterface.utils.pallets;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ngspipesengine.logic.Pipeline;
import ngspipesengine.userInterface.controllers.FXMLPipelineListViewItemController;
import ngspipesengine.utils.Dialog;
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
