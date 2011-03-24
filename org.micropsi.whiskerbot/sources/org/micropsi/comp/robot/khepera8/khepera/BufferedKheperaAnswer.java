package org.micropsi.comp.robot.khepera8.khepera;

import java.io.IOException;
import java.io.InputStream;
//import java.util.concurrent.locks.Lock;

import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

import org.apache.log4j.Logger;

public class BufferedKheperaAnswer extends Thread implements
		SerialPortEventListener {

	
	private boolean debug = false;
	private Logger logger;
	private InputStream inStream;
	private boolean update = true;
	
	private Object lock;
	
	private StringBuffer answer = new StringBuffer();
	
	private String answer_help = "test";
	private Khepera khepera;
	private String answer_complete2;
	private String answer_complete;
	private Object lock2 = new Object();
	private String operatingSystem = null;

	
	
	public BufferedKheperaAnswer(Logger logger, InputStream inStream, Khepera khepera, 
			Object lock,  boolean debug, String operatingSystem){
		this.logger = logger;
		this.inStream = inStream;
		this.lock = lock;
		this.khepera = khepera;
		this.debug = debug;
		this.operatingSystem = operatingSystem;
		
		logger.debug("[BufferedKheperaAnswer] constructor...");
	}
	
	public void run(){
		
		logger.debug("[BufferedKheperaAnswer.run()] run()...");
		
		while(getUpdate()){					
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.debug("[BufferedKheperaAnswer.run()] stop run()");
	}	
	
	public synchronized void setUpdate(boolean update){
		this.update = update;
	}
	
	public synchronized boolean getUpdate(){
		return update;
	}
	
	public void serialEvent(SerialPortEvent event) {
		
//	  synchronized(lock) {
		if(debug ) logger.debug("BufferedKheperaAnswer.serialEvent()] eventType: "+event.getEventType()+
				", new value: "+event.getNewValue()+", old value: "+event.getOldValue()+
				", source: "+event.getSource());
		
		switch(event.getEventType())
		{
		case SerialPortEvent.OE://7 
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" OE "+SerialPortEvent.OE);
			break;
		case SerialPortEvent.FE://9
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" FE "+SerialPortEvent.FE);
			break;
		case SerialPortEvent.PE://8 
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" PE "+SerialPortEvent.PE);
			break;
		case SerialPortEvent.DSR://4
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" DSR "+SerialPortEvent.DSR);
			break;
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY: //2 
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+"  OUTPUT_BUFFER_EMPTY "+SerialPortEvent.OUTPUT_BUFFER_EMPTY);
			break;
		case SerialPortEvent.BI: 
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" BI "+SerialPortEvent.BI);
			if(debug)logger.error("BufferedKheperaAnswer.serialEvent()] Break Interrupt");
			break;
		case SerialPortEvent.CTS://3 
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" CTS "+SerialPortEvent.CTS);
			if(debug)logger.error("BufferedKheperaAnswer.serialEvent()] Clear to send");
			break;
		case SerialPortEvent.RI: 
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" RI "+SerialPortEvent.RI);
			if( event.getNewValue() ) {
				if(debug)logger.error("BufferedKheperaAnswer.serialEvent()] Ring Indicator On");
			}
			else {
				if(debug)logger.error("BufferedKheperaAnswer.serialEvent()] Ring Indicator Off");
			}
			break;
		case SerialPortEvent.CD://6
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" CD "+SerialPortEvent.CD);
			if(debug)logger.error("Working");
			if( event.getNewValue() ) {
				if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+event.getNewValue());
				if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] Connected");
			}
			else
			{
				if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] Disconnected");
			}
			break;
		case SerialPortEvent.DATA_AVAILABLE: //1
			if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] "+System.currentTimeMillis()+" DATA_AVAILABLE "+SerialPortEvent.DATA_AVAILABLE);
			byte[] readBuffer = new byte[50];
			try {
				while (inStream.available() > 0) {
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					int numBytes = inStream.read(readBuffer);
					String result = new String(readBuffer,0,numBytes);
					
					if(debug) logger.debug("BufferedKheperaAnswer.serialEvent()] DATA_AVAILABLE) result="+result);
					if(debug)logger.debug("BufferedKheperaAnswer.serialEvent()] ("+System.currentTimeMillis()+") result="+result+" | length()="+result.length());
					
					int i=0;
					int j = result.charAt(i);
					while(	i<(result.length()-1)
							&&
							(
									j==10
									|| j==13
									|| j==32
									|| j==38
									|| j==40
									|| j==41
									|| (j>=44 && j<=57)
									|| j==60
									|| (j>=64 && j<=90)
									|| j==92
									|| j==95
									|| (j>=97 && j<=122)
									|| j==124
							)
					){ j = result.charAt(++i);}
//					logger.debug("Hat Was Erhalten in der Serial Event!!!!!!!!!!!!!");
					if(debug)logger.debug("i="+i+", j="+j+"(='"+(char)j+"')");
					if(i==(result.length()-1)){
						if(operatingSystem.equals("Linux"))
							readMeLINUX(result); 
						else
							readMeWINDOWS(result);
					}
					else {
						logger.debug("BufferedKheperaAnswer.serialEvent()] result failed character filter! => skip next 50 characters from inputStream...");
						inStream.skip(50);
					}
				}
			}
			catch (IOException e) {
				logger.error("[BufferedKheperaAnswer.serialEvent()] Failed to read",e);
				e.printStackTrace();
			}
			break;
		default: logger.debug("[BufferedKheperaAnswer.serialEvent()] unknown event:"+event);
		}
	//}//end synchronized 
	}
	

	private void readMeLINUX(String readed)
	{
//		synchronized(lock)
		{
		if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()]("+System.currentTimeMillis()
					+") before append: answer="+answer.toString()+" | length()="+answer.length());
		
		answer.append(readed);
		
		if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()]("+System.currentTimeMillis()
				+") after append: answer="+answer.toString()+" | length()="+answer.length());
		
		answer_help = answer.toString();
		
		int i=0,j=0;
		boolean flag = false;
		
		if( (i = answer_help.indexOf("n,"))!=(-1) 
				&& (j = answer_help.indexOf("\n\n"))!=(-1)
				&& (i<j)
				&& (j-i)>16 && (j-i)<42 
		)
		{
			flag = true;
			if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 1");
		} 
		else
			if( (i = answer_help.indexOf("o,"))!=(-1) 
					&& (j = answer_help.indexOf("\n\n"))!=(-1)
					&& (i<j)
					&& (j-i)>16 && (j-i)<34 
			)
			{
				flag = true;
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 2");
		} 
			else
				if( (i = answer_help.indexOf("Command not found"))!=(-1) 
						&& (j = answer_help.indexOf("\n\n"))!=(-1)
						&& (i<j)
						&& (j-i)==17 
				)
				{
					flag = true;
					if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 3");
				} 
				else
					if( (i = answer_help.indexOf("d\n"))!=(-1) 
							&& (j = answer_help.indexOf("\n\n"))!=(-1)
							&& (i<j)
							&& (j-i)==1 
					)
					{
						flag = true;
						if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 4");
					}
					else
						if( (i = answer_help.indexOf("l\n"))!=(-1) 
								&& (j = answer_help.indexOf("\n\n"))!=(-1)
								&& (i<j)
								&& (j-i)==1 
						)
						{
							flag = true;
							if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 5");
						}
						else
							if( (i = answer_help.indexOf("\n\n\n\n\n\n (c) 1992-2001,"))!=(-1) 
									&& (j = answer_help.indexOf("Communication Protocol\n\n"))!=(-1)
									&& (i<j)
									&& (j-i)>400 && (j-i)<550 
							)
							{
								flag = true;
								if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 6");
								j = j+22;
							} 
							else
								if( (i = answer_help.indexOf("z,This LED does not "))!=(-1) 
										&& (j = answer_help.indexOf("exist\n\n"))!=(-1)
										&& (i<j)
										&& (j-i)==20
								)
								{
									flag = true;
									if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 7");
									j = j+5;
								} 
								else
									if( (i = answer_help.indexOf("z,Protocol "))!=(-1) 
											&& (j = answer_help.indexOf("error\n\n"))!=(-1)
											&& (i<j)
											&& (j-i)>10 && (j-i)<15 
									)
									{
										if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 8");
										flag = true;
										j = j+5;
									}
									else
										if( (i = answer_help.indexOf("z,This LED action is not "))!=(-1) 
												&& (j = answer_help.indexOf("possible\n\n"))!=(-1)
												&& (i<j)
												&& (j-i)==25 
										)
										{
											if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] filter 9");
											flag = true;
											j = j+8;
										}
		
		
			if(debug) {
				logger.debug("[BufferedKheperaAnswer.readMeLINUX()] answer_help="+answer_help+" | char:... ");
				for(int k=0;k<answer_help.length();k++){
					logger.debug(" "+(int)answer_help.charAt(k)+" ");
				}
			}
			if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] flag="+flag);
					
			if (flag){
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] answer_help.sub("+i+","+j+")="+answer_help.substring(i,j));
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] answer_help.sub("+i+","+j+")="+answer_help.substring(i,j)
						+",  last 2 numbers as chars: "+(int)answer_help.charAt(answer_help.length()-2)+","+(int)answer_help.charAt(answer_help.length()-1));
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()]+++++++++++++++++++++++++++++++++++++");
//				logger.debug("Ist fertig und setzt Anwort!!!!!!!!!!!!!"+answer_help.substring(i,j));
				khepera.setAnswer_complete(answer_help.substring(i,j));
