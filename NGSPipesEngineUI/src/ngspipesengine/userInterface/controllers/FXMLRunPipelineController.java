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
import utils.ComponentException;
import utils.IInitializable;

import components.FXMLFile;
import components.animation.magnifier.ButtonMagnifier;


public class FXMLRunPipelineController implements IInitializable<Pipeline>{
		
	public static Node mount(Pipeline pipeline)  throws ComponentException {
		String fXMLPath = Uris.FXML_RUN_PIPELINE;
		
		FXMLFile<Node, Pipeline> fxmlFile = new FXMLFile<>(fXMLPath, pipeline);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
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
	private TextArea tAComunicationChannel;
	
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

	private void setText(String text){
		Platform.runLater(()->tAComunicationChannel.setText(text));
	}
	
	private void appendText(String text){
		Platform.runLater(()->tAComunicationChannel.appendText(text));
	}
	
	private void clearText(){
		Platform.runLater(()->tAComunicationChannel.clear());
	}
	
	private Socket acceptClient() throws IOException {
		socket.setSoTimeout(1000);

		setText("Loading (can take a few minutes)");
		
		while(!finish.get()){
			try{
				return socket.accept();
			}catch(SocketTimeoutException ex){
				Platform.runLater(()->{
					String text = tAComunicationChannel.getText();
					
					text = text.endsWith("......") ? text.replace("......", "") : text+"."; 

					tAComunicationChannel.setText(text);
				});		
			}
		}
		
		return null;
	}
	
	private void answerClient(Socket client) throws IOException {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));){
			String line;
			while ((line = in.readLine()) != null)
				appendText(line + "\n");
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
