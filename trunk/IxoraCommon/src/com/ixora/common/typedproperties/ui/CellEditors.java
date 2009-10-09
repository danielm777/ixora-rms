/*
 * Created on 02-Oct-2004
 */
package com.ixora.common.typedproperties.ui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.CellEditorListener;

import com.ixora.common.typedproperties.TypedProperties;

/**
 * @author Daniel Moraru
 */
//TODO: This is the place to try and automate
//the finding and creation of editors for various types
public final class CellEditors {
    private PropertyEntryCellEditorExtended<Color> colorCellEditor;
    private PropertyEntryCellEditorExtended<Date> dateCellEditor;
    private PropertyEntryCellEditorExtended<File> fileCellEditor;
    private PropertyEntryCellEditorExtended<Object> objectCellEditor;
	private CellEditorInt intCellEditor;
	private CellEditorString stringCellEditor;
	private CellEditorSecureString secureStringCellEditor;
	private CellEditorFloat floatCellEditor;
	private CellEditorPercentage percentCellEditor;
	private CellEditorBoolean booleanCellEditor;

// the editors to use for value sets
    private CellEditorValueSet<Color> colorCellEditorSet;
    private CellEditorValueSet<Date> dateCellEditorSet;
	private CellEditorValueSet<Integer> intCellEditorSet;
	private CellEditorValueSet<Object> objectCellEditorSet;
	private CellEditorValueSet<Float> floatCellEditorSet;
	private CellEditorValueSet<Float> percentCellEditorSet;
	private CellEditorValueSet<Boolean> booleanCellEditorSet;
	private CellEditorValueSet<File> fileCellEditorSet;

	/** Reference to the owner of the extended editors */
	private Component owner;
	/** Renderer store */
	private CellRenderers renderers;
	/** Listeners */
	private List<CellEditorListener> listeners;

    /**
     * Constructor.
     * @param owner
     * @param renderers
     */
    public CellEditors(Component owner, CellRenderers renderers) {
        super();
        this.owner = owner;
	    this.renderers = renderers;
	    this.listeners = new LinkedList<CellEditorListener>();
    }

    /**
     * @param type
     * @return
     */
    public PropertyEntryCellEditor<?> getEditor(int type) {
		switch(type) {
	    	case TypedProperties.TYPE_INTEGER:
			    return getIntCellEditor();
			case TypedProperties.TYPE_BOOLEAN:
			    return getBooleanCellEditor();
	    	case TypedProperties.TYPE_STRING:
			    return getStringCellEditor();
			case TypedProperties.TYPE_FLOAT:
			    return getFloatCellEditor();
			case TypedProperties.TYPE_PERCENTAGE:
			    return getPercentCellEditor();
			case TypedProperties.TYPE_COLOR:
			    return getColorCellEditor();
			case TypedProperties.TYPE_DATE:
			    return getDateCellEditor();
			case TypedProperties.TYPE_FILE:
			    return getFileCellEditor();
			case TypedProperties.TYPE_SECURE_STRING:
			    return getSecureStringCellEditor();
    	}
		return getObjectCellEditor();
    }

    /**
     * @param type
     * @return
     */
    public CellEditorValueSet<?> getEditorValueSet(int type) {
		switch(type) {
	    	case TypedProperties.TYPE_INTEGER:
			    return getIntCellEditorSet();
			case TypedProperties.TYPE_BOOLEAN:
			    return getBooleanCellEditorSet();
			case TypedProperties.TYPE_FLOAT:
			    return getFloatCellEditorSet();
			case TypedProperties.TYPE_PERCENTAGE:
			    return getPercentCellEditorSet();
			case TypedProperties.TYPE_COLOR:
			    return getColorCellEditorSet();
			case TypedProperties.TYPE_DATE:
			    return getDateCellEditorSet();
			case TypedProperties.TYPE_FILE:
			    return getFileCellEditorSet();
    	}
		return getObjectCellEditorSet();
    }

