package ngspipesengine.userInterface.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import ngspipesengine.configurator.engines.VMEngine;
import ngspipesengine.dataAccess.Uris;
import ngspipesengine.logic.Pipeline;
import ngspipesengine.utils.Dialog;
import progressReporter.SocketReporter;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;
import components.animation.magnifier.ButtonMagnifier;


public class FXMLRunPipelineController implements IInitializable<Pipeline>{
		
	public static Node mount(Pipeline pipeline)  throws ComponentException {
		String fXMLPath = Uris.FXML_RUN_PIPELINE;
		
		FXMLFile<Node, Pipeline> fxmlFile = new FXMLFile<>(fXMLPath, pipeline);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	private static final String TRACE_TAG = SocketReporter.TRACE_TAG;
	private static final String ERROR_TAG = SocketReporter.ERROR_TAG;
	private static final String INFO_TAG = SocketReporter.INFO_TAG;
	
	
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
	private VMEngine vm;
	private ServerSocket socket;
	private AtomicBoolean finish = new AtomicBoolean();
	
	
	@Override
	public void init(Pipeline pipeline) throws ComponentException {
		this.pipeline = pipeline;
		load();
		run();
	}
	
	private void load(){
		lPipeline.setText(pipeline.getPipeline().getAbsolutePath());
		lResults.setText(pipeline.getResults().getAbsolutePath());
		lInputs.setText(pipeline.getInputs().getAbsolutePath());
		lEngineName.setText(pipeline.getEngineName());
		lFrom.setText(Integer.toString(pipeline.getFrom()));
		lTo.setText(Integer.toString(pipeline.getTo()));
		
		Tooltip.install(lPipeline, new Tooltip(pipeline.getPipeline().getAbsolutePath()));
		Tooltip.install(lResults, new Tooltip(pipeline.getResults().getAbsolutePath()));
		Tooltip.install(lInputs, new Tooltip(pipeline.getInputs().getAbsolutePath()));
		Tooltip.install(lEngineName, new Tooltip(pipeline.getEngineName()));
		Tooltip.install(bCancel, new Tooltip("Cancel"));
		
		new ButtonMagnifier<Button>(bCancel, 1.2).mount();
		
		bCancel.setOnMouseClicked((e)->{
			try {
				finish.set(true);
				vm.stop();
				bCancel.setDisable(true);
			} catch (Exception ex) {
				Dialog.showError("Error stopping Engine!");
			}
		});
	}
	
	private void run() throws ComponentException{
		try{
			socket = new ServerSocket(0);
			initServer();
		}catch(IOException ex){
			throw new ComponentException("Error creating socket!", ex);
		}
		
		new Thread(()->{
			try { 
				pipeline.properties.setPort(socket.getLocalPort());
				vm = new VMEngine(pipeline.properties);
				vm.start();
			} catch(Exception ex) {
				closeSocket();
				Platform.runLater(()-> Dialog.showError(ex.getMessage()));
			}
		}).start();
	}

	private void initServer() {
		new Thread(()->{
			Socket clientSocket = null;
			
			try {
				clientSocket = acceptClient();
				clearText();
				if(clientSocket!=null)
					answerClient(clientSocket);
			} catch (IOException e) {
				Platform.runLater(()->Dialog.showError("Error on comunication channel!"));
			} finally{
				finish.set(true);
				closeSocket();
				closeClient(clientSocket);
				closeVM();
				Platform.runLater(()->bCancel.setDisable(true));
			}
			
		}).start();
	}

	private void setText(TextArea textArea, String text){
		Platform.runLater(()->textArea.setText(text));
	}
	
	private void appendText(TextArea textArea, String text){
		Platform.runLater(()->textArea.appendText(text));
	}
	
	private void clearText(){
		Platform.runLater(()->tATrace.clear());
	}
	
	private Socket acceptClient() throws IOException {
		socket.setSoTimeout(1000);

		setText(tATrace, "Loading (can take a few minutes)");
		
		while(!finish.get()){
			try{
				return socket.accept();
			}catch(SocketTimeoutException ex){
				Platform.runLater(()->{
					String text = tATrace.getText();
					
					text = text.endsWith("......") ? text.replace("......", "") : text+"."; 

					tATrace.setText(text);
				});		
			}
		}
		
		return null;
	}
	
	private void answerClient(Socket client) throws IOException {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));){
			String line;
			while ((line = in.readLine()) != null){
				line+="\n";
				if(line.startsWith(TRACE_TAG))
					appendText(tATrace, line.substring(TRACE_TAG.length()));
				else if(line.startsWith(ERROR_TAG))
					appendText(tAError, line.substring(ERROR_TAG.length()));
				else
					appendText(tAInfo, line.substring(INFO_TAG.length()));
			}
		}
	}
	
	private void closeClient(Socket client){
		if(client == null)
			return;
		
		try {
			client.close();	
		} catch (Exception e) {
			Platform.runLater(()->Dialog.showError("Error closing Socket!"));
		}
	}
	
	private void closeSocket(){
		if(socket == null)
			return;
		
		try {
			socket.close();	
		} catch (Exception e) {
			Platform.runLater(()->Dialog.showError("Error closing Socket!"));
		}
	}

	private void closeVM() {
		if(vm == null)
			return;
		
		try {
			vm.finish();	
		} catch (Exception e) {
			Platform.runLater(()->Dialog.showError("Error finishing VMEngine!"));
		}
	}
	
}
