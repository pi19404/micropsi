package org.micropsi.comp.robot.minipsi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class MacMiniBot {
	
	
	private SerialPort port;
	private OutputStream out;

	public MacMiniBot(String devicename) throws IOException, MacMiniBotException {

		Enumeration identifiers = CommPortIdentifier.getPortIdentifiers();
		while(identifiers.hasMoreElements()) {
			CommPortIdentifier id = (CommPortIdentifier) identifiers.nextElement();
			System.err.println(id.getName());
			if(id.getName().equals(devicename)) {
				try {
					initialize(id);
				} catch (PortInUseException e) {
					throw new MacMiniBotException("Requested device in use",e);
				} catch (UnsupportedCommOperationException e) {
					throw new MacMiniBotException("Requested device does not support 9800 81N",e);
				}
				silence();
				return;
			}
		}	
		
		throw new MacMiniBotException("Requested device not found");
	}
		
	private void initialize(CommPortIdentifier id) throws PortInUseException, IOException, UnsupportedCommOperationException {
		port = (SerialPort)id.open("MacMiniBot",2000);

		out = port.getOutputStream();
		
		port.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.FLOWCONTROL_NONE);
		port.notifyOnOutputEmpty(true);
		
		out.write("BD0\n\r".getBytes());
	}
	
	public void silence() throws IOException {
		out.write("SV1M0SV2M0SV3M0\n\r".getBytes());
	}
	
//	public void setSpeed(int servo, int speed) throws IOException {
//		
//		if(servo == 1) {
//			speed += 131;			
//		} else if(servo == 2) {
//			speed += 126;
//		} else if(servo == 3) {
//			speed += 130;
//		}
//		
//		
//		out.write(("BD0SV"+servo+"M"+speed+"\n\r").getBytes());
//	}
	
	public void setSpeed(int servo1, int servo2, int servo3) {
		
	}
		
	public void shutdown() {
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
		}
		
		port.close();
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		MacMiniBot bot = new MacMiniBot("/dev/tty.usbserial-FTCCBANG");
		
//		while(true) {
//					
//			bot.setSpeed(1,-15);
//			bot.setSpeed(2,-15);
//			bot.setSpeed(3,-15);
//			
//			Thread.sleep(3000);
//
//			bot.setSpeed(1,0);
//			bot.setSpeed(2,0);
//			bot.setSpeed(3,0);
//	
//			Thread.sleep(3000);
//
//			
//			bot.setSpeed(1,15);
//			bot.setSpeed(2,15);
//			bot.setSpeed(3,15);
//	
//			Thread.sleep(3000);
//			
//			bot.setSpeed(1,0);
//			bot.setSpeed(2,0);
//			bot.setSpeed(3,0);
//	
//			Thread.sleep(3000);
//
//		}
		
		bot.silence();
		bot.shutdown();
		
	}

}
