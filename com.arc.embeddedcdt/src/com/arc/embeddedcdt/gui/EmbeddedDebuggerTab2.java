/*******************************************************************************
 * Copyright (c) 2010, 2017 Mentor Graphics Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Anna Dushistova (Mentor Graphics) - initial API and implementation
 * Anna Dushistova (Mentor Graphics) - moved to org.eclipse.cdt.launch.remote.tabs
 * Jonah Graham (Kichwa Coders) - Adapted RemoteCDSFDebuggerTab for use here
 *******************************************************************************/

package com.arc.embeddedcdt.gui;

import org.eclipse.cdt.debug.ui.ICDebuggerPage;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.CDebuggerTab;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

// Derived from org.eclipse.cdt.launch.remote.tabs.RemoteCDSFDebuggerTab
// (it is simpler than the JTAG debugger page - org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagDSFDebuggerTab)
@SuppressWarnings("restriction")
public class EmbeddedDebuggerTab2 extends CDebuggerTab {

    private final static String EMBEDDED_DEBUGGER_ID = "com.arc.embeddedcdt.RemoteGDBDebugger"; //$NON-NLS-1$

    public EmbeddedDebuggerTab2() {
        super(SessionType.LOCAL, false);
    }

    protected void initDebuggerTypes(String selection) {
        setInitializeDefault(true);
        setDebuggerId(EMBEDDED_DEBUGGER_ID);
        updateComboFromSelection();
    }

    protected void loadDynamicDebugArea() {
        Composite dynamicTabHolder = getDynamicTabHolder();
        // Dispose of any current child widgets in the tab holder area
        Control[] children = dynamicTabHolder.getChildren();
        for (int i = 0; i < children.length; i++) {
            children[i].dispose();
        }
        setDynamicTab(new RemoteGdbDebuggerPage());

        ICDebuggerPage debuggerPage = getDynamicTab();
        if (debuggerPage == null) {
            return;
        }
        // Ask the dynamic UI to create its Control
        debuggerPage.setLaunchConfigurationDialog(getLaunchConfigurationDialog());
        debuggerPage.createControl(dynamicTabHolder);
        debuggerPage.getControl().setVisible(true);
        dynamicTabHolder.layout(true);
        contentsChanged();
    }

    @Override
    public String getId() {
        return "com.arc.embeddedcdt.debuggertab.dsf"; //$NON-NLS-1$
    }
}
