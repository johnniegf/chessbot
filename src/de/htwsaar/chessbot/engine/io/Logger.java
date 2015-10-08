package de.htwsaar.chessbot.engine.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Schreibt die UCI-Kommunikation sowie debug- und errorausgaben
 * in eine Log-Datei.
 * 
 * @author David Holzapfel
 *
 */
public class Logger extends Thread {

	public static final byte GUI_TO_ENGINE = 0b0001;
	public static final byte ENGINE_TO_GUI = 0b0010;
	public static final byte DEBUG = 0b0100;
	public static final byte ERROR = 0b1000;

	private static Logger INSTANCE;

	private Writer logWriter;
	private String logFilePath = "chessbot.log";
	private File logFile;
	private SimpleDateFormat dateFormat;
	private boolean loggingDisabled = false;
	private boolean exit = false;
	private boolean recordInput = false;
	
	private LinkedList<String> recordedCommands;
	private volatile LinkedList<String> lineQueue;


	public static Logger getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new Logger();
			INSTANCE.setPriority(4);
			INSTANCE.setName("Logger");
		}
		return INSTANCE;
	}

	@Override
	public void run() {
		while(!exit) {
			try {

				String message = null;
				synchronized (this.lineQueue) {
					message = this.lineQueue.pollFirst();
				}

				if(message != null) {
					writeToLog(message);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeToLog(String message) throws IOException {
		byte logType = Byte.valueOf(String.valueOf(message.charAt(0)));
		message = getPrefix(logType) + message.substring(1) + System.lineSeparator();
		this.logWriter.write(message);
	}



	public void setLoggingDisabled(boolean disabled) {
		this.loggingDisabled = disabled;
	}

	private Logger() {
		logFile = new File(logFilePath);
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		lineQueue = new LinkedList<String>();
		recordedCommands = new LinkedList<String>();
		try {
			logWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(logFile), "utf-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			setLoggingDisabled(true);
			System.out.println("Could not open log file. Logging disabled");
		}
	}

	private String getPrefix(byte logType) {
		String prefix = "[" + dateFormat.format(Calendar.getInstance().getTime()) + "]";

		switch(logType) {
		case GUI_TO_ENGINE:
			prefix += "{G > E}| ";
			break;
		case ENGINE_TO_GUI:
			prefix += "{E > G}| ";
			break;
		case DEBUG:
			prefix += "{DEBUG}| ";
			break;
		case ERROR:
			prefix += "{ERROR}| ";
			break;
		default:
			prefix += "{OTHER}| ";
		}

		return prefix;
	}

	public List<String> getRecordedCommands() {
		return this.recordedCommands;
	}
	
	public void startRecording() {
		this.recordInput = true;
		this.recordedCommands.clear();
	}
	
	public void stopRecording() {
		this.recordInput = false;
	}
	
	public synchronized void log(String message, byte logType) {
		if(this.loggingDisabled) {
			return;
		}

		if(logType == GUI_TO_ENGINE) {
			if(recordInput && !message.startsWith("test record")) {
				recordedCommands.addFirst(message);
			}
		}

		synchronized (this.lineQueue) {
			this.lineQueue.addLast(new String(String.valueOf(logType) + message));
		}
	}

	public void close() {
		try {
			this.logWriter.flush();
			this.logWriter.close();
			while(!lineQueue.isEmpty()) {
				writeToLog(lineQueue.pollFirst());
			}
		} catch (IOException e) {
			return;
		}
		exit = true;
	}

}
