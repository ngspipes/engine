package ngspipesengine.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Log {

	private static final int JSON_IDENTATION = 5;
	private static final String JSON_EXTENSION = ".json";

	public static enum MessageType{
		Fatal(),
		Error(),
		Warning(),
		Info(),
		Debug();

		MessageType(){}
	}

	private static class Message{
		public final MessageType type;
		public final String tag;
		public final String msg;
		public final Date time;
		public final long threadID;

		public Message(MessageType type, String tag, String msg, Date time, long threadID) {
			this.type = type;
			this.tag = tag;
			this.msg = msg;
			this.time = time;
			this.threadID = threadID;
		}
	}

	private static final long POLL_TIMEOUT = 1000;
	private final AtomicBoolean STOP = new AtomicBoolean(false);

	private final JSONObject DATA = new JSONObject();
	
	private static final String START_JSON_KEY = "start";
	private static final String END_JSON_KEY = "end";
	private static final String MESSAGES_JSON_KEY = "messages";
	private static final String TYPE_JSON_KEY = "type";
	private static final String TAG_JSON_KEY = "tag";
	private static final String MESSAGE_JSON_KEY = "message";
	private static final String TIME_JSON_KEY = "time";
	private static final String THREAD_ID_JSON_KEY = "threadID";

	
	private final BlockingQueue<Message> QUEUE = new LinkedBlockingQueue<Log.Message>();
	String fileName; 
	public void setFileName(String fileName) { this.fileName = fileName; }
	
	public Log(){
		this("");
	}
	
	public Log(String fileName){
		this.fileName = fileName;
		initLogThread();
	}

	public void log(MessageType type, String tag, String msg){
		QUEUE.add(new Message(type, tag, msg, new Date(), Thread.currentThread().getId()));
	}

	public void error(String tag, String msg){
		log(MessageType.Error, tag, msg);
	}

	public void info(String tag, String msg){
		log(MessageType.Info, tag, msg);
	}

	public void warning(String tag, String msg){
		log(MessageType.Warning, tag, msg);
	}

	public void debug(String tag, String msg){
		log(MessageType.Debug, tag, msg);
	}
	
	public void fatal(String tag, String msg){
		log(MessageType.Fatal, tag, msg);
	}

	public void stop(){
		STOP.set(true);
	}
	
	private void initLogThread() {
		Thread thread = new Thread(()->{
			try{
				init();

				run();

				finish();	
			} catch(Exception e) {
				e.printStackTrace();
			}
		});

		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(false);
		thread.start();
	}
	
	private void finish() throws Exception {
		DATA.put(END_JSON_KEY, new Date());
		printData();
	}

	private void run() throws Exception {
		Message message;
		while(!STOP.get()){
			message = QUEUE.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);

			if(message!= null){
				DATA.getJSONArray(MESSAGES_JSON_KEY).put(getMessageData(message));
				printData();
			}	
		}
	}

	private static JSONObject getMessageData(Message message) throws JSONException {
		JSONObject data = new JSONObject();

		data.put(TAG_JSON_KEY, message.tag);
		data.put(TYPE_JSON_KEY, message.type);
		data.put(TIME_JSON_KEY, message.time);
		data.put(MESSAGE_JSON_KEY, message.msg);
		data.put(THREAD_ID_JSON_KEY, message.threadID);

		return data;
	}

	private void printData() throws Exception {
		PrintWriter writer = null;
		createLogFile();
		
		try{
			writer = new PrintWriter(new BufferedWriter(new FileWriter(getLogDir())));
			writer.println(DATA.toString(JSON_IDENTATION));
		} finally{
			if(writer!= null)
				writer.close();
		}
	}

	private void createLogFile() throws IOException {
		File logFile = new File(getLogDir());
		if(!logFile.exists()) {
			logFile.getParentFile().mkdirs(); 
			logFile.createNewFile();
		}
	}

	private String getLogDir() {
		return Uris.LOG_FOLDER_PATH + fileName + JSON_EXTENSION;
	}

	private void init() throws Exception {
		DATA.put(START_JSON_KEY, new Date());
		DATA.put(MESSAGES_JSON_KEY, new JSONArray());
	}
	
}
