/*
 * Created on Nov 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.robot.khepera5;




import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.comp.messages.MPerceptionResp;
import org.micropsi.comp.robot.RobotPerceptionExtractor;

/**
 * @author Daniel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestPerceptionExtractor extends RobotPerceptionExtractor{
	
	double pleft, pright;
	String pleft_string, pright_string;
		

	public MPerceptionResp extractPerception() {
		
		
		
		
		/*
		       pleft=0.4;
		       Double pleft_wrapper = new Double(pleft);
		       pleft_string = pleft_wrapper.toString();
		       MPercept blue_eye_left = new MPercept("blue_eye_left");
		       blue_eye_left.addParameter("blue_eye_left",pleft_string);
		       
		       
		       pright=0.4;
		       Double pright_wrapper = new Double(pright);
		       pright_string=pright_wrapper.toString();
		       MPercept blue_eye_right = new MPercept("blue_eye_right");
		       blue_eye_right.addParameter("blue_eye_right",pright_string);*/
		       
		       
		       
		       MPerceptionResp responseall = new MPerceptionResp();
		       /*responseall.addPercept(blue_eye_left);
		       responseall.addPercept(blue_eye_right);*/
		       return responseall;
				 
	}
		public void tick(long simStep) {
                      }
		/* (non-Javadoc)
		 * @see org.micropsi.comp.robot.RobotPerceptionExtractor#getConsoleQuestionContributions()
		 */
		public ConsoleQuestionTypeIF[] getConsoleQuestionContributions() {
			// TODO Auto-generated method stub
			return null;
		}
		/* (non-Javadoc)
		 * @see org.micropsi.comp.robot.RobotPerceptionExtractor#shutdown()
		 */
		public void shutdown() {
			// TODO Auto-generated method stub
			
		};
}	
		
	


