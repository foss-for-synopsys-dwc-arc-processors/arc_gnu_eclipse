/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/

package com.arc.embeddedcdt.gui.jtag;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.LaunchImages;

public class CommandTab implements IConfigListener 
{

	protected Label fPrgmArgumentsLabelInit;
	protected Text fPrgmArgumentsTextInit;

	protected Label fPrgmArgumentsLabelRun;
	protected Text fPrgmArgumentsTextRun;
	private IGDBInit gdbinit;
	private Composite parent;


	public CommandTab(IGDBInit gdbinit)
	{
		this.gdbinit=gdbinit;
	}


	public void createTab()
	{
		parent=gdbinit.createTab("GDB Commands");
		
		createControl(parent);
	}


	public void createControl(Composite parent) {
//		setControl(comp);
		
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_ARGUMNETS_TAB);
		

		//		createVerticalSpacer(comp, 1);
		createCommandsComponent(parent, 1);
	}


	protected void createCommandsComponent(Composite argsComp, int i) {
		GridData gd;

		fPrgmArgumentsLabelInit = new Label(argsComp, SWT.NONE);
		fPrgmArgumentsLabelInit.setText("'Initialize' commands"); //$NON-NLS-1$
		fPrgmArgumentsTextInit = new Text(argsComp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan=2;
		gd.heightHint=100;
		fPrgmArgumentsTextInit.setLayoutData(gd);
		fPrgmArgumentsTextInit.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				gdbScript.scriptChangedEvent();
				gdbinit.updateIt();
			}
		}
			);
		
		fPrgmArgumentsLabelRun = new Label(argsComp, SWT.NONE);
		fPrgmArgumentsLabelRun.setText("'Run' commands"); //$NON-NLS-1$
		fPrgmArgumentsTextRun = new Text(argsComp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		gd.heightHint=100;
		gd.horizontalSpan=2;
		fPrgmArgumentsTextRun.setLayoutData(gd);
		fPrgmArgumentsTextRun.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				gdbinit.updateIt();
			}
		});

//		addControlAccessibleListener(fArgumentVariablesButton, fArgumentVariablesButton.getText()); // need to strip the mnemonic from buttons
	}


	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, (String) null);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN, (String) null);

	}

	public void initializeFrom(ILaunchConfiguration configuration) throws CoreException {
			fPrgmArgumentsTextInit.setText(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, "")); //$NON-NLS-1$
			fPrgmArgumentsTextRun.setText(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN, "")); //$NON-NLS-1$
			getGdbScript().scriptChangedEvent();

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT,
				getAttributeValueFrom(fPrgmArgumentsTextInit));
		configuration.setAttribute(
				LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN,
				getAttributeValueFrom(fPrgmArgumentsTextRun));

	}

	public Image getImage() {
		return LaunchImages.get(LaunchImages.IMG_VIEW_COMMANDS_TAB);
	}

	/**
	 * Retuns the string in the text widget, or <code>null</code> if empty.
	 * 
	 * @return text or <code>null</code>
	 */
	protected String getAttributeValueFrom(Text text) {
		String content = text.getText().trim();
		if (content.length() > 0) {
			return content;
		}
		return null;
	}
	
	IScript gdbScript=new ScriptModifierBase()
	{
		public String getText() 
		{
			return fPrgmArgumentsTextInit.getText();
		}

		public void setText(String t) 
		{
			fPrgmArgumentsTextInit.setText(t);
		}
	};


	public IScript getGdbScript()
	{
		return gdbScript;
	}


}
