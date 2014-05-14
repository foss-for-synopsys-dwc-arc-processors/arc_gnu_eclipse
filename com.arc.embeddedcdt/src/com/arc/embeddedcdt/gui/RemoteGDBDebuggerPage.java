/*******************************************************************************
 * Copyright (c) 2006, 2010 PalmSource, Inc. and others.
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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.cdt.debug.mi.internal.ui.GDBDebuggerPage;
import org.eclipse.cdt.internal.launch.remote.Messages;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.launch.IMILaunchConfigurationConstants;
import com.arc.embeddedcdt.launch.Launch;

/**
 * The dynamic debugger tab for remote launches using gdb server.
 * The gdbserver settings are used to start a gdbserver session on the
 * remote and then to connect to it from the host. The DSDP-TM project is
 * used to accomplish this.
 */
public class RemoteGDBDebuggerPage extends GDBDebuggerPage {

	//protected Text fGDBServerCommandText;
	protected Combo fPrgmArgumentsComboInit;//this variable for select which externally tools
	protected static  Text fPrgmArgumentsTextInit;// this variable for showing  which target is be selected
	public static String  fPrgmArgumentsComboInittext=null; //this variable is for getting user's input initial command
	protected Text fGDBServerPortNumberText;
	protected Text fGDBServerIPAddressText;
	public static String comport="";//this variable is for launching the exactly com port chosen by users
	protected Button fSearchexternalButton;//this button is for searching the path for external tools
	protected Label fSearchexternalLabel;
	protected Button fLaunchComButton;//this variable is for launching COM port
	protected Button fLaunchernalButton;//this button is for launching the external tools
	protected Button fLaunchterminallButton;//this button is for launching the external tools
	protected Text fPrgmArgumentsTextexternal;//this button is for searching the path for external tools
	protected Combo fPrgmArgumentsComCom;//this variable is for getting user's input COM port
	private boolean fSerialPortAvailable = true;
	protected Label fPrgmArgumentsLabelCom;//this variable is for showing COM port
	
    static String runcom="";//this variable is for saving user's input run command
	static String externalpath="";//this variable is for saving user's external path

	static String fLaunchexternalButtonboolean="true";//this variable is to get external tools current status (Enable/disable)
	static String fLaunchTerminalboolean="true";//this variable is to get external tools current status (Enable/disable)
	
	public Boolean createTabitemCOMBool=false;
	public Boolean createTabitemnSIMBool=false;
	
	
	// Constants
	public static final String ASHLING_DEFAULT_PATH_WINDOWS = "C:\\AshlingOpellaXDforARC";
	public static final String ASHLING_DEFAULT_PATH_LINUX = "/usr/bin";
	
	protected Label nSIMpropslabel;
	public static Text fnSIMpropsText;
	protected Button fnSIMpropslButton;//this button is for browsing the prop files for nSIM
	public static String nSIMpropsfiles="nsim_av2em11.props";
	
