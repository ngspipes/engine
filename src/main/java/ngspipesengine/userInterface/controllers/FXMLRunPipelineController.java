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

import components.FXMLFile;
import components.animation.magnifier.ButtonMagnifier;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import jfxutils.ComponentException;
import jfxutils.IInitializable;
import ngspipesengine.dataAccess.Uris;
import ngspipesengine.logic.engine.EngineUIReporter;
import ngspipesengine.logic.pipeline.Pipeline;
import ngspipesengine.utils.Dialog;
import ngspipesengine.utils.WorkQueue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public class FXMLRunPipelineController implements IInitializable<FXMLRunPipelineController.Data>{

	@FunctionalInterface
	private interface ExceptionSupplier<T>{
		T get() throws InterruptedException;
	}


	public static Node mount(Data data)  throws ComponentException {
		String fXMLPath = Uris.FXML_RUN_PIPELINE;
		
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}

	public static class Data{
		public final Pipeline pipeline;
		public final EngineUIReporter reporter;
		public final Runnable onCancel;

		public Data(Pipeline pipeline, EngineUIReporter reporter, Runnable onCancel){
			this.pipeline = pipeline;
			this.reporter = reporter;
			this.onCancel = onCancel;
		}
	}

	private static final int TIMEOUT = 1000;
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final double MAGNIFY_AMP = 1.2;
	
	
	@FXML
	private Button bCancel;
	@FXML
	private Label lPipeline;
	@FXML
	private Label lResults;
	@FXML
	private Label lInputs;
	@FXML
	private Label lEngineName;
	@FXML
	private Label lFrom;
	@FXML
	private Label lTo;
	@FXML
	private TextArea tATrace;
	@FXML
	private TextArea tAError;
	@FXML
	private TextArea tAInfo;

	private Pipeline pipeline;
	private EngineUIReporter reporter;
	private Runnable onCancel;


	
	@Override
	public void init(Data data) throws ComponentException {
		this.pipeline = data.pipeline;
		this.reporter = data.reporter;
		this.onCancel = data.onCancel;

		load();
		runReporterThreads();
	}

	private void load(){
		loadLabels();
		loadCancelButton();
	}

	private void loadCancelButton() {
		Tooltip.install(bCancel, new Tooltip("Cancel"));

		new ButtonMagnifier<>(bCancel, MAGNIFY_AMP).mount();

		bCancel.setOnMouseClicked((e)->{
			onCancel.run();
			bCancel.setDisable(true);
		});
	}

	private void loadLabels() {
		String pipelinePath = pipeline.getPipeline().getAbsolutePath();
		String resultsPath = pipeline.getResults().getAbsolutePath();
		String inputsPath = pipeline.getInputs().getAbsolutePath();
		String engineName = pipeline.getEngineName();

		loadLabel(lPipeline, pipelinePath);
		loadLabel(lResults, resultsPath);
		loadLabel(lInputs, inputsPath);
		loadLabel(lEngineName, engineName);
		loadLabel(lFrom, Integer.toString(pipeline.getFrom()));
		loadLabel(lTo, Integer.toString(pipeline.getTo()));
	}

	private void loadLabel(Label label, String text){
		label.setText(text);
		Tooltip.install(label, new Tooltip(text));
	}

	private void runReporterThreads() {
		WorkQueue.run(this::readTrace);
		WorkQueue.run(this::readError);
		WorkQueue.run(this::readInfo);
	}

	private void readTrace() {
		AtomicBoolean loaded = new AtomicBoolean(false);

		ExceptionSupplier<String> src = () -> {
			String s = reporter.getTrace(TIMEOUT, TIME_UNIT);

			if(s==null) {
				if(!loaded.get())
					updateLoadMessage();
			} else {
				if(!loaded.get()){
					Platform.runLater(()->tATrace.clear());
					loaded.set(true);
				}
			}

			return s;
		};

		readReporter(src,
                    this::trace,
                    "Error reading Trace!");
	}

	private void readError(){
		readReporter(() -> reporter.getError(TIMEOUT, TIME_UNIT),
					this::error,
					"Error reading Error!");
	}

	private void readInfo() {
		readReporter(() -> reporter.getInfo(TIMEOUT, TIME_UNIT),
					this::info,
					"Error reading Info!");
	}

	private void updateLoadMessage(){
		String currText = tATrace.getText();

		if(currText == null || currText.isEmpty())
			currText = "Loading (can take a few minutes)";
		else
			currText = currText.endsWith("...") ? currText.replace("...", "") : currText+".";

		String textUpdate = currText;
		Platform.runLater(() -> tATrace.setText(textUpdate));
	}

	private void readReporter(ExceptionSupplier<String> src, Consumer<String> dest, String errorMsg){
		String msg;

		try {
			while(!reporter.isClosed())
				if((msg = src.get()) != null)
					dest.accept(msg);

			drain(src, dest, errorMsg);
		} catch (InterruptedException e) {
			Platform.runLater(() -> Dialog.showError(errorMsg));
		} finally {
			bCancel.setDisable(true);
		}
	}

	private void drain(ExceptionSupplier<String> src, Consumer<String> dest, String errorMsg){
		String msg;

		try {
			while((msg = src.get()) != null)
				dest.accept(msg);
		}catch (InterruptedException e){
			Platform.runLater(()-> Dialog.showError(errorMsg));
		}
	}

	private void trace(String msg){
		Platform.runLater(()->{
			tATrace.appendText("\n");
			tATrace.appendText(msg);
		});
	}

	private void error(String msg){
		Platform.runLater(()->{
			tAError.appendText("\n");
			tAError.appendText(msg);
		});
	}

	private void info(String msg){
		Platform.runLater(()->{
			tAInfo.appendText("\n");
			tAInfo.appendText(msg);
		});
	}

}
