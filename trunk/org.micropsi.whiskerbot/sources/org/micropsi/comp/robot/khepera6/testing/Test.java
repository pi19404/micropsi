 package org.micropsi.comp.robot.khepera6.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.apache.log4j.Logger;


public class Test {

	//private BufferedWriter command;
	private OutputStreamWriter command;
	private BufferedReader answer;
	//private InputStreamReader answer;
	private SerialPort com1;	
	
	public Test() {
	};
	
	
	private void initialize() {
		
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
		System.out.println("(Manager) initialize()...");
		//System.out.println("pList: "+pList.hasMoreElements());
		while(pList.hasMoreElements()){
			CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
			//System.out.println("CommPortIdentifier = "+cpi.getName());
			if (cpi.getName().equals("COM1")){
				try {
					com1 = (SerialPort)cpi.open("KHEPERA_COM1",1000);
					try {
						//com1.setSerialPortParams(115200,8,2,0);
						//com1.setSerialPortParams(57600,8,2,0);
						 //try {
							 com1.setSerialPortParams(115200,
							 com1.DATABITS_8,
							 com1.STOPBITS_2,
							 com1.PARITY_NONE);
							 //com1.setFlowControlMode(com1.FLOWCONTROL_RTSCTS_IN);
							 //com1.setFlowControlMode(com1.FLOWCONTROL_RTSCTS_OUT);
							 //com1.setFlowControlMode(com1.FLOWCONTROL_XONXOFF_IN);
							 //com1.setFlowControlMode(com1.FLOWCONTROL_XONXOFF_OUT);
							 com1.setFlowControlMode(com1.FLOWCONTROL_NONE);
							 //} catch (UnsupportedCommOperationException e) {
							 //e.printStackTrace();}
							 /*
							 try {
							 outputStream.write(messageString.getBytes());
							 } catch (IOException e) {
							 e.printStackTrace();
							 }
							 */
					} catch (UnsupportedCommOperationException e1) {
						System.out.println("(Manager) UnsupportedCommOperation: "+e1);
					}
					System.out.println("(Manager)established COM1");
					//command = new BufferedWriter(new OutputStreamWriter(com1.getOutputStream()));//,15);
					command = new OutputStreamWriter(com1.getOutputStream());//,15);
					answer = new BufferedReader(new InputStreamReader(com1.getInputStream()));//,50);
					//answer = new InputStreamReader(com1.getInputStream());//,50);
				} catch (PortInUseException e) {
					System.out.println("(Manager) Port in use: "+e);
				} catch (IOException e) {
					System.out.println("IOException while initialising COM1: "+e);
				}
				break;
			}
		}
		/*
		try {
			command.write("restart\n");
		} catch (IOException e) {
			System.out.println("restart failed");
			e.printStackTrace();
		}
		*/
		System.out.println("(Manager)initialize() finished");
	}

	
	public void shutdown(){
		try {
			command.write("restart\n");
			//command.newLine();
			command.flush();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			command.close();
		} catch (IOException e) {
		}
		try {
			answer.close();
		} catch (IOException e1) {
		}
	}
	
