/*
 * Created on 16-Jan-2005
 */
package com.ixora.rms.ui.dataviewboard.charts.definitions;

import java.util.LinkedList;
import java.util.List;

import com.ixora.common.xml.XMLSameTagList;
import com.ixora.common.xml.XMLTagList;
import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.RealizedQuery;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.ui.dataviewboard.charts.ChartImpl;
import com.ixora.rms.ui.dataviewboard.charts.ChartsBoard;
import com.ixora.rms.ui.dataviewboard.charts.RMSDataset;
import com.ixora.rms.ui.dataviewboard.charts.exception.ChartNoRendererException;

/**
 * ChartDef
 * Contains definition for a chart (data only, no functionality),
 * inheriting traits of a ViewDef (name, description and query)
 * Loads and saves contents into XML.
 */
public class ChartDef extends DataView {
	private static final long serialVersionUID = 7027947123382558490L;
	protected XMLTagList<RendererDef> renderers = new XMLSameTagList<RendererDef>(RendererDef.class);

    /**
     * Constructs an empty object, ready to be loaded from XML
     */
    public ChartDef() {
        super();
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param name chart's name
     * @param description chart's description
     * @param query chart's query definition
     * @param author
     */
    public ChartDef(String name, String description, QueryDef query, String author) {
        super(name, description, query, author);
    }

    /**
     * Constructs an object from given values (as opposed to read it
     * from XML)
     * @param name chart's name
     * @param description chart's description
     * @param query chart's query definition
     * @param listRenderers renderer definitions
     * @param author
     */
    public ChartDef(String name, String description,
    		QueryDef query, List<RendererDef> listRenderers, String author) {
        super(name, description, query, author);
        this.renderers.addAll(listRenderers);
    }

    /**
     * @return a list of definitions for the renderers of this chart
     */
    public List<RendererDef> getRenderers() {
    	return renderers;
    }

    /**
     * @see com.ixora.rms.repository.DataView#getBoardClass()
     */
    public String getBoardClass() {
        return ChartsBoard.class.getName();
    }

    /**
     * Throws the appropriate exception if a problem occurs while trying
     * to initialize a control from the given DataView. Used to test
     * the validity of a custom-defined view.
	 * @see com.ixora.rms.repository.DataView#testDataView(com.ixora.rms.ResourceId)
     */
    public void testDataView(ResourceId context) throws RMSException {
    	super.testDataView(context);

        // Make sure that the cube can be realized (query definition ok)
	    RealizedQuery realizedCube = new RealizedQuery(getQueryDef(), context);

	    // Make sure we have at least one renderer
	    if (getRenderers().size() < 1) {
	        throw new ChartNoRendererException();
	    }

	    // Make sure domain and ranges for all renderers have valid IDs
	    for (RendererDef renderer : getRenderers()) {
	        realizedCube.getQueryResult(renderer.getDomain().getId());
	        for (String rangeID : renderer.getRangesIDList()) {
	            realizedCube.getQueryResult(rangeID);
	        }
	    }

        // Make sure the definition can produce a Plot object
	    ChartImpl chartImpl = new ChartImpl(this, realizedCube, 50);
		List<RMSDataset> datasets = new LinkedList<RMSDataset>();
		chartImpl.createPlot(datasets);
    }
}