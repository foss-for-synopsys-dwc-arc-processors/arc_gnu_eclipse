/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
/*
 * Created on 14.nov.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.arc.embeddedcdt.gui;

import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.arc.embeddedcdt.LaunchConfigurationConstants;


public class MainTab extends CMainTab
{
	private Button checkButton;
	protected Button fBuildBeforeLauch;

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		Composite subComp=(Composite) getControl();
		
	     checkButton = new Button(subComp, SWT.CHECK);
			
			checkButton.addSelectionListener(new SelectionListener()
			{
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}

				public void widgetSelected(SelectionEvent e)
				{
					updateLaunchConfigurationDialog();
				}
				
			});
		    checkButton.setText("Application console");
		    
		    fBuildBeforeLauch = createCheckButton(subComp, "Build Before Launch");
		    fBuildBeforeLauch.addSelectionListener(new SelectionAdapter() {
		        @Override
		        public void widgetSelected(SelectionEvent e) {
		        	updateLaunchConfigurationDialog();
		        }
		        });
		
	}

	/**
	 * Show a dialog that lets the user select a project. This in turn provides
	 * context for the main type, allowing the user to key a main type name, or
	 * constraining the search for main types to the specified project.
	 */
	protected void handleBinaryBrowseButtonSelected() {
		super.handleBinaryBrowseButtonSelected() ;
		
//	    FileDialog dialog=new FileDialog(getShell());
//
//	    final ICProject cproject = getCProject();
//		if (cproject != null) {
//		    dialog.setFilterPath(cproject.getPath().toOSString());
//		}
//
//		String fileName;
//		fileName=dialog.open();
//		if ((fileName != null) && (fileName.length()==0))
//		{
//		    return;
//		}
//
//		// it is the debuggers job to put up an error message. The binary might not
//		// even exist yet at this point.
//		fProgText.setText(fileName);
//
	}
    
	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		setMessage(null);

		return true;
	}

	protected boolean isBinary(IProject project, IPath exePath)
			throws CoreException {
		// It's none of CDT's business whether or not this is a binary. Let GDB produce an
		// error if it isn't.
		return true;
	}

	public String getExecutable()
	{
		return fProgText.getText();
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		super.setDefaults(configuration);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE, LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE_DEFAULT);
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
		try {
			fBuildBeforeLauch.setSelection(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_BUILD_BEFORE_LAUNCH, LaunchConfigurationConstants.ATTR_DEBUGGER_BUILD_BEFORE_LAUNCH_DEFAULT));
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		   
		try {
			checkButton.setSelection(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE, 
					LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE_DEFAULT)); //$NON-NLS-1$
		} catch (CoreException e) {
			setErrorMessage(e.getStatus().getMessage()); //$NON-NLS-1$
			LaunchUIPlugin.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_BUILD_BEFORE_LAUNCH, fBuildBeforeLauch.getSelection());
		configuration.setAttribute(
				LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE,
				(Boolean)checkButton.getSelection());

	}

	
}
