/*
 * Created on Feb 23, 2004
 */
package com.ixora.rms;

import com.ixora.common.Product;
import com.ixora.common.update.Module;

/**
 * RMS module.
 * @author Daniel Moraru
 */
public final class RMSModule extends Module {

	/**
	 */
	public RMSModule() {
		super("RMS", Product.getProductInfo().getVersion());
	}
}
