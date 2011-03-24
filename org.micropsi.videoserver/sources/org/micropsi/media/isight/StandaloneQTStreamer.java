package org.micropsi.media.isight;
/* relased under terms of the MIT public license 

Copyright (c) 2005, Chris Adamson, invalidname@mac.com


Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:


The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.


THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.time.Tasking;
import quicktime.io.QTFile;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.streaming.MediaParams;
import quicktime.streaming.PresParams;
import quicktime.streaming.Presentation;
import quicktime.streaming.QTSConstants;
import quicktime.streaming.SettingsDialog;
import quicktime.streaming.Stream;

public class StandaloneQTStreamer extends Tasking implements ActionListener {

    boolean broadcasting = false;

    public static final int BROADCAST_WIDTH = 320;
    public static final int BROADCAST_HEIGHT = 240;

    Button startStopButton;
    Button configButton;

    Presentation pres;
    int presenterTimeScale = 600;

    public static void main (String[] args) {
        System.out.println ("main");
        try {
            QTSession.open();
            new StandaloneQTStreamer();
        } catch (QTException qte) {
            qte.printStackTrace();
        }
    }

    public StandaloneQTStreamer() throws QTException {
        System.out.println ("LittleBroadcast constructor");
        QTFile file = new QTFile (new File ("little.sdp"));
		try {
			MediaParams mediaParams = new MediaParams();
            mediaParams.setWidth (BROADCAST_WIDTH);
            mediaParams.setHeight (BROADCAST_HEIGHT);

            QDGraphics myGWorld = new QDGraphics (new QDRect (BROADCAST_WIDTH, BROADCAST_HEIGHT));
            mediaParams.setGWorld (myGWorld);

			PresParams presParams =
                new PresParams( presenterTimeScale,
                                QTSConstants.kQTSSendMediaFlag | 
                                QTSConstants.kQTSAutoModeFlag |
                                QTSConstants.kQTSDontShowStatusFlag,
                                mediaParams );
			pres = Presentation.fromFile( file, presParams );

            // find audio stream
            Stream audioStream = null;
            for (int i=1; i<=pres.getNumStreams(); i++) {
                System.out.println ("stream: " + i + ": " + 
                                    pres.getIndStream(i));
                Stream aStream = pres.getIndStream (i);
                if (pres.hasCharacteristic(aStream, 
                                           StdQTConstants.audioMediaCharacteristic)) {
                    audioStream = aStream;
                    break;
                }
            }
            System.out.println ("audioStream = " + audioStream);

            pres.setVolumes (audioStream, 100, 100);
            System.out.println ("created presentation, gworld == " +
                                pres.getGWorld() + ", size == "+
                                mediaParams.getWidth() + "x" + 
                                mediaParams.getHeight() + ", streams == " +
                                pres.getNumStreams());  
            
            //SettingsDialog sd = 
            new SettingsDialog (pres);
            System.out.println ("Did settings");
            pres.preroll();
            broadcasting = false;

            // Make monitor window
            startStopButton = new Button ("Start");
            configButton = new Button ("Configure");
            startStopButton.addActionListener (this);
            configButton.addActionListener (this);
            Frame monitorFrame = new Frame ("QTJ Streaming");
            monitorFrame.setLayout (new BorderLayout());
            Panel buttonPanel = new Panel();
            buttonPanel.add (startStopButton);
            buttonPanel.add (configButton);
            monitorFrame.add (buttonPanel, BorderLayout.SOUTH);
            monitorFrame.pack();
            monitorFrame.setVisible(true);

            // add shutdown handler to make sure presentation 
            // gets stopped
            Thread presentationStopper = new Thread() {
                    public void run() {
                        try {
                            pres.stop();
                        } catch (QTException qte) {}
                    }
                };
            Runtime.getRuntime().addShutdownHook (presentationStopper);

		} catch ( QTException e ) {
			e.printStackTrace();
			System.exit (-1);
		}
    }

    public void actionPerformed (ActionEvent ae) {
        System.out.println ("actionPerformed");
        try {
            if (ae.getSource() == startStopButton) {
                if (broadcasting) {
                    pres.stop();
                    stopTasking();
                    broadcasting = false;
                    startStopButton.setLabel ("Start");
                    System.out.println ("Stopped");
                } else {
                    pres.start();
                    startTasking();
                    broadcasting = true;
                    startStopButton.setLabel ("Stop");
                    System.out.println ("Started");
                }
            } else if (ae.getSource() == configButton) {
                new SettingsDialog (pres);
            }
        } catch (QTException qte) {
            qte.printStackTrace();
        }
    }

	public synchronized final void task() throws QTException {
		pres.idle(null);
	}

}
