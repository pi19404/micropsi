package org.micropsi.nodenet;

import org.apache.log4j.Logger;

class UserInteractionManager implements UserInteractionIF {
	
	private Logger logger;
	private UserInteractionIF interaction;
	
	public UserInteractionManager(Logger logger) {
		this.logger = logger;
	}
	
	protected void setUserInteractionImplementation(UserInteractionIF interaction) {
		this.interaction = interaction;
	}

	protected UserInteractionIF getUserInteractionImplementation() {
		return interaction;
	}
	
	public String[] selectFromAlternatives(String[] alternatives) {
		if(interaction == null) {
			logger.warn("User interaction requested (selectFromAlternatives), but no user interaction facility found");
			return new String[0];
		}
		return interaction.selectFromAlternatives(alternatives);
	}

	public String askUser(String prompt) {
		if(interaction == null) {
			logger.warn("User interaction requested (askUser), but no user interaction facility found");
			return null;
		}
		return interaction.askUser(prompt);
	}

	public void displayInformation(String information) {
		if(interaction == null) {
			logger.warn("User interaction requested (displayInformation), but no user interaction facility found");
			return;
		}
		interaction.displayInformation(information);
	}
	
}
