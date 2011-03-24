/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 28.07.2003
 *
 */
package org.micropsi.comp.world;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;
import org.micropsi.common.coordinates.Area2D;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.world.objects.AbstractAgentObject;
import org.micropsi.comp.world.objects.GroundType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Matthias
 *
 */
public class GroundMap {
	
	private ArrayList<GroundType> groundTypeList = null;
	
	private GroundType[][] groundTypeMap = null;
	private GroundType defaultGroundType = null;
	private int defaultGroundTypeIndex = 0;
	
	private int typeMapSizeX, typeMapSizeY;
	private double typeMapScaleX = 1, typeMapScaleY = 1;
	private Position typeMapOffset = new Position(0, 0, 0);

	private String imageFileName = null;

	/**
	 * @param groundmapImageDir
	 * 
	 */
	public GroundMap(Element configData, String groundmapImageDir, Logger logger) throws MicropsiException {
		try {
			imageFileName = WorldComponent.getFileNameWithoutPath(XMLElementHelper.getElementValueByTagName(configData, "typesimagefile"));
			NodeList nl = XMLElementHelper.getElementByTagName(configData, "groundtypes").getElementsByTagName("groundtype");
			groundTypeList = new ArrayList<GroundType>(16);
			for (int i = 0; i < nl.getLength(); i++) {
				GroundType gt = new GroundType((Element) nl.item(i), logger);
				while (groundTypeList.size() <= gt.getIndex()) {
					groundTypeList.add(null);
				}
				groundTypeList.set(gt.getIndex(), gt);
			}
		} catch (XMLElementNotFoundException e) {
			throw new MicropsiException(10, "Initializing GroundMap: required tag missing.", e);
		}
		SWTReadGroundTypeMap(groundmapImageDir);
		
		// optional parameters:
		try {
			Element areaElement = XMLElementHelper.getElementByTagName(configData, "groundmaparea");
			Area2D area = new Area2D(areaElement, typeMapOffset, null);
			typeMapOffset = area.getLowestCoords();
			if (area.getHighestCoords() != null) {
				Position pos = area.getHighestCoords();
				pos.subtract(typeMapOffset);
				typeMapScaleX = pos.getX() / typeMapSizeX;
				typeMapScaleY = pos.getY() / typeMapSizeY;
			}
		} catch (XMLElementNotFoundException e1) {
			// ignore: Parameters optional
		}
		try {
			defaultGroundTypeIndex = Integer.parseInt(XMLElementHelper.getElementValueByTagName(configData, "defaultgroundtype"));
		} catch (NumberFormatException e1) {
			logger.warn("Initializing GroundMap: Number format error in config file (defaultgroundtype).", e1);
		} catch (XMLElementNotFoundException e1) {
			// ignore: Parameters optional
		}
		defaultGroundType = groundTypeList.get(defaultGroundTypeIndex);
		
	}
	
