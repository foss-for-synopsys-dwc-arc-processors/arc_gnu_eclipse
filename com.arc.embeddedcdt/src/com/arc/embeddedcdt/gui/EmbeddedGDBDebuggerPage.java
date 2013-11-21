/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/
package com.arc.embeddedcdt.gui;

import java.io.File;

import org.eclipse.cdt.debug.mi.core.IMILaunchConfigurationConstants;
import org.eclipse.cdt.debug.mi.internal.ui.MIUIMessages;
import org.eclipse.cdt.debug.mi.internal.ui.StandardGDBDebuggerPage;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;

import com.arc.embeddedcdt.LaunchPlugin;
import com.arc.embeddedcdt.preferences.PrefConstants;

/**
 * The dynamic tab for gdb-based debugger implementations.
 */
public class EmbeddedGDBDebuggerPage extends StandardGDBDebuggerPage {
	private boolean fIsInitializing = false;
	/* (non-Javadoc)
     * @see org.eclipse.cdt.debug.mi.internal.ui.GDBDebuggerPage#createMainTab(org.eclipse.swt.widgets.TabFolder)
     */
    public void createMainTabX(Composite tabFolder)
    {
//        TabItem tabItem = new TabItem( tabFolder, SWT.NONE );
//        tabItem.setText( MIUIMessages.getString( "GDBDebuggerPage.2" ) ); //$NON-NLS-1$
        Composite comp = ControlFactory.createCompositeEx( tabFolder, 1, GridData.FILL_BOTH );
        ((GridLayout)comp.getLayout()).makeColumnsEqualWidth = false;
//        tabItem.setControl( comp );
        Composite subComp = ControlFactory.createCompositeEx( comp, 3, GridData.FILL_HORIZONTAL );
        ((GridLayout)subComp.getLayout()).makeColumnsEqualWidth = false;
        Label label = ControlFactory.createLabel( subComp, MIUIMessages.getString( "GDBDebuggerPage.3" ) ); //$NON-NLS-1$
        GridData gd = new GridData();
        //		gd.horizontalSpan = 2;
        label.setLayoutData( gd );
        fGDBCommandText = ControlFactory.createTextField( subComp, SWT.SINGLE | SWT.BORDER );
        fGDBCommandText.addModifyListener( new ModifyListener() {
        
        	public void modifyText( ModifyEvent evt ) {
        		if ( !isInitializing() )
        			updateLaunchConfigurationDialog();
        	}
        } );
        Button button = createPushButton( subComp, MIUIMessages.getString( "GDBDebuggerPage.4" ), null ); //$NON-NLS-1$
        button.addSelectionListener( new SelectionAdapter() {
        
        	public void widgetSelected( SelectionEvent evt ) {
        		handleGDBButtonSelected();
        		updateLaunchConfigurationDialog();
        	}
        
        	private void handleGDBButtonSelected() {
        		FileDialog dialog = new FileDialog( getShell(), SWT.NONE );
        		dialog.setText( MIUIMessages.getString( "GDBDebuggerPage.5" ) ); //$NON-NLS-1$
        		String gdbCommand = fGDBCommandText.getText().trim();
        		int lastSeparatorIndex = gdbCommand.lastIndexOf( File.separator );
        		if ( lastSeparatorIndex != -1 ) {
        			dialog.setFilterPath( gdbCommand.substring( 0, lastSeparatorIndex ) );
        		}
        		String res = dialog.open();
        		if ( res == null ) {
        			return;
        		}
        		fGDBCommandText.setText( res );
        	}
        } );
    }
    public void createSolibTab(TabFolder tabFolder)
    {
    }
    
	protected boolean isInitializing() {
		return fIsInitializing;
	}

	private void setInitializing( boolean isInitializing ) {
		fIsInitializing = isInitializing;
	}    
    
    /* (non-Javadoc)
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
     */
	public void initializeFrom(ILaunchConfiguration configuration) {
		setInitializing(true);
		super.initializeFrom(configuration) ;
		String gdbCommand = getValue(PrefConstants.P_DEBUGGER_NAME); 
		String gdbInit = getValue(PrefConstants.P_DEBUGGER_INIT);
		try {
			// if there is a saved attribute, use that, else use the preference
			gdbCommand = configuration.getAttribute(
					IMILaunchConfigurationConstants.ATTR_DEBUG_NAME, 
					gdbCommand); 
			gdbInit = configuration.getAttribute( 
					IMILaunchConfigurationConstants.ATTR_GDB_INIT, 
					gdbInit );

		} catch (CoreException e) {
		}
		fGDBCommandText.setText(gdbCommand);
		fGDBInitText.setText(gdbInit) ;
		setInitializing(false);
	}

	protected String getValue(String KEY) {
		return LaunchPlugin.getDefault().getPluginPreferences().getString(KEY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration) ;
		String gdbStr = fGDBCommandText.getText();
		gdbStr.trim();
		configuration.setAttribute(IMILaunchConfigurationConstants.ATTR_DEBUG_NAME, gdbStr);
		
		String gdbInit = fGDBInitText.getText();
		gdbInit.trim();
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_GDB_INIT, gdbInit);
		
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		String gdbCommand = getValue(PrefConstants.P_DEBUGGER_NAME); //$NON-NLS-1$ // ****
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUG_NAME, gdbCommand); //$NON-NLS-1$
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_GDB_INIT, ".gdbinit");
	}
	public String getDebugger()
	{
		return fGDBCommandText.getText();
	}

}