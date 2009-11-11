/**
 * 05-Mar-2006
 */
package com.ixora.common.typedproperties.ui;

import javax.swing.JTextField;

import com.ixora.common.ui.UIFactoryMgr;

/**
 * @author Daniel Moraru
 */
class CellComponentSecureString extends CellComponentObject {
	private static final long serialVersionUID = 6869509599063478520L;

	/**
	 *
	 */
	public CellComponentSecureString() {
		super();
	}

	/**
	 * @see com.ixora.common.typedproperties.ui.CellComponentObject#createTextField()
	 */
	protected JTextField createTextField() {
		return UIFactoryMgr.createPasswordField();
	}
}
