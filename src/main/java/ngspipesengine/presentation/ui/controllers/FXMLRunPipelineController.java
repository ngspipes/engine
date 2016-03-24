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

import components.FXMLFile;
import components.animation.magnifier.ButtonMagnifier;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import jfxutils.ComponentException;
import jfxutils.IInitializable;
import ngspipesengine.presentation.ui.utils.Uris;
import ngspipesengine.presentation.logic.engine.EngineReporter;
import ngspipesengine.presentation.logic.pipeline.Pipeline;
import ngspipesengine.presentation.ui.utils.Dialog;
import ngspipesengine.presentation.utils.WorkQueue;

import java.util.Timer;
import java.util.TimerTask;
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
		public final EngineReporter reporter;
		public final Runnable onCancel;

		public Data(Pipeline pipeline, EngineReporter reporter, Runnable onCancel){
			this.pipeline = pipeline;
			this.reporter = reporter;
			this.onCancel = onCancel;
		}
	}

	private static final int TIMEOUT = 1000;
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final int BLINK_INTERVAL = 500;
	private static final double MAGNIFY_AMP = 1.2;
	private static final String BLINK_TRACE_ON_STYLE = "-fx-background-color: #badfa3;";
	private static final String BLINK_TRACE_OFF_STYLE = "-fx-background-color: transparent;";
	private static final String BLINK_INFO_ON_STYLE = "-fx-background-color: #83b7e1;";
	private static final String BLINK_INFO_OFF_STYLE = "-fx-background-color: transparent;";
	private static final String BLINK_ERROR_ON_STYLE = "-fx-background-color: #f09292;";
	private static final String BLINK_ERROR_OFF_STYLE = "-fx-background-color: transparent;";

	
	
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
	private TabPane tabPaneReporter;
	@FXML
	private Tab tabTrace;
	@FXML
	private Tab tabInfo;
	@FXML
	private Tab tabError;
	@FXML
	private TextArea tATrace;
	@FXML
	private TextArea tAError;
	@FXML
	private TextArea tAInfo;

	private final AtomicBoolean blinkTrace = new AtomicBoolean(false);
	private final AtomicBoolean blinkInfo = new AtomicBoolean(false);
	private final AtomicBoolean blinkError = new AtomicBoolean(false);

	private Pipeline pipeline;
	private EngineReporter reporter;
	private Runnable onCancel;


	
	@Override
	public void init(Data data) throws ComponentException {
		this.pipeline = data.pipeline;
		this.reporter = data.reporter;
		this.onCancel = data.onCancel;

		loadPipelineInfo();
		loadCancelButton();
		runReporterThreads();
		loadBlinkEffect();
	}

	private void loadPipelineInfo(){
		String pipelinePath = pipeline.getPipeline().getAbsolutePath();
		String resultsPath = pipeline.getOutputDir().getAbsolutePath();
		String inputsPath = pipeline.getInputDir().getAbsolutePath();
		String engineName = pipeline.getEngineName();

		loadLabel(lPipeline, pipelinePath);
		loadLabel(lResults, resultsPath);
		loadLabel(lInputs, inputsPath);
		loadLabel(lEngineName, engineName);
		loadLabel(lFrom, Integer.toString(pipeline.getFrom()));
		loadLabel(lTo, Integer.toString(pipeline.getTo()));
	}

	private void loadCancelButton() {
		Tooltip.install(bCancel, new Tooltip("Cancel"));

		new ButtonMagnifier<>(bCancel, MAGNIFY_AMP).mount();

		bCancel.setOnMouseClicked((e)->{
			onCancel.run();
			bCancel.setDisable(true);
		});
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

		bCancel.setDisable(true);
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

	private void loadBlinkEffect() {
		registerSelectedTabObserver(tabTrace, blinkTrace);
		registerSelectedTabObserver(tabInfo, blinkInfo);
		registerSelectedTabObserver(tabError, blinkError);

		registerTextAreaObserver(tabTrace, tATrace, blinkTrace);
		registerTextAreaObserver(tabInfo, tAInfo, blinkInfo);
		registerTextAreaObserver(tabError, tAError, blinkError);

		runTimer();
	}

	private void registerSelectedTabObserver(Tab tab, AtomicBoolean blinkOrder){
		tabPaneReporter.getSelectionModel().selectedItemProperty().addListener((e, oldTab, newTab)->{
			if(newTab == tab)
				blinkOrder.set(false);
		});
	}

	private void registerTextAreaObserver(Tab tab, TextArea textArea, AtomicBoolean blinkOrder){
		textArea.textProperty().addListener((e)->{
			if(tabPaneReporter.getSelectionModel().getSelectedItem() != tab)
				blinkOrder.set(true);
		});
	}

	private void runTimer(){
		Timer timer = new Timer("Blink timer", true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				blink(tabTrace, blinkTrace, BLINK_TRACE_ON_STYLE, BLINK_TRACE_OFF_STYLE);
				blink(tabInfo, blinkInfo, BLINK_INFO_ON_STYLE, BLINK_INFO_OFF_STYLE);
				blink(tabError, blinkError, BLINK_ERROR_ON_STYLE, BLINK_ERROR_OFF_STYLE);
			}
		}, BLINK_INTERVAL, BLINK_INTERVAL);
	}

	private void blink(Tab tab, AtomicBoolean blinkPermission, String blinkOnStyle, String blinkOffStyle){
		String style;
		if(blinkPermission.get()) {
			if(tab.getStyle() == null || tab.getStyle().equals(blinkOffStyle))
				style = blinkOnStyle;
			else
				style = blinkOffStyle;
		} else {
			style = blinkOffStyle;
		}

		tab.setStyle(style);
	}

}
