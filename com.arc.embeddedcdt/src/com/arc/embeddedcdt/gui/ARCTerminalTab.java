/*******************************************************************************
 * Copyright (c) 2006, 2015 PalmSource, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Ewa Matejska (PalmSource)
 * 
 * Referenced GDBDebuggerPage code to write this.
 * Anna Dushistova (Mentor Graphics) - moved to org.eclipse.cdt.launch.remote.tabs
 * Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/
package com.arc.embeddedcdt.gui;

import java.util.List;

import org.eclipse.cdt.launch.ui.CLaunchConfigurationTab;
import org.eclipse.cdt.launch.ui.ICDTLaunchHelpContextIds;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.LaunchImages;
import com.arc.embeddedcdt.launch.Launch;

public class ARCTerminalTab extends CLaunchConfigurationTab {
	protected Button fLaunchComButton;//this variable is for launching COM port
	protected Button fLaunchterminallButton;//this button is for launching the external tools
	protected Text fPrgmArgumentsTextexternal;//this button is for searching the path for external tools
	protected Combo fPrgmArgumentsComCom;//this variable is for getting user's input COM port
	private boolean fSerialPortAvailable = true;
	public String comport_openocd="";//this variable is for launching the exactly com port chosen by users
	public String comport_ashling="";//this variable is for launching the exactly com port chosen by users
	protected Label fPrgmArgumentsLabelCom;//this variable is for showing COM port
	static String fLaunchTerminalboolean="true";//this variable is to get external tools current status (Enable/disable)
	static String gdbserver = null;
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_ARGUMNETS_TAB);
		
		GridLayout topLayout = new GridLayout();
		comp.setLayout(topLayout);

		createVerticalSpacer(comp, 1);
		createTerminalComponent(comp, 1);
	}


	protected void createTerminalComponent(Composite comp, int i) {
		Composite argsComp = SWTFactory.createComposite(comp, 3, 3, GridData.FILL_BOTH);
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 3;
		
		argsComp.setLayoutData(gd);

		
		fPrgmArgumentsLabelCom = new Label(argsComp, SWT.NONE);//5-1
		fPrgmArgumentsLabelCom.setText("COM  Ports:"); //$NON-NLS-1$
	
		fPrgmArgumentsComCom =new Combo(argsComp, SWT.None);//5-2 and 5-3
		fPrgmArgumentsComCom.setEnabled(Boolean.parseBoolean(fLaunchTerminalboolean));
		List<String> COM = null;
		try {
			COM = Launch.COMserialport();
		} catch (java.lang.UnsatisfiedLinkError e) {
			e.printStackTrace();
		} catch (java.lang.NoClassDefFoundError e) {
			e.printStackTrace();
		}

		if (COM != null) {
			for (int ii=0;ii<COM.size();ii++) {
				    String currentcom=(String) COM.get(ii);
				    fPrgmArgumentsComCom.add(currentcom);
				    
	        }
		} 
		else {
			fSerialPortAvailable = false;
			fPrgmArgumentsComCom.setEnabled(fSerialPortAvailable);
			fLaunchComButton.setEnabled(fSerialPortAvailable);
		}
		fPrgmArgumentsComCom.setText(fPrgmArgumentsComCom.getItem(0));
		fLaunchComButton = new Button(argsComp,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchComButton.setSelection(Boolean.parseBoolean(fLaunchTerminalboolean));
	
		fLaunchComButton.setText("Launch Terminal");
		fLaunchComButton.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent event) {
	        	if(fLaunchComButton.getSelection()==true){
	        		fLaunchTerminalboolean="true";
	        		fPrgmArgumentsComCom.setEnabled(fSerialPortAvailable);
	        		fPrgmArgumentsLabelCom.setEnabled(fSerialPortAvailable);
	        	} else {
	        		fLaunchTerminalboolean="false";
		        	fPrgmArgumentsComCom.setEnabled(false);
		        	fPrgmArgumentsLabelCom.setEnabled(false);
	           	}
	        	updateLaunchConfigurationDialog();
	        }

	        public void widgetDefaultSelected(SelectionEvent event) {
	        }
	        
	      });
		

	}
	protected void handleBinarylaunchButtonSelected(){
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT,getAttributeValueFromString(fLaunchTerminalboolean));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_OPENOCD_PORT,getAttributeValueFromString(comport_openocd));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_ASHLING_PORT,getAttributeValueFromString(comport_ashling));
			
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		
		try {
			comport_openocd=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_OPENOCD_PORT, "");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if (FirstlaunchDialog.value[1] != null) {
				if (!FirstlaunchDialog.value[1].equalsIgnoreCase("")) {
					comport_openocd = FirstlaunchDialog.value[1];
				}

			}
		 try {
			comport_ashling=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_ASHLING_PORT, "");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if (FirstlaunchDialog.value[1] != null) {
				if (!FirstlaunchDialog.value[1].equalsIgnoreCase("")) {
					comport_ashling = FirstlaunchDialog.value[1];
				}

			}	
		 
		 
		try {
			fLaunchTerminalboolean = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, "true");
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fPrgmArgumentsComCom.setEnabled(Boolean.parseBoolean(fLaunchTerminalboolean));
		fPrgmArgumentsLabelCom.setEnabled(Boolean.parseBoolean(fLaunchTerminalboolean));
		
		try {
			gdbserver = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, "JTAG via OpenOCD"/*""*/);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(gdbserver.indexOf("OpenOCD")>-1){
			if (!comport_openocd.equalsIgnoreCase("")) {
				int privious = fPrgmArgumentsComCom.indexOf(comport_openocd);
				if (privious > -1)
					fPrgmArgumentsComCom.remove(privious);
				fPrgmArgumentsComCom.add(comport_openocd, 0);

			}
			fPrgmArgumentsComCom.setText(fPrgmArgumentsComCom.getItem(0));
			fPrgmArgumentsComCom.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent evt) {
					Combo combo = (Combo) evt.widget;
					comport_openocd = combo.getText();
					updateLaunchConfigurationDialog();
				}
			});
		   }
	     if(gdbserver.indexOf("Ashlin")>-1){
			if (!comport_ashling.equalsIgnoreCase("")) {
				int privious = fPrgmArgumentsComCom.indexOf(comport_ashling);
				if (privious > -1)
					fPrgmArgumentsComCom.remove(privious);
				fPrgmArgumentsComCom.add(comport_ashling, 0);

			}
			fPrgmArgumentsComCom.setText(fPrgmArgumentsComCom.getItem(0));
			fPrgmArgumentsComCom.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent evt) {
					Combo combo = (Combo) evt.widget;
					comport_ashling = combo.getText();
					updateLaunchConfigurationDialog();
				}
			});
	     }
	     
	}


	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (fSerialPortAvailable)
			configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT,getAttributeValueFromString(fLaunchTerminalboolean));
		
		if(gdbserver.indexOf("OpenOCD")>-1){
			configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_OPENOCD_PORT,getAttributeValueFromString(fPrgmArgumentsComCom.getText()));
	     }
	     if(gdbserver.indexOf("Ashlin")>-1){
			configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_ASHLING_PORT,getAttributeValueFromString(fPrgmArgumentsComCom.getText()));
	     }
	     
			

		
		
		   }
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "Terminal";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
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
	public static String getAttributeValueFromString(String string) {
		String content = string;
		if (content.length() > 0) {
			return content;
		}
		return null;
	}
	protected String getAttributeValueFromCombo(Combo combo) {
	
		String content = combo.getText().trim();
		if (content.length() > 0) {
			return content;
		}
		
		return null;
	}


}
