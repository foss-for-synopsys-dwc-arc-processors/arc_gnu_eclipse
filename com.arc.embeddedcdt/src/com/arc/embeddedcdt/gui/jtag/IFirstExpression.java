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

public interface IFirstExpression
{

	/** Creates the first expression */
	String createFirstEntry(String script, String newText);

}
