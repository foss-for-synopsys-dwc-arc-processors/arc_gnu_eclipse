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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public interface IGDBInit
{

	/** Log errors. Can be invoked from outside the GUI thread. */
	void logError(Exception e);

	/** Run this command asynchronously *if the previous async command completed*.
	 * 
	 *  This avoids lots of async commands piling up.
	 */
	void runAsync(Runnable runnable);
	
	public void setDisplay(Display d);

	Display getDisplay();

	String getDebugger();

	String getExecutable();

	Composite createTab(String string);

	void setStatus(String s);



	/** updateLaunchConfigurationDialog */
	void updateIt();



	IScript getConfigScript();




	void addConfigListener(IConfigListener configScriptTab);

	String executeCommand(String string);

	String executeCommandTcl(String tcl);



	

}