	private void SWTReadGroundTypeMap(String groundMapImageDir) throws MicropsiException {
		ImageData groundTypesImage;
		String fileName = WorldComponent.getFullFileName(groundMapImageDir, imageFileName);
		try {
			groundTypesImage = new ImageData(fileName);
		} catch (SWTException e) {
			throw new MicropsiException(10, "Error reading groundtypes image file '" + fileName + "'.", e);
		}
		typeMapSizeX = groundTypesImage.width;
		typeMapSizeY = groundTypesImage.height;
		groundTypeMap = new GroundType[typeMapSizeX][typeMapSizeY];
		for(int i = 0; i < typeMapSizeX; i++) {
			for(int j = 0; j < typeMapSizeY; j++) {
				int gtindex = groundTypesImage.getPixel(i, j);
				GroundType gt = groundTypeList.get(gtindex);
				if (gt == null) {
					throw new MicropsiException(10, "Error initializing GroundMap: no groundtype for index " + gtindex + ".");
				}
				groundTypeMap[i][typeMapSizeY - j - 1] = gt;
			}
		}
	}

/*	private void readGroundTypeMap(String groundMapImageDir) throws MicropsiException {
		BufferedImage groundTypesImage;
		Raster typesRaster;
		String fileName = WorldComponent.getFullFileName(groundMapImageDir, imageFileName);
		try {
			groundTypesImage = ImageIO.read(new File(fileName));
			typesRaster = groundTypesImage.getData();
		} catch (IOException e2) {
			throw new MicropsiException(10, "Error reading groundtypes image file '" + fileName + "'.", e2);
		}
		typeMapSizeX = typesRaster.getWidth();
		typeMapSizeY = typesRaster.getHeight();
		groundTypeMap = new GroundType[typeMapSizeX][typeMapSizeY];
		for(int i = 0; i < typeMapSizeX; i++) {
			for(int j = 0; j < typeMapSizeY; j++) {
				int gtindex = typesRaster.getSample(i, j, 0);
				GroundType gt = (GroundType) groundTypeList.get(gtindex);
				if (gt == null) {
					throw new MicropsiException(10, "Error initializing GroundMap: no groundtype for index " + gtindex + ".");
				}
				groundTypeMap[i][typeMapSizeY - j - 1] = gt;
			}
		}
	}
*/
	public Element toXMLElement(Document doc) {
		Element element = doc.createElement("groundmap");
		Element currentElement;
		
		currentElement = doc.createElement("typesimagefile");
		currentElement.appendChild(doc.createTextNode(imageFileName));
		element.appendChild(currentElement);
		
		currentElement = doc.createElement("groundtypes");
		for (int i = 0; i < groundTypeList.size(); i++) {
			if (groundTypeList.get(i) != null) {
				currentElement.appendChild(groundTypeList.get(i).toXMLElement(doc));
			}
		}
		element.appendChild(currentElement);
		
		currentElement = new Area2D(getLowestCoords(), getHighestCoords()).toXMLElement(doc, "groundmaparea");
		element.appendChild(currentElement);

		currentElement = doc.createElement("defaultgroundtype");
		currentElement.appendChild(doc.createTextNode(Integer.toString(defaultGroundTypeIndex)));
		element.appendChild(currentElement);
		
		return element;
	}
	
	public GroundType getGroundType(Position pos) {
		int x = (int) Math.round(Math.floor((pos.getX() - typeMapOffset.getX()) / typeMapScaleX));
		int y = (int) Math.round(Math.floor((pos.getY() - typeMapOffset.getY()) / typeMapScaleY));
		try {
			return groundTypeMap[x][y];
		} catch (IndexOutOfBoundsException e) {
			return defaultGroundType;
		}
	}
	
	public WorldVector getEffectiveMoveVector(AbstractAgentObject agent, WorldVector vec) {
		if (vec.getX() == 0 && vec.getY() == 0) {
			return vec;
		}
		double stepLengthFactor = Double.MAX_VALUE;
		if (vec.getX() != 0) {
			stepLengthFactor = Math.abs(typeMapScaleX / vec.getX());
		} 
		if (vec.getY() != 0 && Math.abs(typeMapScaleY / vec.getY()) < stepLengthFactor) {
			stepLengthFactor = Math.abs(typeMapScaleY / vec.getY());
		}
		WorldVector result = new WorldVector();
		WorldVector left = new WorldVector(vec);
		WorldVector step = new WorldVector(vec);
		step.scaleBy(stepLengthFactor);
		
		Position currentPos = new Position(agent.getPosition());
		WorldVector stepEffort = new WorldVector();
		boolean forbiddenArea = false;
		boolean finished;
		do {
			finished = true;
			stepEffort.set(step);
			stepEffort.scaleBy(1/getGroundType(currentPos).getMoveEfficiency());
			if (stepEffort.getLength() < left.getLength()) {
				currentPos.add(step);
				if (getGroundType(currentPos).isAgentAllowed()) {
					result.add(step);
					left.subtract(stepEffort);
					finished = false;
				} else {
					forbiddenArea = true;
				}
			}
		} while (!finished);
		if (!forbiddenArea) {
			left.scaleBy(getGroundType(currentPos).getMoveEfficiency());
			currentPos.add(left);
			if (getGroundType(currentPos).isAgentAllowed()) {
				result.add(left);
			}
		}
		return result;
	}
	
	public Position getLowestCoords() {
		return typeMapOffset;
	}
	
	public Position getHighestCoords() {
		return new Position(typeMapOffset.getX() + typeMapSizeX*typeMapScaleX, typeMapOffset.getY() + typeMapSizeY*typeMapScaleY, 0);
	}

	/**
	 * @return Returns the typeMapScaleX.
	 */
	public double getScaleX() {
		return typeMapScaleX;
	}
	/**
	 * @return Returns the typeMapScaleY.
	 */
	public double getScaleY() {
		return typeMapScaleY;
	}
	/**
	 * @return Returns the imageFileName.
	 */
	public String getImageFileName() {
		return imageFileName;
	}
}
