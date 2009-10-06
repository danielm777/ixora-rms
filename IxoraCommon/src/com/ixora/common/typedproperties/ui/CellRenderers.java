/*
 * Created on 01-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import com.ixora.common.typedproperties.TypedProperties;

/**
 * Holds all renders used by typed properties.
 * @author Daniel Moraru
 */
// TODO: This is the place to try and automate
// the finding and creation of renderers for various types
public final class CellRenderers {
    private CellComponentColor colorCellRenderer;
    private CellComponentDate dateCellRenderer;
    private CellComponentExtended colorCellRendererExtended;
    private CellComponentExtended dateCellRendererExtended;
	private CellComponentInt intCellRenderer;
	private CellComponentObject objectCellRenderer;
	private CellComponentSecureString secureStringCellRenderer;
	private CellComponentExtended objectCellRendererExtended;
	private CellComponentFloat floatCellRenderer;
	private CellComponentPercentage percentCellRenderer;
	private CellComponentBoolean booleanCellRenderer;
	private CellComponentFile fileCellRenderer;
	private CellComponentExtended fileCellRendererExtended;

// the renderers to use for value sets
    private CellComponentValueSet colorCellRendererSet;
    private CellComponentValueSet dateCellRendererSet;
	private CellComponentValueSet intCellRendererSet;
	private CellComponentValueSet objectCellRendererSet;
	private CellComponentValueSet floatCellRendererSet;
	private CellComponentValueSet percentCellRendererSet;
	private CellComponentValueSet booleanCellRendererSet;
	private CellComponentValueSet fileCellRendererSet;

    /**
     * Constructor
     */
    public CellRenderers() {
        super();
    }

    /**
     * @param type
     * @return
     */
    public CellComponent getRenderer(int type) {
		switch(type) {
	    	case TypedProperties.TYPE_INTEGER:
			    return getIntCellRenderer();
			case TypedProperties.TYPE_BOOLEAN:
			    return getBooleanCellRenderer();
			case TypedProperties.TYPE_FLOAT:
			    return getFloatCellRenderer();
			case TypedProperties.TYPE_PERCENTAGE:
			    return getPercentCellRenderer();
			case TypedProperties.TYPE_COLOR:
			    return getColorCellRenderer();
			case TypedProperties.TYPE_DATE:
			    return getDateCellRenderer();
			case TypedProperties.TYPE_FILE:
			    return getFileCellRenderer();
			case TypedProperties.TYPE_SECURE_STRING:
			    return getSecureStringCellRenderer();
    	}
	    return getObjectCellRenderer();
    }

    /**
     * @param type
     * @return
     */
    public CellComponent getRendererExtended(int type) {
		switch(type) {
			case TypedProperties.TYPE_COLOR:
			    return getColorCellRendererExtended();
			case TypedProperties.TYPE_DATE:
			    return getDateCellRendererExtended();
			case TypedProperties.TYPE_FILE:
			    return getFileCellRendererExtended();
			case TypedProperties.TYPE_SERIALIZABLE:
			    return getObjectCellRendererExtended();
    	}
		return getRenderer(type);
    }

    /**
     * @param type
     * @return
     */
    public CellComponentValueSet getRendererValueSet(int type) {
		switch(type) {
	    	case TypedProperties.TYPE_INTEGER:
			    return getIntCellRendererSet();
			case TypedProperties.TYPE_BOOLEAN:
			    return getBooleanCellRendererSet();
			case TypedProperties.TYPE_FLOAT:
			    return getFloatCellRendererSet();
			case TypedProperties.TYPE_PERCENTAGE:
			    return getPercentCellRendererSet();
			case TypedProperties.TYPE_COLOR:
			    return getColorCellRendererSet();
			case TypedProperties.TYPE_DATE:
			    return getDateCellRendererSet();
			case TypedProperties.TYPE_FILE:
			    return getFileCellRendererSet();
    	}
		return getObjectCellRendererSet();
    }

