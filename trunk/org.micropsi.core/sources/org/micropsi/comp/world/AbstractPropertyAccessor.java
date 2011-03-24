/*
 * Created on 04.08.2004
 *
 */

package org.micropsi.comp.world;

/**
 * @author Matthias
 *
 */
public abstract class AbstractPropertyAccessor {
	private boolean redundantProperty = false;  // if true, property will not be saved, ...
	private boolean editable = true;

	
	private String key;
	private int valueType = ObjectProperty.VTYPE_STRING;

	/**
	 * @param key
	 * @param valueType
	 * @param editable
	 * @param redundantProperty
	 */
	public AbstractPropertyAccessor(String key, int valueType,
			boolean editable, boolean redundantProperty) {
		this.key = key;
		this.valueType = valueType;
		this.editable = editable;
		this.redundantProperty = redundantProperty;
	}

	/**
	 * @param key
	 * @param valueType
	 * @param editable
	 */
	public AbstractPropertyAccessor(String key, int valueType,
			boolean editable) {
		this(key, valueType, editable, false);
	}

	/**
	 * @param key
	 * @param valueType
	 */
	public AbstractPropertyAccessor(String key, int valueType) {
		this(key, valueType, true, false);
	}
	
	public boolean setProperty(ObjectProperty prop) {
		try {
			return _setProperty(prop);
		} catch (NumberFormatException e) {
			prop.setComment("invalid data format");
			return false;
		}
	}
	
	public ObjectProperty getProperty() {
		return new ObjectProperty(key, getValue(), valueType, editable, redundantProperty);
	}
	
	protected abstract boolean _setProperty(ObjectProperty prop);
	
	protected abstract String getValue();
	
	protected double getDoubleValue() {
		return Double.parseDouble(getValue());
	}

	protected double getIntValue() {
		return Integer.parseInt(getValue());
	}
	
	public boolean matchesKey(String key) {
		return (key != null && this.key.equals(key));
	}

	/**
	 * @return Returns the key.
	 */
	public String getKey() {
		return key;
	}
}