	static String externaltools="";
	@Override
	public String getName() {
		return Messages.Remote_GDB_Debugger_Options;
	}
	@Override
	public void setDefaults( ILaunchConfigurationWorkingCopy configuration ) {
		super.setDefaults(configuration);
		configuration.setAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND, 
									IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND_DEFAULT );
		configuration.setAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,
									IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT_DEFAULT );
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, (String) null);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH, (String) null);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, (String) null);
		
	}
	
	/**
	 * Get default path to nSIM application nsimdrv.
	 */
	public static String getNsimdrvDefaultPath() {
	
		if (isWindowsOS()) {
			return System.getenv("NSIM_HOME") + java.io.File.separator
					+ "bin" + java.io.File.separator +
					"nsimdrv.exe";
		} else {
			return System.getenv("NSIM_HOME") + java.io.File.separator
					+ "bin" + java.io.File.separator +
					"nsimdrv";
		}
	}
	
	public static String getOpenOCDDefaultPath() {
		if (isWindowsOS()) {
			String s = System.getProperty("eclipse.home.location");
			s = s.substring("file:/".length()).replace("/", "\\");
			String path = s + java.io.File.separator +
					".." + java.io.File.separator +
					"share" + java.io.File.separator +
					"openocd" + java.io.File.separator +
					"scripts" + java.io.File.separator +
					"target" + java.io.File.separator +
					"snps_starter_kit_arc-em.cfg";
			try {
				return Paths.get(path).toRealPath().toString();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		} else {
			return "/usr/local/share/openocd/scripts/target/snps_starter_kit_arc-em.cfg";
		}
	}

	@Override
	public void initializeFrom( ILaunchConfiguration configuration ) {
		super.initializeFrom(configuration);
		String gdbserverCommand = null;
		try {
			gdbserverCommand = configuration.getAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND,
														   IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND_DEFAULT);
			
		}
		catch( CoreException e ) {
		}
		
		fGDBCommandText.setText( "arc-elf32-gdb" );
		try {
		    externaltools = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String());
		if (configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String()).equalsIgnoreCase(""))
		{
			fPrgmArgumentsComboInit.setText(fPrgmArgumentsComboInit.getItem(0));
		}
		else fPrgmArgumentsComboInit.setText(externaltools);	
		
		
		 if(externaltools.lastIndexOf("via")>1&&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("true"))
		 {
			 fSearchexternalLabel.setText(externaltools.substring(externaltools.lastIndexOf("via")+4, externaltools.length())+" Path:");
//			 fLaunchernalButton.setSelection(true);//setText("Enable Launch "+externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length()));//get current status
			 fLaunchexternalButtonboolean="true";
//			 fSearchexternalLabel.setEnabled(true);
//			 fSearchexternalButton.setEnabled(true);
//			 fPrgmArgumentsTextexternal.setEnabled(true);
			 fLaunchernalButton.setText("Launch "+externaltools.substring(externaltools.lastIndexOf("via")+4, externaltools.length()));
			 }
		 else  if(externaltools.lastIndexOf("via")>1&&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false")) 
		 {
			 fSearchexternalLabel.setText(externaltools.substring(externaltools.lastIndexOf("via")+4, externaltools.length())+" Path:");
//			 fLaunchernalButton.setSelection(false);//fLaunchernalButton.setText("Disable Launch "+externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length()));//get current status
			 fLaunchexternalButtonboolean="false";
//			 fSearchexternalLabel.setEnabled(false);
//			 fSearchexternalButton.setEnabled(false);
//			 fPrgmArgumentsTextexternal.setEnabled(false);
//			 fLaunchernalButton.setText("Launch "+externaltools.substring(externaltools.lastIndexOf("via")+4, externaltools.length()));
			
		 }
		 else if (externaltools.lastIndexOf("via")<1
				 &&!configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("true")
				 &&!externaltools.equalsIgnoreCase("nSIM")
				 )
		 {
			 fSearchexternalLabel.setText("OpenOCD Path:");
//			 fLaunchernalButton.setSelection(true);
//			 fLaunchernalButton.setText("Launch OpenOCD");//fLaunchernalButton.setText("Enable Launch OpenOCD");
//			 fSearchexternalLabel.setEnabled(true);
//			 fSearchexternalButton.setEnabled(true);
//			 fPrgmArgumentsTextexternal.setEnabled(true);
			 fLaunchexternalButtonboolean="true";
		 }
		 else if (externaltools.lastIndexOf("via")<1
				 &&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false")
				 &&!externaltools.equalsIgnoreCase("nSIM"))
		 {
			 fSearchexternalLabel.setText("OpenOCD Path:");
//			 fLaunchernalButton.setSelection(false);
//			 fLaunchernalButton.setText("Launch OpenOCD");//fLaunchernalButton.setText("Disable Launch OpenOCD");
//			 fSearchexternalLabel.setEnabled(false);
//			 fSearchexternalButton.setEnabled(false);
//			 fPrgmArgumentsTextexternal.setEnabled(false);
			 fLaunchexternalButtonboolean="false";
		 }
		 else if (externaltools.equalsIgnoreCase("nSIM")
				 &&!configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("ture"))
		 {
//			 fLaunchComButton.setSelection(false);
			 fLaunchTerminalboolean="false";
//			 fPrgmArgumentsComCom.setEnabled(false);
//	        fPrgmArgumentsLabelCom.setEnabled(false);
	        
	         fSearchexternalLabel.setText("nSIM Path:");
//			 fLaunchernalButton.setSelection(true);
//			 fLaunchernalButton.setText("Launch nSIM");//fLaunchernalButton.setText("Enable Launch OpenOCD");
//			 fSearchexternalLabel.setEnabled(true);
//			 fSearchexternalButton.setEnabled(true);
//			 fPrgmArgumentsTextexternal.setEnabled(true);
			 fLaunchexternalButtonboolean="true";
		 }
		 else if (externaltools.equalsIgnoreCase("nSIM")
				 &&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false"))
		 {
//			 fLaunchComButton.setSelection(false);
			 fLaunchTerminalboolean="false";
//			 fPrgmArgumentsComCom.setEnabled(false);
//	         fPrgmArgumentsLabelCom.setEnabled(false);
//	        
//	        
//			 fLaunchComButton.setSelection(false);
			 fSearchexternalLabel.setText("OpenOCD Path:");
//			 fLaunchernalButton.setSelection(false);
//			 fLaunchernalButton.setText("Launch OpenOCD");//fLaunchernalButton.setText("Disable Launch OpenOCD");
//			 fSearchexternalLabel.setEnabled(false);
//			 fPrgmArgumentsTextexternal.setEnabled(false);
			 fLaunchexternalButtonboolean="false";
		 }

		 // Set host and IP.
		 try {
			 String portnumber = configuration.getAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,new String() );
			 if(portnumber.equalsIgnoreCase("")){
				 portnumber="3333"; 
			 }
     		 fGDBServerPortNumberText.setText( portnumber );
			 String hostname = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS, new String());
			 if(hostname.equalsIgnoreCase("")){
				 hostname = "localhost";
			 }
			 fGDBServerIPAddressText.setText(hostname);
			 

				
			 comport=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT, new String());
			 if (FirstlaunchDialog.value[1] != null) {
					if (!FirstlaunchDialog.value[1].equalsIgnoreCase("")) {
						comport = FirstlaunchDialog.value[1];
					}

				}
			 
