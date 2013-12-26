/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

package com.arc.embeddedcdt.gui;

import org.eclipse.cdt.debug.core.ICDebugConfiguration;
import org.eclipse.cdt.launch.ui.CDebuggerTab;


public class EmbeddedDebuggerTab extends CDebuggerTab {
	public EmbeddedDebuggerTab(boolean attachMode) {
		super(attachMode);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.launch.internal.ui.AbstractCDebuggerTab#loadDebuggerCombo(org.eclipse.cdt.debug.core.ICDebugConfiguration[], java.lang.String)
	 */
	protected void loadDebuggerCombo(ICDebugConfiguration[] debugConfigs,
			String current) {
		/* Force the only choice */
		super.loadDebuggerCombo(debugConfigs, "com.arc.embeddedcdt.EmbeddedCDebugger");
		super.loadDebuggerCombo(debugConfigs, "com.arc.embeddedcdt.RemoteGDBDebugger");
	}

	public String getDebugger()
	{
		return ((EmbeddedGDBDebuggerPage)getDynamicTab()).getDebugger();
	}
}
