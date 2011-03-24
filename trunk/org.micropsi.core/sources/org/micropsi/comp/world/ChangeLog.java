/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 04.10.2003
 *
 */
package org.micropsi.comp.world;

import java.util.ArrayList;
import java.util.Collection;

import org.micropsi.comp.world.objects.AbstractObject;

/**
 * @author Matthias
 *
 */
public class ChangeLog {
	private ArrayList<ChangeLogEntry> changeLog;
	private int maxLogTime;
	
	private long currentTick = -1;
	private long loggedSince = 0;

	/**
	 * 
	 */
	public ChangeLog(int maxLogTime, World world) {
		this.maxLogTime = maxLogTime;
		changeLog = new ArrayList<ChangeLogEntry>(maxLogTime*5);
		currentTick = world.getSimStep();
		loggedSince = currentTick;
	}
	
	public ChangeLogEntry addChange(AbstractObject object, long tick, int changeType) {
		ChangeLogEntry entry = new ChangeLogEntry(object, tick, changeType);
		changeLog.add(entry);
		return entry;
	}
	
	public void updateChangeLogEntry(ChangeLogEntry entry, long tick, int changeType) {
		if (tick == entry.getChangeTime()) {
			entry.addChangeType(changeType);
		} else {
			int oldIndex = changeLog.indexOf(entry);
			entry.setChangeTime(tick);
			if (oldIndex < 0) {
				entry.setChangeType(changeType);
				changeLog.add(entry);
			} else {
				entry.addChangeType(changeType);
				changeLog.remove(oldIndex);
				changeLog.add(entry);
			}
		}
	}
	
	/**Returns the oldest time since which you can get object changes from this log.
	 * If your last information is older, you should get the complete object list instead.
	 * @see World#getObjects()
	 * 
	 * @return the oldest tick that is logged
	 */
	public long getOldestPreservedTick() {
		return Math.max(currentTick - maxLogTime, loggedSince);
	}
	
	public void updateTime(long tick) {
		int i = 0;
		currentTick = tick;
		long lastPreservedTick = getOldestPreservedTick();
		while (i < changeLog.size() && changeLog.get(i).getChangeTime() < lastPreservedTick) {
			i++;
		}
		if (i > 0) {
			changeLog.subList(0, i).clear();
		}
	}
	
	public Collection<ChangeLogEntry> getChangeEntries(long tick) {
		Collection<ChangeLogEntry> res = new ArrayList<ChangeLogEntry>();
		int i = changeLog.size() - 1;
		while (i >= 0 && changeLog.get(i).getChangeTime() > tick) {
			res.add(changeLog.get(i));
			i--;
		}
		return res;
	}

	public Collection<ChangeLogEntry> getChangeEntries(long tick, int changeType) {
		Collection<ChangeLogEntry> res = new ArrayList<ChangeLogEntry>();
		int i = changeLog.size() - 1;
		while (i >= 0 && changeLog.get(i).getChangeTime() > tick) {
			if ((changeLog.get(i).getChangeType() & changeType) != 0) {
				res.add(changeLog.get(i));
			}
			i--;
		}
		return res;
	}

	/**Forgets all changes, starts logging anew
	 * @param tick - since when logging will be reliable again.
	 * 
	 * @see ChangeLog#getOldestPreservedTick()
	 */
	public void reset(long tick) {
		loggedSince = tick;
		changeLog.clear();
	}

}