//				logger.debug("Sent Answer!!!!!!!!!!!!!!!!!!!!!!!!!!");
//				khepera.answer_complete = ;
//				if(khepera.answer_complete == null)
//					logger.debug("[BufferedKheperaAnswer.readMeLINUX()] kann nicht sein! answer_complete==0, answer="+answer+" answer_help="+answer_help);
				answer.setLength(0);
				try {
					if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] inStream.available()="+inStream.available());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeLINUX()] lock.notify() ("+System.currentTimeMillis()+")");
				//lock.notify();
			}
			else if(answer.length()>700){
				logger.error("[BufferedKheperaAnswer.readMeLINUX()] error: data lost!! answer.length()>700 => reset to answer.length()=0");
				logger.debug("[BufferedKheperaAnswer.readMeLINUX()] ++++buffer dump: answer="+answer+", length="+answer.length());
				logger.debug("[BufferedKheperaAnswer.readMeLINUX()] ++++advise: check filter rules!");
				answer.setLength(0);
			}
		}
	}
	
	
	private void readMeWINDOWS(String readed)
	{
//		synchronized(lock)
		{
			if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()]("+System.currentTimeMillis()
					+") before append: answer="+answer.toString()+" | length()="+answer.length());
		answer.append(readed);
		if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()]("+System.currentTimeMillis()
				+") after append: answer="+answer.toString()+" | length()="+answer.length());
		answer_help = answer.toString();
		
		int i=0,j=0;
		boolean flag = false;
		
		if( (i = answer_help.indexOf("n,"))!=(-1) 
				&& (j = answer_help.indexOf("\r\n"))!=(-1)
				&& (i<j)
				&& (j-i)>16 && (j-i)<42 
		)
		{
			flag = true;
			if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 1");
		} 
		else
			if( (i = answer_help.indexOf("o,"))!=(-1) 
					&& (j = answer_help.indexOf("\r\n"))!=(-1)
					&& (i<j)
					&& (j-i)>16 && (j-i)<34 
			)
			{
				flag = true;
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 2");
		} 
			else
				if( (i = answer_help.indexOf("Command not found"))!=(-1) 
						&& (j = answer_help.indexOf("\r\n"))!=(-1)
						&& (i<j)
						&& (j-i)==17 
				)
				{
					flag = true;
					if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 3");
				} 
				else
					if( (i = answer_help.indexOf("d\r"))!=(-1) 
							&& (j = answer_help.indexOf("\n"))!=(-1)
							&& (i<j)
							&& (j-i)==2 
					)
					{
						flag = true;
						if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 4");
					}
					else
						if( (i = answer_help.indexOf("l\r"))!=(-1) 
								&& (j = answer_help.indexOf("\n"))!=(-1)
								&& (i<j)
								&& (j-i)==2 
						)
						{
							flag = true;
							if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 5");
						}
						else
							if( (i = answer_help.indexOf("\r\n\r\n\r\n (c) 1992-2001,"))!=(-1) 
									&& (j = answer_help.indexOf("Communication Protocol\r\n"))!=(-1)
									&& (i<j)
									&& (j-i)>400 && (j-i)<550 
							)
							{
								flag = true;
								if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 6");
								j = j+22;
							} 
							else
								if( (i = answer_help.indexOf("z,This LED does not "))!=(-1) 
										&& (j = answer_help.indexOf("exist\r\n"))!=(-1)
										&& (i<j)
										&& (j-i)==20
								)
								{
									flag = true;
									if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 7");
									j = j+5;
								} 
								else
									if( (i = answer_help.indexOf("z,Protocol "))!=(-1) 
											&& (j = answer_help.indexOf("error\r\n"))!=(-1)
											&& (i<j)
											&& (j-i)>10 && (j-i)<15 
									)
									{
										if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 8");
										flag = true;
										j = j+5;
									}
									else
										if( (i = answer_help.indexOf("z,This LED action is not "))!=(-1) 
												&& (j = answer_help.indexOf("possible\r\n"))!=(-1)
												&& (i<j)
												&& (j-i)==25 
										)
										{
											if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] filter 9");
											flag = true;
											j = j+8;
										}
		
		
			if(debug) {
				logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] answer_help="+answer_help+" | char:... ");
				for(int k=0;k<answer_help.length();k++){
					logger.debug(" "+(int)answer_help.charAt(k)+" ");
				}
			}
			if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] flag="+flag);
					
			if (flag){
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] answer_help.sub("+i+","+j+")="+answer_help.substring(i,j));
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] answer_help.sub("+i+","+j+")="+answer_help.substring(i,j)+",  "+(int)answer_help.charAt(answer_help.length()-2));
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()]+++++++++++++++++++++++++++++++++++++");
				khepera.setAnswer_complete(answer_help.substring(i,j));
//				khepera.answer_complete = ;
//				if(khepera.answer_complete == null)
//					logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] kann nicht sein! answer_complete==0, answer="+answer+" answer_help="+answer_help);
				answer.setLength(0);
				try {
					if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] inStream.available()="+inStream.available());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(debug)logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] lock.notify() ("+System.currentTimeMillis()+")");
				//lock.notify();
			}
			else if(answer.length()>700){
				logger.error("[BufferedKheperaAnswer.readMeWINDOWS()] error: data lost!! answer.length()>700 => reset to answer.length()=0");
				logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] ++++buffer dump: answer="+answer+", length="+answer.length());
				logger.debug("[BufferedKheperaAnswer.readMeWINDOWS()] ++++advise: check filter rules!");
				answer.setLength(0);
			}
		}
	}
}