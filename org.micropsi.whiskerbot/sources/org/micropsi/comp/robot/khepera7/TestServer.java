package org.micropsi.comp.robot.khepera7;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class TestServer {

	public static void main(String args[]) {
		final int CYCLES = 100;
		Logger logger;
		boolean debug = false;
		long start = 0, go = 0;
		ArrayList l = new ArrayList();
		logger = Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new PatternLayout()));
		logger.setLevel(Level.DEBUG);
		Khepera khepera = new Khepera("COM1", logger, debug);
		// Khepera khepera = new Khepera("COM1",logger, debug, l);

		//khepera.restart();
		khepera.update.setUpdate(true);
		logger.debug("restarted");
		go = System.currentTimeMillis();

		for (int i = 0; i < CYCLES; i++) {

			// System.out.println(i%4);
			switch (i % 4) {
			case 0: {
				start = System.currentTimeMillis();
				khepera.setLED0("2");
				// System.out.println(System.currentTimeMillis() + "\tLED0\t"
				// + (System.currentTimeMillis() - start));
				l
						.add((String) (System.currentTimeMillis() + "\tLED0\t" + (System
								.currentTimeMillis() - start)));
				break;
			}
			case 1: {
				start = System.currentTimeMillis();
				khepera.setSpeed_motor("2", "2");
				// System.out.println(System.currentTimeMillis() + "\tSpeed1\t"
				// + (System.currentTimeMillis() - start));
				l
						.add((String) (System.currentTimeMillis()
								+ "\tSpeed1\t" + (System.currentTimeMillis() - start)));
				break;
			}
			case 2: {
				start = System.currentTimeMillis();
				khepera.setLED1("2");
				// System.out.println(System.currentTimeMillis() + "\tLED1\t"
				// + (System.currentTimeMillis() - start));
				l
						.add((String) (System.currentTimeMillis() + "\tLED1\t" + (System
								.currentTimeMillis() - start)));
				break;
			}
			case 3: {
				start = System.currentTimeMillis();
				khepera.setSpeed_motor("-2", "-2");
				// System.out.println(System.currentTimeMillis() + "\tSpeed2\t"
				// + (System.currentTimeMillis() - start));
				l
						.add((String) (System.currentTimeMillis()
								+ "\tSpeed2\t" + (System.currentTimeMillis() - start)));
				break;
			}
			default:
				break;
			}

			khepera.getLs1();
			khepera.getPs1();
			 System.out.println("(Test) Ls1="+khepera.getLs1()+"\tPs1="+khepera.getPs1());
			 System.out.println("");
			try {
				int t;
				System.out.println(t = 120 + (int) ((Math.random() * 40) - 20));
				Thread.sleep(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		khepera.update.setUpdate(false);

		khepera.shutdown();

		l.addAll(khepera.update.getArrayList());

		Collections.sort(l);
		Iterator it = l.iterator();
		while (it.hasNext()) {
			StringTokenizer tok = new StringTokenizer((String) it.next());
			System.out.print(Long.valueOf((String) tok.nextElement()) - go);
			String str = (String) tok.nextElement();
			if (str.equals("Prox") || str.equals("Light"))
				System.out.print("\t\t" + str);
			else
				System.out.print("\t" + str);

			System.out.println("\t" + (String) tok.nextElement());
		}
		System.out.println();

		System.out.println(System.currentTimeMillis() - go + "(Test) ende");

	}

}