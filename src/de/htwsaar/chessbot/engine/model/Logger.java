package de.htwsaar.chessbot.engine.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import com.sun.jmx.snmp.Timestamp;

public class Logger {

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
	

	public static Logger getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new Logger();
		}
		return INSTANCE;
	}

	public void setLoggingDisabled(boolean disabled) {
		this.loggingDisabled = disabled;
	}
	
	private Logger() {
		logFile = new File(logFilePath);
		dateFormat = new SimpleDateFormat("HH:mm:ss");
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

	public void log(String message, byte logType) {
		if(this.loggingDisabled) {
			return;
		}

		try {
			String logMessage = getPrefix(logType);
			logMessage += message;
			logMessage += "\n";
			this.logWriter.write(logMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			this.logWriter.close();
		} catch (IOException e) {
			return;
		}
	}
	
}
