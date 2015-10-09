package de.htwsaar.chessbot.engine.util;

import java.util.List;

import de.htwsaar.chessbot.engine.Engine;
import de.htwsaar.chessbot.engine.config.Config;
import de.htwsaar.chessbot.engine.io.Logger;
import de.htwsaar.chessbot.engine.io.Parser;
import de.htwsaar.chessbot.engine.io.UCISender;

public class TestCommands {

	private static final String TEST_COMMANDS = 
			"<< TestCommands >>" + System.lineSeparator()
			+ "test help CMD" + System.lineSeparator()
			+ "test queuemsg TYPE COUNT [MSG]" + System.lineSeparator()
			+ "test set OPTION_NAME VALUE" + System.lineSeparator()
			+ "test get OPTION_NAME" + System.lineSeparator()
			+ "test record start" + System.lineSeparator()
			+ "test record stop" + System.lineSeparator()
			+ "test record exec" + System.lineSeparator()
			+ "<<--------------->>";
	
	private static final String HELP_QUEUEMSG =
			"#Usage: test queuemsg TYPE COUNT [MSG]" + System.lineSeparator()
			+ "#\tTYPE: togui error debug" + System.lineSeparator()
			+ "#\tCOUNT: an integer > 0" + System.lineSeparator()
			+ "#\tMSG: the message to queue" + System.lineSeparator()
			+ "#" + System.lineSeparator()
			+ "#  Enters a message COUNT times into the" + System.lineSeparator()
			+ "#  UCISenders message queue.";
	private static final String HELP_SET =
			"#Usage: test set OPTION_NAME VALUE" + System.lineSeparator()
			+ "#\tOPTION_NAME: name of the config option" + System.lineSeparator()
			+ "#\tVALUE: the new value of the option" + System.lineSeparator()
			+ "#" + System.lineSeparator()
			+ "#  Sets an option of the Config to the" + System.lineSeparator()
			+ "#  given value.";
	private static final String HELP_GET =
			"#Usage: test get OPTION_NAME" + System.lineSeparator()
			+ "#\tOPTION_NAME: the name of the config option" + System.lineSeparator()
			+ "#" + System.lineSeparator()
			+ "#  Returns the value of the config option.";
	private static final String HELP_RECORD =
			"#Usage: test record MODE" + System.lineSeparator()
			+ "#\tMODE: start stop exec" + System.lineSeparator()
			+ "#" + System.lineSeparator()
			+ "#  Provides the ability to record commands" + System.lineSeparator()
			+ "#  for convenient replay." + System.lineSeparator()
			+ "#  Modes:" + System.lineSeparator()
			+ "#   - start" + System.lineSeparator()
			+ "#    Begins to record all commands that do" + System.lineSeparator()
			+ "#    not start with \"test record\"." + System.lineSeparator()
			+ "#   - stop" + System.lineSeparator()
			+ "#    Stops the recording." + System.lineSeparator()
			+ "#   - exec" + System.lineSeparator()
			+ "#    Executes all previously recorded commands." + System.lineSeparator();
	private static final String HELP_HELP =
			"#Usage: test help CMD" + System.lineSeparator()
			+ "#\tCMD: the command you want to know more about" + System.lineSeparator()
			+ "#" + System.lineSeparator()
			+ "#  Displays this help for the specified command." + System.lineSeparator();
	
	public static void test(String cmd, Engine engine) {
		String[] params = cmd.split(" ");
		
		if(params.length == 1) {
			System.out.println(TEST_COMMANDS);
			return;
		}

		switch(params[1].toLowerCase()) {
		case "help":
			testHelp(params);
			break;
		case "queuemsg":
			testQueueMsg(params);
			break;
		case "set":
			testSet(params);
			break;
		case "get":
			testGet(params);
			break;
		case "record":
			testRecord(params, engine);
			break;
		}
	}
	
	private static void testHelp(String[] params) {
		switch(params[2]) {
		case "help":
			System.out.println(HELP_HELP);
			break;
		case "queuemsg":
			System.out.println(HELP_QUEUEMSG);
			break;
		case "set":
			System.out.println(HELP_SET);
			break;
		case "get":
			System.out.println(HELP_GET);
			break;
		case "record":
			System.out.println(HELP_RECORD);
			break;
		}
	}

	private static void testRecord(String[] params, Engine engine) {
		switch(params[2]) {
		case "start":
			Logger.getInstance().startRecording();
			break;
		case "stop":
			Logger.getInstance().stopRecording();
			break;
		case "exec":
			List<String> lastCommands = Logger.getInstance().getRecordedCommands();
			for(int i = lastCommands.size() - 1; i >= 0; i--) {
				String cmd = lastCommands.get(i);
				System.out.println(">> " + cmd);
				engine.getUCI().parseCommand(cmd);
			}
			break;
		}
		
	}

	private static void testGet(String[] params) {
		System.out.println(Config.getInstance().getOption(params[2]).getValue());
	}

	private static void testSet(String[] params) {
		String setOption = String.format("setoption name %s value %s", params[2], params[3]);
		Parser.setoption(setOption);
	}

	private static void testQueueMsg(String[] params) {
		String msg = "TEST_MSG";
		if(params.length > 4) {
			msg = "";
			for(int i = 4; i < params.length; i++) {
				msg += params[i] + " ";
			}
		}

		int count = Integer.parseInt(params[3]);
		switch(params[2].toLowerCase()) {
		case "togui":
			for(int i = 0; i < count; i++) {
				UCISender.getInstance().sendToGUI(msg);
			}
			UCISender.getInstance().sendDebug("[TEST]Done.");
			break;
		case "debug":
			for(int i = 0; i < count; i++) {
				UCISender.getInstance().sendDebug(msg);
			}
			UCISender.getInstance().sendDebug("[TEST]Done.");
			break;
		case "error":
			for(int i = 0; i < count; i++) {
				UCISender.getInstance().sendError(msg);
			}
			UCISender.getInstance().sendDebug("[TEST]Done.");
		}
	}

}