//			 if(!comport.equalsIgnoreCase(""))
//			 {
//				 int privious=fPrgmArgumentsComCom.indexOf(comport);
//				 if(privious>-1)
//				     fPrgmArgumentsComCom.remove(privious);
//				 fPrgmArgumentsComCom.add(comport, 0);
//			 }
//			 
			 nSIMpropsfiles = configuration.getAttribute(LaunchConfigurationConstants.ATTR_NSIM_PROP_FILE, new String());
			 if(nSIMpropsfiles.equalsIgnoreCase("")){
				 nSIMpropsfiles = "nsim_av2em11.props";
			 }
			 //fnSIMpropsText.setText(nsimprop);
			 
			 
			
		 }
		 catch( CoreException e ) {
		 }
		
		 String Terminallaunch=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, new String());//get which external tool is in use

			if (!groupcom.isDisposed()) {
				if (!Terminallaunch.equalsIgnoreCase("false")) {
					fLaunchComButton.setSelection(true);// setText("Enable Launch Terminal");
					fLaunchTerminalboolean = "true";
					if (fPrgmArgumentsComCom.getItemCount() > 0) {
						fPrgmArgumentsComCom.setEnabled(true);
						fPrgmArgumentsLabelCom.setEnabled(true);
					} else {
						fPrgmArgumentsComCom.setEnabled(false);
						fPrgmArgumentsLabelCom.setEnabled(false);
					}
				} else if (Terminallaunch.equalsIgnoreCase("false")) {
					fLaunchComButton.setSelection(false);// setText("Enable Launch Terminal");
					fLaunchTerminalboolean = "false";
					fPrgmArgumentsComCom.setEnabled(false);
					fPrgmArgumentsLabelCom.setEnabled(false);

				}
			}

		// fLaunchComButton.setText("Launch Terminal");
