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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.IBinaryParser.IBinaryObject;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.ICDebugConfiguration;
import org.eclipse.cdt.debug.core.ICDebugConstants;
import org.eclipse.cdt.debug.ui.ICDebuggerPage;
import org.eclipse.cdt.launch.LaunchUtils;
import org.eclipse.cdt.launch.internal.ui.LaunchMessages;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.launch.ui.CDebuggerTab;
import org.eclipse.cdt.launch.ui.ICDTLaunchHelpContextIds;
import org.eclipse.cdt.launch.ui.CDebuggerTab.AdvancedDebuggerOptionsDialog;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.icu.text.Collator;
import com.ibm.icu.text.MessageFormat;


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
