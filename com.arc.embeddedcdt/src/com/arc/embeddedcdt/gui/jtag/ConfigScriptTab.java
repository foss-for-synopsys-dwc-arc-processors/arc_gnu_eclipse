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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.arc.embeddedcdt.gui.buttons.IClick;

public class ConfigScriptTab implements IConfigListener
{
	private  String ATTR_DEBUGGER_CONFIG ;

	private  String LONG_DESC;

	private String TABLABEL;

	private String OPENOCD_CFG_FILE;

	protected Text openOCDInitText;
	protected Label fPrgmArgumentsLabelInit;

	private IGDBInit gdbinit;

	public ConfigScriptTab(IGDBInit gdbinit, String configFile, String label, String longDesc, String saveKey)
	{
		this.gdbinit=gdbinit;
		gdbinit.addConfigListener(this);
		OPENOCD_CFG_FILE=configFile;
		TABLABEL=label;
		LONG_DESC=longDesc;
		ATTR_DEBUGGER_CONFIG=saveKey;
	}

	IScript configScript=new ScriptModifierBase()
	{
		public String getText() {
			return openOCDInitText.getText();
		}

		public void setText(String t) {
			openOCDInitText.setText(t);
		}

		
	};
	

	protected void write(Shell shell)
	{
		final String text=configScript.getText();
		gdbinit.runAsync(new Runnable()
		{
			public void run()
			{
				String tcl="";
				tcl+="set fp [aio.open "+OPENOCD_CFG_FILE+" w]\n";
				tcl+="$fp puts -nonewline {" + text +"}\n";
				tcl+="$fp close\n";
				gdbinit.executeCommandTcl(tcl);
			}
			
		});

	}
	
	
	/** Execute command on Synopsys JTAG device */
	protected String executeCommand(String string)
	{
		return gdbinit.executeCommand(string);
		
	}


	private void read(Shell shell)
	{
		gdbinit.runAsync(new Runnable()
		{
			public void run()
			{
			
				final String c=executeCommand("cat "+ OPENOCD_CFG_FILE);

				gdbinit.getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						configScript.setText(c);
						configScript.scriptChangedEvent();
					}

				});
			}
		});

	}

	public void createTab()
	{
		Composite comp = gdbinit.createTab(TABLABEL);
		createCommandsComponent(comp, 2);
		
	}
	protected void createCommandsComponent(Composite comp, int i)
	{
		GridData gd;

		fPrgmArgumentsLabelInit = new Label(comp, SWT.NONE);
		fPrgmArgumentsLabelInit.setText(LONG_DESC); //$NON-NLS-1$
		openOCDInitText = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gd.horizontalSpan = 2;
		gd.heightHint = 100;
		openOCDInitText.setLayoutData(gd);
		openOCDInitText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				configScript.scriptChangedEvent();
				gdbinit.updateIt();
			}
		});


		ConfigJTAGTab.actionButton(comp, "Write config file to Synopsys JTAG debugger", new IClick()
		{
			public void click(Shell shell)
			{
				write(shell);
			}
		});
		ConfigJTAGTab.actionButton(comp, "Read config file from Synopsys JTAG debugger", new IClick()
		{
			public void click(Shell shell)
			{
				read(shell);
			}

		});
		
	}



	/* (non-Javadoc)
	 * @see com.arc.embeddedcdt.gui.jtag.IConfigListener#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				ATTR_DEBUGGER_CONFIG,
				(String) "");

	}

	/* (non-Javadoc)
	 * @see com.arc.embeddedcdt.gui.jtag.IConfigListener#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) throws CoreException {
		getConfigScript().scriptChangedEvent();
		openOCDInitText.setText(configuration.getAttribute(
				ATTR_DEBUGGER_CONFIG, "")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.arc.embeddedcdt.gui.jtag.IConfigListener#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				ATTR_DEBUGGER_CONFIG,
				ConfigJTAGTab.getAttributeValueFrom(openOCDInitText));

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
	

	public IScript getConfigScript()
	{
		return configScript;
	}


}