    /**
     * @return the colorCellRenderer.
     */
    private CellComponentColor getColorCellRenderer() {
        if(colorCellRenderer == null) {
            colorCellRenderer = new CellComponentColor();
        }
        return colorCellRenderer;
    }
    /**
     * @return the booleanCellRenderer.
     */
    private CellComponentBoolean getBooleanCellRenderer() {
        if(booleanCellRenderer == null) {
            booleanCellRenderer = new CellComponentBoolean();
        }
        return booleanCellRenderer;
    }
    /**
     * @return the booleanCellRendererSet.
     */
    private CellComponentValueSet getBooleanCellRendererSet() {
        if(booleanCellRendererSet == null) {
            booleanCellRendererSet = new CellComponentValueSet(new CellComponentBoolean());
        }
        return booleanCellRendererSet;
    }
    /**
     * @return the colorCellRendererExtended.
     */
    private CellComponentExtended getColorCellRendererExtended() {
        if(colorCellRendererExtended == null) {
            colorCellRendererExtended = new CellComponentExtended(new CellComponentColor());
        }
        return colorCellRendererExtended;
    }
	/**
	 * @return the colorCellRendererSet.
	 */
	private CellComponentValueSet getColorCellRendererSet() {
	    if(colorCellRendererSet == null) {
	        colorCellRendererSet = new CellComponentValueSet(new CellComponentColor());
	    }
	    return colorCellRendererSet;
	}
    /**
     * @return the dateCellRenderer.
     */
    private CellComponentDate getDateCellRenderer() {
        if(dateCellRenderer == null) {
            dateCellRenderer = new CellComponentDate();
        }
        return dateCellRenderer;
    }
    /**
     * @return the dateCellRendererExtended.
     */
    private CellComponentExtended getDateCellRendererExtended() {
        if(dateCellRendererExtended == null) {
            dateCellRendererExtended = new CellComponentExtended(new CellComponentDate());
        }
        return dateCellRendererExtended;
    }
    /**
     * @return the dateCellRendererSet.
     */
    private CellComponentValueSet getDateCellRendererSet() {
        if(dateCellRendererSet == null) {
            dateCellRendererSet = new CellComponentValueSet(new CellComponentDate());;
        }
        return dateCellRendererSet;
    }
    /**
     * @return the fileCellRenderer.
     */
    private CellComponentFile getFileCellRenderer() {
        if(fileCellRenderer == null) {
            fileCellRenderer = new CellComponentFile();
        }
        return fileCellRenderer;
    }
    /**
     * @return the fileCellRendererExtended.
     */
    private CellComponentExtended getFileCellRendererExtended() {
        if(fileCellRendererExtended == null) {
            fileCellRendererExtended = new CellComponentExtended(new CellComponentFile());
        }
        return fileCellRendererExtended;
    }
    /**
     * @return the fileCellRendererSet.
     */
    private CellComponentValueSet getFileCellRendererSet() {
        if(fileCellRendererSet == null) {
            fileCellRendererSet = new CellComponentValueSet(new CellComponentFile());
        }
        return fileCellRendererSet;
    }
    /**
     * @return the floatCellRenderer.
     */
    private CellComponentFloat getFloatCellRenderer() {
        if(floatCellRenderer == null) {
            floatCellRenderer = new CellComponentFloat();
        }
        return floatCellRenderer;
    }
    /**
     * @return the floatCellRendererSet.
     */
    private CellComponentValueSet getFloatCellRendererSet() {
        if(floatCellRendererSet == null) {
            floatCellRendererSet = new CellComponentValueSet(new CellComponentFloat());
        }
        return floatCellRendererSet;
    }
    /**
     * @return the intCellRenderer.
     */
    private CellComponentInt getIntCellRenderer() {
        if(intCellRenderer == null) {
            intCellRenderer = new CellComponentInt();
        }
        return intCellRenderer;
    }
    /**
     * @return the intCellRendererSet.
     */
    private CellComponentValueSet getIntCellRendererSet() {
        if(intCellRendererSet == null) {
            intCellRendererSet = new CellComponentValueSet(new CellComponentInt());
        }
        return intCellRendererSet;
    }
    /**
     * @return the objectCellRenderer.
     */
    private CellComponentObject getObjectCellRenderer() {
        if(objectCellRenderer == null) {
            objectCellRenderer = new CellComponentObject();
        }
        return objectCellRenderer;
    }
    /**
     * @return the objectCellRendererExtended.
     */
    private CellComponentExtended getObjectCellRendererExtended() {
        if(objectCellRendererExtended == null) {
            objectCellRendererExtended = new CellComponentExtended(new CellComponentObject());
        }
        return objectCellRendererExtended;
    }
    /**
     * @return the objectCellRendererSet.
     */
    private CellComponentValueSet getObjectCellRendererSet() {
        if(objectCellRendererSet == null) {
    		objectCellRendererSet = new CellComponentValueSet(new CellComponentObject());
        }
        return objectCellRendererSet;
    }
    /**
     * @return the secureStringCellRenderer.
     */
    private CellComponentObject getSecureStringCellRenderer() {
        if(secureStringCellRenderer == null) {
        	secureStringCellRenderer = new CellComponentSecureString();
        }
        return secureStringCellRenderer;
    }

    /**
     * @return the percentCellRenderer.
     */
    private CellComponentPercentage getPercentCellRenderer() {
        if(percentCellRenderer == null) {
            percentCellRenderer = new CellComponentPercentage();
        }
        return percentCellRenderer;
    }
    /**
     * @return the percentCellRendererSet.
     */
    private CellComponentValueSet getPercentCellRendererSet() {
        if(percentCellRendererSet == null) {
            percentCellRendererSet = new CellComponentValueSet(new CellComponentPercentage());
        }
        return percentCellRendererSet;
    }
}
