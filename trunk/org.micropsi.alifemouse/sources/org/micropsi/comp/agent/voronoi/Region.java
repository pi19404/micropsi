package org.micropsi.comp.agent.voronoi;

import org.micropsi.common.coordinates.Position;
import org.micropsi.nodenet.NetEntity;

public class Region {
	public static final int NOTEXPLORED = -1;
	public static final int FOOD = 0;
	public static final int WATER = 1;
	public static final int HEALING = 2;
	public static final int DAMAGE = 3;
	public static final int IMPASSABLE = 4;
	
	public Position position;
	
	private NetEntity node = null;
	private boolean unknown = true;
	private boolean[] type = new boolean[5];
	
	public Region(Position position, int type, NetEntity node) {
		this(position, type);
		this.node = node;
	}
	
	public Region(Position position, int type) {
		this(position);
		for(int i = 0; i < this.type.length; i++)
			this.type[i] = false;
		this.type[type] = true;
		unknown = false;
	}
	
	public Region(Position position, NetEntity node) {
		this(position);
		this.node = node;
	}
	
	public Region(Position position) {
		this.position = position;
	}
	
	public void addType(int type) {
		this.type[type] = true;
		unknown = false;
	}
	
	public boolean isType(int type) {
		if(type >= 0 && type < this.type.length)
			return this.type[type];
		else if(type == -1) {
			for(int i = 0; i < this.type.length; i++) {
				if(this.type[i])
					return false;
			}
			return true;
		} else
			return false;
	}

	public NetEntity getNode() {
		return node;
	}

	public void setNode(NetEntity node) {
		this.node = node;
	}
	
	public boolean isUnknown() {
		return unknown;
	}
	
	public void known() {
		unknown = false;
	}
	
	public void unknown() {
		unknown = true;
	}
	
	public boolean equalsType(Region region) {
		boolean toReturn = true;
		
		// unknown regions cannot be compared
		if(region == null || this.unknown || region.isUnknown())
			return false;
		
		for(int i = 0; i < this.type.length; i++) {
			toReturn = toReturn && (this.type[i] == region.isType(i));
		}
		
		return toReturn;
	}
}
