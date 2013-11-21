/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
package com.arc.embeddedcdt.gui.jtag;

import com.arc.embeddedcdt.gui.buttons.IFancyCombo;


public interface IScript
{

	String getText();

	void setText(String text);

	/** Notify these that script changed */
	void add(IFancyCombo fancyButton);

	/**
	 * When the GUI is created or the script changed, copy values from script into GUI components 
	 */
	void scriptChangedEvent();

	void changeScript(IFirstExpression fancyButton, String regexp, String text);

}
