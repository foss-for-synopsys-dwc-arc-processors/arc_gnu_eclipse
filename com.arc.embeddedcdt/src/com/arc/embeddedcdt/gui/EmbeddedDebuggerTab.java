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

import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.core.ICDebugConfiguration;
import org.eclipse.cdt.launch.ui.CDebuggerTab;
import org.eclipse.debug.core.ILaunchConfiguration;

public class EmbeddedDebuggerTab extends CDebuggerTab {

    private static final String debuggerId = "com.arc.embeddedcdt.RemoteGDBDebugger";

    public EmbeddedDebuggerTab(boolean attachMode) {
        super(attachMode);
    }

    @SuppressWarnings("restriction")
    @Override
    protected void loadDebuggerComboBox(ILaunchConfiguration config, String selection) {
        CDebugCorePlugin.getDefault().saveDefaultDebugConfiguration(debuggerId);
        ICDebugConfiguration dc = CDebugCorePlugin.getDefault().getDefaultDebugConfiguration();
        loadDebuggerCombo(new ICDebugConfiguration[] { dc }, dc.getID());
    }
}
