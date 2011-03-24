/*
 * Created on 22.04.2005
 *
 */
package org.micropsi.comp.agent.genetics;

import org.micropsi.comp.agent.RandomGenerator;

/**
 * @author Markus
 *
 */
public class Genom {
    public static int FOOD = 0;
    public static int WATER = 1;
    public static int HEALING = 2;
    public static int AFFILIATION = 3;
    
    private static int GEN_COUNT = 4;
    private static double MAX_THRESHOLD = 1.0;
    private static double MIN_THRESHOLD = -1.0;
    
    private double[] genes;
    
    public Genom() {
        genes = new double[GEN_COUNT];
        for(int i = 0; i < GEN_COUNT; i++)
            genes[i] = 0.0;
    }
    
    public Genom(Genom genom) {
        this();
        if(genom != null) {
            for(int i = 0; i < GEN_COUNT; i++)
                genes[i] = genom.getGen(i);
        }
    }
    
    public double getGen(int index) {
        if(index < GEN_COUNT && index >= 0)
            return genes[index];
        else
            return 0.0;
    }
    
    public void setGen(int index, double value) {
        if(index < GEN_COUNT && index >= 0) {
            if(value < MIN_THRESHOLD)
                genes[index] = MIN_THRESHOLD;
            else if(value > MAX_THRESHOLD)
                genes[index] = MAX_THRESHOLD;
            else
                genes[index] = value;
        }
    }
    
    public Genom mutate() {
        Genom child = new Genom();
        for(int i = 0; i < GEN_COUNT; i++) {
            if(RandomGenerator.generator.nextBoolean()) {
                child.setGen(i, genes[i]);
                continue;
            }
            child.setGen(i, genes[i] + (RandomGenerator.generator.nextDouble() - 0.5) * 2.0);
        }
        
        return child;
    }
}
