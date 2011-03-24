package org.micropsi.comp.robot.khepera8.khepera;

import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class TestCommPortClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String comPort = "/dev/ttyS0";
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
		// logger.debug("(Khepera) initializeeeee()...");

		while (pList.hasMoreElements()) {

			CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
			SerialPort com = null;
			System.out.println("found comport " + cpi.getName() + " TYPE: "
					+ cpi.getPortType());
			if ((cpi.getPortType() == CommPortIdentifier.PORT_SERIAL)
					&& cpi.getName().equals(comPort)) {

				try {
					com = (SerialPort) cpi.open("KHEPERA_" + comPort, 1000);
				} catch (PortInUseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					System.out.println("Currentsettings: " + com.getBaudRate()
							+ " " + com.getDataBits() + " " + com.getParity()
							+ " " + com.getStopBits());
					com.setSerialPortParams(57600,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_2,
							SerialPort.PARITY_NONE);
					System.out.println("AFTER SETUP: Currentsettings: " + com.getBaudRate()
							+ " " + com.getDataBits() + " " + com.getParity()
							+ " " + com.getStopBits());
					
				} catch (UnsupportedCommOperationException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
