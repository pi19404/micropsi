/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 18.01.2004
 *
 */
package org.micropsi.comp.world.objects;

/**
 * @author Matthias
 *
 */
public interface AgingIF {
	
	public void setAge(int age);
	public int getAge();
	public void becomeOlder(int ageDiff);

}