	/**
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void addCellEditorListener(CellEditorListener l) {
	    if(!this.listeners.contains(l)) {
	        this.listeners.add(l);
		}
	    if(booleanCellEditor != null) {
	        booleanCellEditor.addCellEditorListener(l);
	    }
	    if(booleanCellEditorSet != null) {
	        booleanCellEditorSet.addCellEditorListener(l);
	    }
	    if(colorCellEditor != null) {
	        colorCellEditor.addCellEditorListener(l);
	    }
	    if(colorCellEditorSet != null) {
	        colorCellEditorSet.addCellEditorListener(l);
	    }
	    if(dateCellEditor != null) {
	        dateCellEditor.addCellEditorListener(l);
	    }
	    if(dateCellEditorSet != null) {
	        dateCellEditorSet.addCellEditorListener(l);
	    }
	    if(fileCellEditor != null) {
	        fileCellEditor.addCellEditorListener(l);
	    }
	    if(fileCellEditorSet != null) {
	        fileCellEditorSet.addCellEditorListener(l);
	    }
	    if(floatCellEditor != null) {
	        floatCellEditor.addCellEditorListener(l);
	    }
	    if(floatCellEditorSet != null) {
	        floatCellEditorSet.addCellEditorListener(l);
	    }
	    if(intCellEditor != null) {
	        intCellEditor.addCellEditorListener(l);
	    }
	    if(intCellEditorSet != null) {
	        intCellEditorSet.addCellEditorListener(l);
	    }
	    if(percentCellEditor != null) {
	        percentCellEditor.addCellEditorListener(l);
	    }
	    if(percentCellEditorSet != null) {
	        percentCellEditorSet.addCellEditorListener(l);
	    }
	    if(objectCellEditor != null) {
	        objectCellEditor.addCellEditorListener(l);
	    }
	    if(objectCellEditorSet != null) {
	        objectCellEditorSet.addCellEditorListener(l);
	    }
	    if(stringCellEditor != null) {
	        stringCellEditor.addCellEditorListener(l);
	    }
	    if(secureStringCellEditor != null) {
	        secureStringCellEditor.addCellEditorListener(l);
	    }
	}

	/**
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void removeCellEditorListener(CellEditorListener l) {
	    this.listeners.remove(l);
	    if(booleanCellEditor != null) {
	        booleanCellEditor.removeCellEditorListener(l);
	    }
	    if(booleanCellEditorSet != null) {
	        booleanCellEditorSet.removeCellEditorListener(l);
	    }
	    if(colorCellEditor != null) {
	        colorCellEditor.removeCellEditorListener(l);
	    }
	    if(colorCellEditorSet != null) {
	        colorCellEditorSet.removeCellEditorListener(l);
	    }
	    if(dateCellEditor != null) {
	        dateCellEditor.removeCellEditorListener(l);
	    }
	    if(dateCellEditorSet != null) {
	        dateCellEditorSet.removeCellEditorListener(l);
	    }
	    if(fileCellEditor != null) {
	        fileCellEditor.removeCellEditorListener(l);
	    }
	    if(fileCellEditorSet != null) {
	        fileCellEditorSet.removeCellEditorListener(l);
	    }
	    if(floatCellEditor != null) {
	        floatCellEditor.removeCellEditorListener(l);
	    }
	    if(floatCellEditorSet != null) {
	        floatCellEditorSet.removeCellEditorListener(l);
	    }
	    if(intCellEditor != null) {
	        intCellEditor.removeCellEditorListener(l);
	    }
	    if(intCellEditorSet != null) {
	        intCellEditorSet.removeCellEditorListener(l);
	    }
	    if(percentCellEditor != null) {
	        percentCellEditor.removeCellEditorListener(l);
	    }
	    if(percentCellEditorSet != null) {
	        percentCellEditorSet.removeCellEditorListener(l);
	    }
	    if(objectCellEditor != null) {
	        objectCellEditor.removeCellEditorListener(l);
	    }
	    if(objectCellEditorSet != null) {
	        objectCellEditorSet.removeCellEditorListener(l);
	    }
	    if(stringCellEditor != null) {
	        stringCellEditor.removeCellEditorListener(l);
	    }
	    if(secureStringCellEditor != null) {
	        secureStringCellEditor.removeCellEditorListener(l);
	    }
	}

    /**
     * @return the booleanCellEditor.
     */
    private CellEditorBoolean getBooleanCellEditor() {
        if(booleanCellEditor == null) {
            booleanCellEditor = new CellEditorBoolean();
            prepareCellEditor(booleanCellEditor);
        }
        return booleanCellEditor;
    }
    /**
     * @return the booleanCellEditorSet.
     */
    @SuppressWarnings("unchecked")
	private CellEditorValueSet<Boolean> getBooleanCellEditorSet() {
        if(booleanCellEditorSet == null) {
    		booleanCellEditorSet = new CellEditorValueSet<Boolean>(owner,
    		        (CellComponentValueSet<Boolean>)renderers.getRendererValueSet(TypedProperties.TYPE_BOOLEAN));
    		prepareCellEditor(booleanCellEditorSet);
        }
        return booleanCellEditorSet;
    }
    /**
     * @return the colorCellEditor.
     */
    @SuppressWarnings("unchecked")
	private PropertyEntryCellEditorExtended<Color> getColorCellEditor() {
        if(colorCellEditor == null) {
            colorCellEditor = new PropertyEntryCellEditorExtended<Color>(owner,
		        (CellComponentExtended<Color>)renderers.getRendererExtended(TypedProperties.TYPE_COLOR));
            prepareCellEditor(colorCellEditor);
        }
        return colorCellEditor;
    }
	/**
	 * @return the colorCellEditorSet.
	 */
    @SuppressWarnings("unchecked")
	private CellEditorValueSet<Color> getColorCellEditorSet() {
	    if(colorCellEditorSet == null) {
			colorCellEditorSet = new CellEditorValueSet<Color>(owner,
			        (CellComponentValueSet<Color>)renderers.getRendererValueSet(TypedProperties.TYPE_COLOR));
			prepareCellEditor(colorCellEditorSet);
	    }
	    return colorCellEditorSet;
	}
    /**
     * @return the dateCellEditor.
     */
    @SuppressWarnings("unchecked")
	private PropertyEntryCellEditorExtended<Date> getDateCellEditor() {
        if(dateCellEditor == null) {
            dateCellEditor = new PropertyEntryCellEditorExtended<Date>(owner,
		        (CellComponentExtended<Date>)renderers.getRendererExtended(TypedProperties.TYPE_DATE));
            prepareCellEditor(dateCellEditor);
        }
        return dateCellEditor;
    }
    /**
     * @return the dateCellEditorSet.
     */
    @SuppressWarnings("unchecked")
	private CellEditorValueSet<Date> getDateCellEditorSet() {
        if(dateCellEditorSet == null) {
    		dateCellEditorSet = new CellEditorValueSet<Date>(owner,
    		        (CellComponentValueSet<Date>)renderers.getRendererValueSet(TypedProperties.TYPE_DATE));
    		prepareCellEditor(dateCellEditorSet);
        }
        return dateCellEditorSet;
    }
    /**
     * @return the fileCellEditor.
     */
    @SuppressWarnings("unchecked")
	private PropertyEntryCellEditorExtended<File> getFileCellEditor() {
        if(fileCellEditor == null) {
    		fileCellEditor = new PropertyEntryCellEditorExtended(owner,
    		        (CellComponentExtended<File>)renderers.getRendererExtended(TypedProperties.TYPE_FILE));
    		prepareCellEditor(fileCellEditor);
        }
        return fileCellEditor;
    }
    /**
     * @return the fileCellEditorSet.
     */
    @SuppressWarnings("unchecked")
	private CellEditorValueSet<File> getFileCellEditorSet() {
        if(fileCellEditorSet == null) {
    		fileCellEditorSet = new CellEditorValueSet<File>(owner,
    		        (CellComponentValueSet<File>)renderers.getRendererValueSet(TypedProperties.TYPE_FILE));
    		prepareCellEditor(fileCellEditorSet);
        }
        return fileCellEditorSet;
    }
    /**
     * @return the floatCellEditor.
     */
    private CellEditorFloat getFloatCellEditor() {
        if(floatCellEditor == null) {
    	    floatCellEditor = new CellEditorFloat(
    	            (CellComponentFloat)renderers.getRenderer(TypedProperties.TYPE_FLOAT));
    	    prepareCellEditor(floatCellEditor);
        }
        return floatCellEditor;
    }
    /**
     * @return the floatCellEditorSet.
     */
    @SuppressWarnings("unchecked")
	private CellEditorValueSet<Float> getFloatCellEditorSet() {
        if(floatCellEditorSet == null) {
    		floatCellEditorSet = new CellEditorValueSet<Float>(owner,
    		        (CellComponentValueSet<Float>)renderers.getRendererValueSet(TypedProperties.TYPE_FLOAT));
    		prepareCellEditor(floatCellEditorSet);
        }
        return floatCellEditorSet;
    }
    /**
     * @return the intCellEditor.
     */
    private CellEditorInt getIntCellEditor() {
        if(intCellEditor == null) {
    	    intCellEditor = new CellEditorInt(
    	            (CellComponentInt)renderers.getRenderer(TypedProperties.TYPE_INTEGER));
    	    prepareCellEditor(intCellEditor);
        }
        return intCellEditor;
    }
    /**
     * @return the intCellEditorSet.
     */
    @SuppressWarnings("unchecked")
	private CellEditorValueSet<Integer> getIntCellEditorSet() {
        if(intCellEditorSet == null) {
    		intCellEditorSet = new CellEditorValueSet<Integer>(owner,
    		        (CellComponentValueSet<Integer>)renderers.getRendererValueSet(TypedProperties.TYPE_INTEGER));
    		prepareCellEditor(intCellEditorSet);
        }
        return intCellEditorSet;
    }
    /**
     * @return the objectCellEditor.
     */
    @SuppressWarnings("unchecked")
	private PropertyEntryCellEditorExtended<Object> getObjectCellEditor() {
        if(objectCellEditor == null) {
    		objectCellEditor = new PropertyEntryCellEditorExtended<Object>(owner,
    		        (CellComponentExtended<Object>)renderers.getRendererExtended(TypedProperties.TYPE_SERIALIZABLE));
    		prepareCellEditor(objectCellEditor);
        }
        return objectCellEditor;
    }
    /**
     * @return the objectCellEditorSet.
     */
    @SuppressWarnings("unchecked")
	private CellEditorValueSet<Object> getObjectCellEditorSet() {
        if(objectCellEditorSet == null) {
    		objectCellEditorSet = new CellEditorValueSet<Object>(owner,
    		        (CellComponentValueSet<Object>)renderers.getRendererValueSet(TypedProperties.TYPE_STRING));
    		prepareCellEditor(objectCellEditorSet);
        }
        return objectCellEditorSet;
    }
    /**
     * @return the percentCellEditor.
     */
    private CellEditorPercentage getPercentCellEditor() {
        if(percentCellEditor == null) {
    	    percentCellEditor = new CellEditorPercentage(
    	            (CellComponentPercentage)renderers.getRenderer(TypedProperties.TYPE_PERCENTAGE));
    	    prepareCellEditor(percentCellEditor);
        }
        return percentCellEditor;
    }
    /**
     * @return the percentCellEditorSet.
     */
    @SuppressWarnings("unchecked")
	private CellEditorValueSet<Float> getPercentCellEditorSet() {
        if(percentCellEditorSet == null) {
    		percentCellEditorSet = new CellEditorValueSet<Float>(owner,
    		        (CellComponentValueSet<Float>)renderers.getRendererValueSet(TypedProperties.TYPE_PERCENTAGE));
    		prepareCellEditor(percentCellEditorSet);
        }
        return percentCellEditorSet;
    }
    /**
     * @return the stringCellEditor.
     */
    private CellEditorString getStringCellEditor() {
        if(stringCellEditor == null) {
            stringCellEditor = new CellEditorString();
            prepareCellEditor(stringCellEditor);
        }
        return stringCellEditor;
    }
    /**
     * @return the secureStringCellEditor.
     */
    private CellEditorSecureString getSecureStringCellEditor() {
        if(secureStringCellEditor == null) {
        	secureStringCellEditor = new CellEditorSecureString();
            prepareCellEditor(secureStringCellEditor);
        }
        return secureStringCellEditor;
    }

    /**
     * Prepares a newly created editor.
     * @param e
     */
    private void prepareCellEditor(PropertyEntryCellEditor<?> e) {
        for(Iterator<?> iter = listeners.iterator(); iter.hasNext();) {
            e.addCellEditorListener((CellEditorListener)iter.next());
        }
    }
}