//		 if (fSerialPortAvailable)
//			 fPrgmArgumentsComCom.setText(fPrgmArgumentsComCom.getItem(0));
		
		String gdbserver=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, "JTAG via OpenOCD"/*new String()*/);
		
		 if(!gdbserver.equalsIgnoreCase(""))
		 {
			 int privious=fPrgmArgumentsComboInit.indexOf(gdbserver);
			 if(privious>-1)
				 fPrgmArgumentsComboInit.remove(privious);
			 fPrgmArgumentsComboInit.add(gdbserver, 0);
			 fPrgmArgumentsComboInit.select(0);
		 }
		
		
		if(gdbserver.indexOf("Ashling")>-1)
		{
			// Ashling GDB server
			String defaultValue = isWindowsOS() ? ASHLING_DEFAULT_PATH_WINDOWS : ASHLING_DEFAULT_PATH_LINUX;
			fPrgmArgumentsTextexternal.setText(
				configuration.getAttribute(
					LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH,
					defaultValue
				)
			);
		}
		else if	(gdbserver.indexOf("OpenOCD")>-1) {
			fPrgmArgumentsTextexternal.setText(
				configuration.getAttribute(
					LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH,
					getOpenOCDDefaultPath()
				)
			);
		} else {
			// nSIM and default case
			fPrgmArgumentsTextexternal.setText(
				configuration.getAttribute(
					LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH,
					getNsimdrvDefaultPath()
				)
			);
		}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void performApply( ILaunchConfigurationWorkingCopy configuration ) {
		super.performApply(configuration);
		//String str = fGDBServerCommandText.getText();
		//str.trim();
		//configuration.setAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND, str );
		String str = fGDBServerPortNumberText.getText();
		str.trim();
	
		configuration.setAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT, str );
		
		String gdbStr = fGDBCommandText.getText();
		gdbStr.trim();
		configuration.setAttribute(IMILaunchConfigurationConstants.ATTR_DEBUG_NAME, gdbStr);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,CommandTab.getAttributeValueFromString(fPrgmArgumentsComboInit.getItem(0)));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH,externalpath);
		
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,getAttributeValueFromString(fPrgmArgumentsComboInittext));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT,getAttributeValueFromString(fLaunchexternalButtonboolean));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_NSIM_PROP_FILE,nSIMpropsfiles);
		if (fSerialPortAvailable)
			configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT,getAttributeValueFromString(fLaunchTerminalboolean));
		
		String hostname = fGDBServerIPAddressText.getText();
		configuration.setAttribute(
				LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS,
				getAttributeValueFromString(hostname)
		);
		
		
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT,getAttributeValueFromString(comport));
		
	}
	/* 
	* @return true---windows 
	*/
	public static boolean isWindowsOS(){
	    boolean isWindowsOS = false;
	    String osName = System.getProperty("os.name");
	    if(osName.toLowerCase().indexOf("windows")>-1){
	      isWindowsOS = true;
	    }
	    return isWindowsOS;
	 }
	//static TabFolder subtabFolder;
	//static TabItem tabItemCOM ;
	//static TabItem tabItem2;
	static Group groupcom;
	static Group groupnsim;
	
	protected void createGdbserverSettingsTab( TabFolder tabFolder ) {
		TabItem tabItem = new TabItem( tabFolder, SWT.NONE );
		tabItem.setText( Messages.Gdbserver_Settings_Tab_Name );
		
		Composite comp = new Composite(tabFolder, SWT.NULL);
		comp.setLayout(new GridLayout(1, true));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		((GridLayout)comp.getLayout()).makeColumnsEqualWidth = false;
		comp.setFont( tabFolder.getFont() );
		tabItem.setControl( comp );
		
		final Composite subComp = new Composite(comp, SWT.NULL);
		subComp.setLayout(new GridLayout(5, true));
		subComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		((GridLayout)subComp.getLayout()).makeColumnsEqualWidth = false;
		subComp.setFont( tabFolder.getFont() );
		
	
	
	
		
		Label label = new Label(subComp, SWT.LEFT);		
		label.setText("ARC GDB Server:");
		GridData gd = new GridData();
		label.setLayoutData( gd );
		gd = new GridData();
		gd.horizontalSpan =4;
		fPrgmArgumentsComboInit =new Combo(subComp, SWT.None);//1-2 and 1-3
		fPrgmArgumentsComboInit.setLayoutData(gd);
		fPrgmArgumentsComboInit.add("JTAG via OpenOCD");
		fPrgmArgumentsComboInit.add("JTAG via Ashling");
		fPrgmArgumentsComboInit.add("nSIM");
		fPrgmArgumentsComboInit.add("Generic gdbserver");
		
		fPrgmArgumentsComboInit.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				Combo combo= (Combo)evt.widget;
				boolean isWindows=isWindowsOS();
				fGDBServerPortNumberText.getText();
				//fGDBServerIPAddressText.setText("localhost");
				fPrgmArgumentsComboInittext = combo.getText();
						    
				if (fPrgmArgumentsComboInittext
						.equalsIgnoreCase("JTAG via OpenOCD")) {
					fGDBServerPortNumberText.setText("3333");
					//subtabFolder.setVisible(true);
					fPrgmArgumentsTextexternal.setText(getOpenOCDDefaultPath());
//					fPrgmArgumentsComCom.setVisible(true);
//					fLaunchComButton.setVisible(true);
//					fPrgmArgumentsLabelCom.setVisible(true);
//					fSearchexternalButton.setVisible(true);
//					fSearchexternalLabel.setVisible(true);
//					fPrgmArgumentsTextexternal.setVisible(true);
					
					// Do not enable UI elements if serial port is not available.
//					fPrgmArgumentsComCom.setEnabled(fSerialPortAvailable);
//					fLaunchComButton.setEnabled(fSerialPortAvailable);
//					fPrgmArgumentsLabelCom.setEnabled(fSerialPortAvailable);
//					
//					fnSIMpropsText.setVisible(false);
//					fnSIMpropslButton.setVisible(false);
//					nSIMpropslabel.setVisible(false);
					
					
					groupnsim.dispose();
					if(createTabitemCOMBool==false)
						 createTabitemCOM(subComp);
					groupcom.setText("JTAG via OpenOCD");
//					subtabFolder.setSelection(tabItemCOM);
					createTabitemnSIMBool=false;
					
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via Ashling"))
				{
					//subtabFolder.setVisible(true);
					fGDBServerPortNumberText.setText("2331");
					if (isWindows)
						fPrgmArgumentsTextexternal.setText(ASHLING_DEFAULT_PATH_WINDOWS);
					else
						fPrgmArgumentsTextexternal.setText(ASHLING_DEFAULT_PATH_LINUX);
					
//					fPrgmArgumentsComCom.setVisible(fSerialPortAvailable);
//					fLaunchComButton.setVisible(fSerialPortAvailable);
//					fPrgmArgumentsLabelCom.setVisible(fSerialPortAvailable);
//					
//					fSearchexternalButton.setVisible(true);
//					fSearchexternalLabel.setVisible(true);
//					fPrgmArgumentsTextexternal.setVisible(true);
//					
//					fnSIMpropsText.setVisible(false);
//					fnSIMpropslButton.setVisible(false);
//					nSIMpropslabel.setVisible(false);
					
					groupnsim.dispose();
					if(createTabitemCOMBool==false)
						 createTabitemCOM(subComp);
					groupcom.setText("JTAG via Ashling");
//					subtabFolder.setSelection(tabItemCOM);
					createTabitemnSIMBool=false;
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("nSIM"))
				{
//					subtabFolder.setVisible(true);
					
					fGDBServerPortNumberText.setText("1234");
					fPrgmArgumentsTextexternal.setText(getNsimdrvDefaultPath());
					if (!CommandTab.initcom.isEmpty())
						CommandTab.initcom="";

//					fLaunchComButton.setSelection(false);
					fLaunchTerminalboolean="false";

//					fPrgmArgumentsComCom.setVisible(false);
//					fLaunchComButton.setVisible(false);
//					fPrgmArgumentsLabelCom.setVisible(false);
//					
//					
//					fSearchexternalButton.setVisible(true);
//					fSearchexternalLabel.setVisible(true);
//					fPrgmArgumentsTextexternal.setVisible(true);
//					
//					fnSIMpropsText.setVisible(true);
//					fnSIMpropslButton.setVisible(true);
//					nSIMpropslabel.setVisible(true);
					
					IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();

					String viewId = "org.eclipse.tm.terminal.view.TerminalView"; 

					if (page != null) {
						IViewReference[] viewReferences = page.getViewReferences();
						for (IViewReference ivr : viewReferences) {
							if (ivr.getId().equalsIgnoreCase(viewId)
									|| ivr.getId().equalsIgnoreCase("more view id if you want to close more than one at a time")) {
								page.hideView(ivr);
							}
						}
					}
					
//					subtabFolder.setSelection(tabItem2);
					groupcom.dispose();
					if(createTabitemnSIMBool==false)
					    createTabitemnSIM(subComp);
					groupnsim.setText("nSIM");
					createTabitemCOMBool=false;
					
					
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("Generic gdbserver"))
				{

					fLaunchTerminalboolean="false";

//					fPrgmArgumentsComCom.setVisible(false);
//					fLaunchComButton.setVisible(false);
//					fPrgmArgumentsLabelCom.setVisible(false);
//					fSearchexternalButton.setVisible(false);
//					fSearchexternalLabel.setVisible(false);
//					fLaunchernalButton.setVisible(false);
//					fPrgmArgumentsTextexternal.setVisible(false);
					IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();

					String viewId = "org.eclipse.tm.terminal.view.TerminalView"; 

					if (page != null) {
						IViewReference[] viewReferences = page.getViewReferences();
						for (IViewReference ivr : viewReferences) {
							if (ivr.getId().equalsIgnoreCase(viewId)
									|| ivr.getId().equalsIgnoreCase("more view id if you want to close more than one at a time")) {
								page.hideView(ivr);
							}
						}
					}
//					subtabFolder.setVisible(false);
					if(!groupcom.isDisposed())
					      groupcom.setVisible(false);
					if(!groupnsim.isDisposed())
					    groupnsim.setVisible(false);
					
				}

				if (fPrgmArgumentsComboInittext.lastIndexOf("via")>-1){
					fSearchexternalLabel.setText(fPrgmArgumentsComboInittext.substring(fPrgmArgumentsComboInittext.lastIndexOf("via")+4, fPrgmArgumentsComboInittext.length())+" Path:");
					fLaunchernalButton.setText("Launch "+fPrgmArgumentsComboInittext.substring(fPrgmArgumentsComboInittext.lastIndexOf("via")+4, fPrgmArgumentsComboInittext.length()));
				} else {
					fSearchexternalLabel.setText(fPrgmArgumentsComboInittext+" Path:");
					fLaunchernalButton.setText("Launch "+fPrgmArgumentsComboInittext);

				}
				
//				if(fLaunchComButton.getSelection()==true){
//					fLaunchTerminalboolean="true";
//					fPrgmArgumentsComCom.setEnabled(fSerialPortAvailable);
//					fPrgmArgumentsLabelCom.setEnabled(fSerialPortAvailable);
//				}
				updateLaunchConfigurationDialog();

			
			}
			});
		
		
		//-----------------------------------
		
		
		
		//fGDBServerCommandText = new Text(subComp, SWT.SINGLE | SWT.BORDER);
		//GridData data = new GridData();
		//fGDBServerCommandText.setLayoutData(data);
		//fGDBServerCommandText.addModifyListener( new ModifyListener() {

		//	public void modifyText( ModifyEvent evt ) {
		//		updateLaunchConfigurationDialog();
		//	}
		//} );

		
		label = new Label(subComp, SWT.LEFT);
		label.setText(Messages.Port_number_textfield_label);
		gd = new GridData();
		gd.horizontalSpan =1;
		label.setLayoutData( gd );
		
		fGDBServerPortNumberText = new Text(subComp, SWT.SINGLE | SWT.BORDER| SWT.BEGINNING);
		gd = new GridData();
		gd.horizontalSpan =4;
		fGDBServerPortNumberText.setLayoutData(gd);
		fGDBServerPortNumberText.addModifyListener( new ModifyListener() {

			public void modifyText( ModifyEvent evt ) {
				updateLaunchConfigurationDialog();
				//portnumber=fGDBServerPortNumberText.getText();
			}
		} );
		
		label = new Label(subComp, SWT.LEFT);
		label.setText("Host Address:");
		gd = new GridData();
		gd.horizontalSpan =1;
		label.setLayoutData( gd );
		fGDBServerIPAddressText = new Text(subComp, SWT.SINGLE | SWT.BORDER| SWT.BEGINNING);
		gd = new GridData();
		gd.horizontalSpan =4;
		fGDBServerIPAddressText.setLayoutData( gd );
		fGDBServerIPAddressText.setText("localhost");
		fGDBServerIPAddressText.addModifyListener( new ModifyListener() {

			public void modifyText( ModifyEvent evt ) {
				updateLaunchConfigurationDialog();
			}
		} );
		
				
		fSearchexternalLabel=new Label(subComp, SWT.LEFT);
		fSearchexternalLabel.setText("Path");
		gd = new GridData();
		fSearchexternalLabel.setLayoutData(gd);
			
		fPrgmArgumentsTextexternal=new Text(subComp, SWT.SINGLE | SWT.BORDER);//6-1
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint=400;
		gd.horizontalSpan =2;
		fPrgmArgumentsTextexternal.setLayoutData(gd);
		fPrgmArgumentsTextexternal.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				externalpath=fPrgmArgumentsTextexternal.getText();
				updateLaunchConfigurationDialog();
			}
		});
		
		fSearchexternalButton = createPushButton(subComp, "Browse", null); //$NON-NLS-1$  //6-2
		gd = new GridData(SWT.BEGINNING);
		fSearchexternalButton.setLayoutData(gd);
		fSearchexternalButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});
	
		fLaunchernalButton = new Button(subComp,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchernalButton.setSelection(true);
		gd = new GridData(SWT.BEGINNING);
		fLaunchernalButton.setLayoutData(gd);
	
		fLaunchernalButton.addSelectionListener(new SelectionListener() {

	        public void widgetSelected(SelectionEvent event) {
	        	//String tools=fPrgmArgumentsComboInittext.substring(fPrgmArgumentsComboInittext.lastIndexOf("via")+3,fPrgmArgumentsComboInittext.length());
	        	//fLaunchernalButton.setText("Launch"+tools);
	        	//if(fLaunchexternalButtonboolean.equalsIgnoreCase("true")){
	        	if(fLaunchernalButton.getSelection()==true){
	        	fLaunchexternalButtonboolean="true";
	        	fSearchexternalButton.setEnabled(true);
	        	fSearchexternalLabel.setEnabled(true);
	        	fPrgmArgumentsTextexternal.setEnabled(true);
	        	}
	        	else {
		        	fLaunchexternalButtonboolean="false";
		        	fSearchexternalButton.setEnabled(false);
		        	fSearchexternalLabel.setEnabled(false);
		        	fPrgmArgumentsTextexternal.setEnabled(false);
	           	}
	        	updateLaunchConfigurationDialog();
	        }

	        public void widgetDefaultSelected(SelectionEvent event) {
	        }
	        
	      });
		
//		subtabFolder = new TabFolder(subComp, SWT.NONE|SWT.BOTTOM);
//		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
//		gd2.horizontalSpan =5;
//		subtabFolder.setLayoutData(gd2);
        if(createTabitemnSIMBool==false)
        	createTabitemnSIM(subComp);
        if(createTabitemCOMBool==false)
        	createTabitemCOM(subComp);
		
		
		
		
	}
	

	private void createTabitemCOM(Composite subComp) { 
		
		
		
		createTabitemCOMBool=true;
//		tabItemCOM = new TabItem( subtabFolder, SWT.NONE );
//		tabItemCOM.setText("COM Tab");
		
		groupcom = SWTFactory.createGroup(subComp, fPrgmArgumentsComboInit.getItem(0), 5, 5, GridData.FILL_HORIZONTAL);
		Composite compCOM = SWTFactory.createComposite(groupcom, 5, 5, GridData.FILL_BOTH);
		
//		Composite compCOM = new Composite(subtabFolder, SWT.NULL);
//		compCOM.setLayout(new GridLayout(1, true));
//		compCOM.setLayoutData(new GridData(GridData.FILL_BOTH));
//		((GridLayout)compCOM.getLayout()).makeColumnsEqualWidth = false;
		
//		tabItemCOM.setControl( compCOM );
		
//		Composite subCompCOM = new Composite(compCOM, SWT.NULL);
//		subCompCOM.setLayout(new GridLayout(5, true));
//		subCompCOM.setLayoutData(new GridData(GridData.FILL_BOTH));
//		((GridLayout)subCompCOM.getLayout()).makeColumnsEqualWidth = false;
		
		fPrgmArgumentsLabelCom = new Label(compCOM, SWT.NONE);//5-1
		fPrgmArgumentsLabelCom.setText("COM  Ports:"); //$NON-NLS-1$
	
	
		fPrgmArgumentsComCom =new Combo(compCOM, SWT.None);//5-2 and 5-3 
		
		
		
		
		fLaunchComButton = new Button(compCOM,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchComButton.setSelection(true);
	
	
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
		
		// Set serial port list. This call might fail, for example if RxTx
		// library is not available. In this case let's just disable UI elements.
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
		
		
		fPrgmArgumentsComCom.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				Combo combo= (Combo)evt.widget;
				comport=combo.getText();
				updateLaunchConfigurationDialog();
			}
		});
		
		 if(!comport.equalsIgnoreCase(""))
		 {
			 int privious=fPrgmArgumentsComCom.indexOf(comport);
			 if(privious>-1)
			     fPrgmArgumentsComCom.remove(privious);
			 fPrgmArgumentsComCom.add(comport, 0);
			 
			 
		 }
		 fPrgmArgumentsComCom.setText(fPrgmArgumentsComCom.getItem(0));
		
	}
	private void createTabitemnSIM(Composite subComp) { 
		createTabitemnSIMBool=true;
//		tabItem2 = new TabItem(subtabFolder, SWT.NONE); 
//		tabItem2.setText("nSIM Tab");
//	
//		Composite comp2 = new Composite(subtabFolder, SWT.NULL);
//		comp2.setLayout(new GridLayout(1, true));
//		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));
//		((GridLayout)comp2.getLayout()).makeColumnsEqualWidth = false;
//	
//		tabItem2.setControl( comp2 );
		groupnsim = SWTFactory.createGroup(subComp, fPrgmArgumentsComboInit.getItem(0), 5, 5, GridData.FILL_HORIZONTAL);
		@SuppressWarnings("restriction")
		Composite compnSIM = SWTFactory.createComposite(groupnsim, 5, 5, GridData.FILL_BOTH);
		
		
		
//		Composite subComp2 = new Composite(comp2, SWT.NULL);
//		subComp2.setLayout(new GridLayout(5, true));
//		subComp2.setLayoutData(new GridData(GridData.FILL_BOTH));
//		((GridLayout)subComp2.getLayout()).makeColumnsEqualWidth = false;
		nSIMpropslabel = new Label(compnSIM, SWT.CENTER);
		nSIMpropslabel.setText("nSIM Props:");
	
		fnSIMpropsText = new Text(compnSIM, SWT.SINGLE | SWT.BORDER| SWT.BEGINNING);
		
	
		fnSIMpropsText.setText(nSIMpropsfiles);
		fnSIMpropsText.addModifyListener( new ModifyListener() {

			public void modifyText( ModifyEvent evt ) {
				nSIMpropsfiles=fnSIMpropsText.getText();
				updateLaunchConfigurationDialog();
			}
		} );
		
		fnSIMpropslButton = createPushButton(compnSIM, "Browse", null); //$NON-NLS-1$  //6-2
		
	
		fnSIMpropslButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handlepropsBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});
		} 
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.debug.mi.internal.ui.GDBDebuggerPage#createTabs(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabs( TabFolder tabFolder ) {
		super.createTabs( tabFolder );
		createGdbserverSettingsTab( tabFolder );
	}
	protected void handleBinaryBrowseButtonSelected() {
		if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via OpenOCD")||fPrgmArgumentsComboInittext.equalsIgnoreCase("nSIM"))
		{
			FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
			fileDialog.setFileName(fPrgmArgumentsTextexternal.getText());
			String text= fileDialog.open();
			if (text != null) {
				fPrgmArgumentsTextexternal.setText(text);
			}
			 
		}
		else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via Ashling"))
		{
			DirectoryDialog directoryDialog = new DirectoryDialog(getShell(), SWT.NONE);
			directoryDialog.setFilterPath(fPrgmArgumentsTextexternal.getText());
			String text= directoryDialog.open();
			if (text != null) {
				fPrgmArgumentsTextexternal.setText(text);
			}
			 
		}
	}
	
	protected void handlepropsBinaryBrowseButtonSelected() {
			FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
			fileDialog.setFileName(fnSIMpropsText.getText());
			String text= fileDialog.open();
			nSIMpropsfiles=fileDialog.getFileName();
			if (text != null) {
				fnSIMpropsText.setText(nSIMpropsfiles);
			}
	}
	public static String getAttributeValueFromString(String string) {
		String content = string;
		if (content.length() > 0) {
			return content;
		}
		return null;
	}
}

