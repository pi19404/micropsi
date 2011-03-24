package org.micropsi.nodenet;

/**
 * @author rvuine
 */
public interface UserInteractionIF {

	public String[] selectFromAlternatives(String[] alternatives);
	
	public String askUser(String prompt);
	
	public void displayInformation(String information);

}
