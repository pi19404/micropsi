/*
 * Created on 22.04.2005
 *
 */
package org.micropsi.comp.agent;

import java.util.Random;

/**
 * @author Markus
 *
 */
public class RandomGenerator {
    public static Random generator = new Random();
    
    public static void setSeed(long seed) {
        generator.setSeed(seed);
    }
}
