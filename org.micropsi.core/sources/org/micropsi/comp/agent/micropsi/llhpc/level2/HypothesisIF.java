package org.micropsi.comp.agent.micropsi.llhpc.level2;

import java.awt.image.BufferedImage;

import org.micropsi.common.exception.MicropsiException;

public interface HypothesisIF {

	public double calculateMatch(BufferedImage img, int shiftx, int shifty) throws MicropsiException;

	public String getName();

}