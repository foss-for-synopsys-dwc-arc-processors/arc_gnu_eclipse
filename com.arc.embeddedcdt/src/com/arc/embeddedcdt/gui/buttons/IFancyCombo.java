/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
package com.arc.embeddedcdt.gui.buttons;

import com.arc.embeddedcdt.gui.jtag.IScript;

public interface IFancyCombo
{


	/** Notification that the script changed 
	 * @param source The script that changed  
	 **/
	public abstract void scriptChangedEvent(IScript source);

}