	private void flush2(int ms){	
		
			wait(ms);
			
			try {
				command.flush();
			} catch (IOException e) {
				System.out.println("flush() IOException!!");
				e.printStackTrace();
			}
			/*
			if ((DSR && CTS && DTR && CD &&RTS)) {
				System.out.println("||DSR:"+DSR+" CTS:"+CTS+" DTR:"+DTR
						+" CD:"+CD+" RTS:"+RTS);
				try {
					command.flush();
				} catch (IOException e) {
					System.out.println("flush() IOException!!");
					e.printStackTrace();
				}
				help = false;
			}
			else System.out.println("DSR:"+DSR+" CTS:"+CTS+" DTR:"+DTR
							+" CD:"+CD+" RTS:"+RTS);
			*/			
	//	}while(help);
		
	}
	private void flush2(){	
		
		wait(1);
		
		try {
			command.flush();
		} catch (IOException e) {
			System.out.println("flush() IOException!!");
			e.printStackTrace();
		}	
	}
	
	
	public void wait(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e2) {
			System.out.println("(Prox) Thread.sleep error!");
			e2.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
	
		String s = null;
		long start = 0;
		long stop = 0;
		long max = -1000;
		long min = 1000;
		long sum = 0;
		long sum1 = 0;
		long sum2 = 0;
		long sum3 = 0;
		long sum4 = 0;
		long sum5 = 0;
		int i = 0;
		int value = 0;
		int not_ready = 0;
		char[] cbuf = new char[100];
		
		Test test = new Test();
		test.initialize();
		
		//try {
			//test.command.write("D,2,-2\n");
		
			/*
			
			
			test.command.write("run sloader\n");
			test.flush2();

			try {
				String ss = "";
				do{
					if(test.answer.ready()) {
						ss = test.answer.readLine();
						System.out.println(ss);
					}
				}while(!(ss.equals("S format Motorola loader mode")));
					System.out.print(".");
				
				System.out.println("ready to send S-program...");
			} catch (IOException e1) {
				System.out.println("loader mode failed");
				e1.printStackTrace();
			}
			
			
			
			BufferedReader sprogram = null;
						
			try {
				sprogram = new BufferedReader(new FileReader("C:/ir_prox.s37"));
				} catch (RuntimeException e1) {
					e1.printStackTrace();
				}
			
			try {
				String pdata = null;			
				while((pdata=sprogram.readLine()) != null){
					//System.out.println(pdata);
					test.command.write(pdata);
					test.flush2();
				}
				sprogram.close();
			} catch (IOException e) {
				System.out.println("readline() error"+e);
			}
			
			
			try {
				String ss = "";
				do{
					if(test.answer.ready()) {
						ss = test.answer.readLine();
						System.out.println(ss);
					}
				}while(!(ss.equals("S: download terminated")));
					System.out.print(".");
				
				System.out.println("ready to get hatto...");
			} catch (IOException e1) {
				System.out.println("download terminated failed");
				e1.printStackTrace();
			}	
			System.out.println("==================================================================");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
*/
		
		/*
		try {
			String ss = "";
			do{
				if(test.answer.ready()) {
					ss = test.answer.readLine();
					System.out.println(ss);
				}
			}while(!(ss.equals("hatto")));
				System.out.print(".");
			
			System.out.println("ready to get prox...");
		} catch (IOException e1) {
			System.out.println("hatto failed");
			e1.printStackTrace();
		}
		*/
		System.out.println("==================================================================");

		
		final BufferedReader tastatur = new BufferedReader(new InputStreamReader(System.in));
		
		
		try {
			test.command.write("D,0,0\n");
			test.flush2();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("==================================================================");
		
		String s1 = "";
		boolean flip = true;
		int j = 0;
		int h = 0;
		
		
		for (i=0;i<1000000000;i++){			
		try {
			//start = System.currentTimeMillis();
			if(test.answer.ready()) {
				//System.out.println("ready() = true"); 
				++j;
				test.wait(5);
				//start = System.currentTimeMillis();
				for (h=0;h<cbuf.length;h++){
					if((cbuf[h] = (char)test.answer.read())==10){ 
						//System.out.println("cbuf["+h+"] = \\n");		
						break;
					}
					//else System.out.println("cbuf:"+String.valueOf(cbuf));	
				}
				//System.out.println("cbuf fertig:"+String.valueOf(cbuf));
				
				//test.wait(5);
				//start = System.currentTimeMillis();
				//s1 = test.answer.readLine();
				s1 = String.valueOf(cbuf,0,h-1);
				//stop = System.currentTimeMillis() - start;
				
				//start = System.currentTimeMillis();
				//System.out.println(s1);
				stop = System.currentTimeMillis() - start;
				
				if((i%1)==0)
					System.out.println(s1+"\t"+stop+"||"+(int)(sum1/(j/5.0))+"|"+(int)(sum2/(j/5.0))+"|"+
							+(int)(sum3/(j/5.0))+"|"+(int)(sum4/(j/5.0))+"|"+(int)(sum5/(j/5.0))+"||"+
							not_ready);
				not_ready = 0;
				
				//long stop_help = System.currentTimeMillis();
				//while((System.currentTimeMillis()-stop_help)<100){}
				
				test.wait(1);
				
				
				
				
				start = System.currentTimeMillis();
				try {
					switch (i%5){
					case 0: test.command.write("L,0,2\n"); sum1 += stop; break;
					case 1: test.command.write("O\n");     sum2 += stop; break;
					case 2: test.command.write("L,1,2\n"); sum3 += stop; break;
					case 3: test.command.write("N\n");     sum4 += stop; break;
					case 4: test.command.write("D,0,0\n"); sum5 += stop; break;
					default: break;//System.out.println("switch: default!"); break;
					}
					test.flush2();
					//test.wait(10);
//					if(flip){test.command.write("N\n"); sum1 += stop;}
//					else {test.command.write("O\n"); sum2 += stop;}
//					test.flush2();
//					flip = !flip;
//				    
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				//stop = System.currentTimeMillis() - start;
				
				
			}
			else {
				++not_ready;
				//System.out.println("ready() = false");
				test.wait(1);
			}
			//test.wait(20);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		
		
		
		for (i=0;i<10000;i++){
			start = System.currentTimeMillis();
			
			System.out.print("press key...");
			try {
				tastatur.readLine();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			try {
				tastatur.readLine();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
			
			try {
				test.command.write("D,2,4\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("write fertig. press key...");

			try {
				tastatur.readLine();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			test.flush2();
			System.out.println("flush fertig. answer.readLine():");
	
			try {
				start = System.currentTimeMillis();
				
				//if(test.answer.ready()) 
				{
					System.out.println(test.answer.readLine());
					System.out.println(System.currentTimeMillis()-start+" ms");
				}
				test.wait(20);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		for (i=0;i<20;i++){
			start = System.currentTimeMillis();
			
			try {
				test.command.write("N\n");
				test.flush2();
				if(test.answer.ready()) System.out.println(test.answer.readLine());
				test.wait(20);
				test.command.write("L,1,2\n");
				test.flush2();
				test.wait(20);
				test.command.write("D,"+i%4+",-1\n");
				test.flush2();
				test.wait(20);
				test.command.write("O\n");
				test.flush2();
				if(test.answer.ready()) System.out.println(test.answer.readLine());
				test.wait(20);
				test.command.write("L,0,2\n");
				test.flush2();
				test.wait(20);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/*
		try {
				//test.command.write("N\n");
				//test.flush2();
				//value = test.answer.read(cbuf);
				
//				test.command.write("0\n");
//				test.flush2();
				if(test.answer.ready()){
					System.out.println(i+":" +test.answer.readLine());
					test.answer.mark(500);
				}
				else {System.out.println("answer.ready() = false");
					test.answer.reset();
					
				}
					
			} catch (IOException e) {
				System.out.println("IOException");	
				e.printStackTrace();
			}
			stop = System.currentTimeMillis() - start;
			
			if (stop < min)
				min = stop;
			if (stop > max)
				max = stop;
			sum += stop;
			
			//if((i%100)==0) 
				//System.out.println(i+"\t"+value+"|"+String.valueOf(cbuf)+"\t\t"+(stop)+"ms");
			try {
				if((i%100)==0)
					test.command.write("D,2,-2\n");
				if((i%100)==50)
					test.command.write("D,-2,2\n");
			} catch (IOException e) {
				System.out.println("speed failed");
				e.printStackTrace();
			}
			
		
		}
		*/
		System.out.println("mean: "+(sum/i)+"ms\tmin: "+min+"ms\tmax: "+max+"ms");
		
		
		test.shutdown();	
	}
}
