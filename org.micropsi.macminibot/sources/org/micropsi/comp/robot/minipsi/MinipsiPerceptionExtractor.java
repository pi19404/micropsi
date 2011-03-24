package org.micropsi.comp.robot.minipsi;

import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.comp.messages.MPerceptionResp;
import org.micropsi.comp.robot.RobotPerceptionExtractor;

public class MinipsiPerceptionExtractor extends RobotPerceptionExtractor {

	@Override
	public MPerceptionResp extractPerception() {
		return null;
	}

	@Override
	public ConsoleQuestionTypeIF[] getConsoleQuestionContributions() {
		return null;
	}

	@Override
	public void tick(long simStep) {
	}

	@Override
	public void shutdown() {
	}
}
