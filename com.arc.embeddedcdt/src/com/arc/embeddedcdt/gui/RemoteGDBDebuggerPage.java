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

import java.util.List;

import org.eclipse.cdt.debug.mi.internal.ui.GDBDebuggerPage;
import org.eclipse.cdt.internal.launch.remote.Messages;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Perspective;
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
	public static String comport=null;//this variable is for launching the exactly com port chosen by users
	protected Button fSearchexternalButton;//this button is for searching the path for external tools
	protected Label fSearchexternalLabel;
	protected Button fLaunchComButton;//this variable is for launching COM port
	protected Button fLaunchernalButton;//this button is for launching the external tools
	protected Button fLaunchterminallButton;//this button is for launching the external tools
	protected Text fPrgmArgumentsTextexternal;//this button is for searching the path for external tools
	protected Combo fPrgmArgumentsComCom;//this variable is for getting user's input COM port
	protected Label fPrgmArgumentsLabelCom;//this variable is for showing COM port
	
    static String runcom="";//this variable is for saving user's input run command
	static String externalpath="";//this variable is for saving user's external path
	static String portnumber="";//this variable is for saving user's portnumber
	static String IPAddress="";//this variable is for saving user's portnumber

	static String fLaunchexternalButtonboolean="true";//this variable is to get external tools current status (Enable/disable)
	static String fLaunchTerminalboolean="true";//this variable is to get external tools current status (Enable/disable)
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
	
	@Override
	public void initializeFrom( ILaunchConfiguration configuration ) {
		super.initializeFrom(configuration);
		String gdbserverCommand = null;
		String gdbserverPortNumber = null;
		try {
			gdbserverCommand = configuration.getAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND,
														   IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND_DEFAULT);
		}
		catch( CoreException e ) {
		}
		try {
			gdbserverPortNumber = configuration.getAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,"" );
			portnumber =gdbserverPortNumber;
		}
		catch( CoreException e ) {
		}
		//fGDBServerCommandText.setText( gdbserverCommand );
		fGDBServerPortNumberText.setText( gdbserverPortNumber );
		fGDBCommandText.setText( "arc-elf32-gdb" );
		try {
		String externaltools = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String());
		if (configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String()).equalsIgnoreCase(""))
		{
			fPrgmArgumentsComboInit.setText(fPrgmArgumentsComboInit.getItem(0));
		}
		else fPrgmArgumentsComboInit.setText(externaltools);	
		
		
		 if(externaltools.lastIndexOf("via")>1&&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("true"))
		 {
			 fSearchexternalLabel.setText(externaltools.substring(externaltools.lastIndexOf("via")+4, externaltools.length())+" Path:");
			 fLaunchernalButton.setSelection(true);//setText("Enable Launch "+externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length()));//get current status
			 fLaunchexternalButtonboolean="true";
			 fSearchexternalLabel.setEnabled(true);
			 fSearchexternalButton.setEnabled(true);
			 fPrgmArgumentsTextexternal.setEnabled(true);
			 fLaunchernalButton.setText("Launch "+externaltools.substring(externaltools.lastIndexOf("via")+4, externaltools.length()));
			 }
		 else  if(externaltools.lastIndexOf("via")>1&&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false")) 
		 {
			 fSearchexternalLabel.setText(externaltools.substring(externaltools.lastIndexOf("via")+4, externaltools.length())+" Path:");
			 fLaunchernalButton.setSelection(false);//fLaunchernalButton.setText("Disable Launch "+externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length()));//get current status
			 fLaunchexternalButtonboolean="false";
			 fSearchexternalLabel.setEnabled(false);
			 fSearchexternalButton.setEnabled(false);
			 fPrgmArgumentsTextexternal.setEnabled(false);
			 fLaunchernalButton.setText("Launch "+externaltools.substring(externaltools.lastIndexOf("via")+4, externaltools.length()));
			
		 }
		 else if (externaltools.lastIndexOf("via")<1
				 &&!configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("ture")
				 &&!externaltools.equalsIgnoreCase("nSIM")
				 )
		 {
			 fSearchexternalLabel.setText("OpenOCD Path:");
			 fLaunchernalButton.setSelection(true);
			 fLaunchernalButton.setText("Launch OpenOCD");//fLaunchernalButton.setText("Enable Launch OpenOCD");
			 fSearchexternalLabel.setEnabled(true);
			 fSearchexternalButton.setEnabled(true);
			 fPrgmArgumentsTextexternal.setEnabled(true);
			 fLaunchexternalButtonboolean="true";
		 }
		 else if (externaltools.lastIndexOf("via")<1
				 &&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false")
				 &&!externaltools.equalsIgnoreCase("nSIM"))
		 {
			 fSearchexternalLabel.setText("OpenOCD Path:");
			 fLaunchernalButton.setSelection(false);
			 fLaunchernalButton.setText("Launch OpenOCD");//fLaunchernalButton.setText("Disable Launch OpenOCD");
			 fSearchexternalLabel.setEnabled(false);
			 fSearchexternalButton.setEnabled(false);
			 fPrgmArgumentsTextexternal.setEnabled(false);
			 fLaunchexternalButtonboolean="false";
		 }
		 else if (externaltools.equalsIgnoreCase("nSIM")
				 &&!configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("ture"))
		 {
			 fLaunchComButton.setSelection(false);
			 fLaunchTerminalboolean="false";
			 fPrgmArgumentsComCom.setEnabled(false);
	        fPrgmArgumentsLabelCom.setEnabled(false);
	        
	         fSearchexternalLabel.setText("nSIM Path:");
			 fLaunchernalButton.setSelection(true);
			 fLaunchernalButton.setText("Launch nSIM");//fLaunchernalButton.setText("Enable Launch OpenOCD");
			 fSearchexternalLabel.setEnabled(true);
			 fSearchexternalButton.setEnabled(true);
			 fPrgmArgumentsTextexternal.setEnabled(true);
			 fLaunchexternalButtonboolean="true";
		 }
		 else if (externaltools.equalsIgnoreCase("nSIM")
				 &&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false"))
		 {
			 fLaunchComButton.setSelection(false);
			 fLaunchTerminalboolean="false";
			 fPrgmArgumentsComCom.setEnabled(false);
	         fPrgmArgumentsLabelCom.setEnabled(false);
	        
	        
			 fLaunchComButton.setSelection(false);
			 fSearchexternalLabel.setText("OpenOCD Path:");
			 fLaunchernalButton.setSelection(false);
			 fLaunchernalButton.setText("Launch OpenOCD");//fLaunchernalButton.setText("Disable Launch OpenOCD");
			 fSearchexternalLabel.setEnabled(false);
			 fPrgmArgumentsTextexternal.setEnabled(false);
			 fLaunchexternalButtonboolean="false";
		 }
		
		 String Terminallaunch=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, new String());//get which external tool is in use
		 if(!Terminallaunch.equalsIgnoreCase("false"))
		 {
			 fLaunchComButton.setSelection(true);//setText("Enable Launch Terminal");
			 fLaunchTerminalboolean="true";
			 fPrgmArgumentsComCom.setEnabled(true);
	         fPrgmArgumentsLabelCom.setEnabled(true);
			 }
		 else if(Terminallaunch.equalsIgnoreCase("false")) 
		 {
			 fLaunchComButton.setSelection(false);//fLaunchComButton.setText("Disable Launch Terminal");
			 fLaunchTerminalboolean="false";
			 fPrgmArgumentsComCom.setEnabled(false);
	        fPrgmArgumentsLabelCom.setEnabled(false);
			
		 }
		 fLaunchComButton.setText("Launch Terminal");
		fPrgmArgumentsComCom.setText(fPrgmArgumentsComCom.getItem(0));
		
		fPrgmArgumentsTextexternal.setText(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH, "${INSTALL_DIR}/share/openocd/scripts/target/snps_starter_kit_arc-em.cfg")); //$NON-NLS-1$
		if(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String()).indexOf("Ashling")>-1)
		{
			fPrgmArgumentsTextexternal.setText(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH,"C:\\AshlingOpellaXDforARC")); 
		}
		else if	(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String()).indexOf("OpenOCD")>-1)
		{
			fPrgmArgumentsTextexternal.setText(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH, "${INSTALL_DIR}/share/openocd/scripts/target/snps_starter_kit_arc-em.cfg")); //$NON-NLS-1$
			//fPrgmArgumentsTextexternal.setText(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH, externalpath)); //$NON-NLS-1$
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
		portnumber=str;
		configuration.setAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT, str );
	
		
		
		String gdbStr = fGDBCommandText.getText();
		gdbStr.trim();
		configuration.setAttribute(IMILaunchConfigurationConstants.ATTR_DEBUG_NAME, gdbStr);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,CommandTab.getAttributeValueFromString(fPrgmArgumentsComboInit.getItem(0)));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH,externalpath);
			configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT,getAttributeValueFromString(comport));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,getAttributeValueFromString(fPrgmArgumentsComboInittext));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT,getAttributeValueFromString(fLaunchexternalButtonboolean));
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT,getAttributeValueFromString(fLaunchTerminalboolean));
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
	protected void createGdbserverSettingsTab( TabFolder tabFolder ) {
		TabItem tabItem = new TabItem( tabFolder, SWT.NONE );
		tabItem.setText( Messages.Gdbserver_Settings_Tab_Name );
		
		Composite comp = new Composite(tabFolder, SWT.NULL);
		comp.setLayout(new GridLayout(1, true));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		((GridLayout)comp.getLayout()).makeColumnsEqualWidth = false;
		comp.setFont( tabFolder.getFont() );
		tabItem.setControl( comp );
		
		Composite subComp = new Composite(comp, SWT.NULL);
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
		
		fPrgmArgumentsComboInit.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				Combo combo= (Combo)evt.widget;
				boolean isWindows=isWindowsOS(); 
				fPrgmArgumentsComboInittext = combo.getText();
				if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via OpenOCD"))
				{   //if(portnumber.equalsIgnoreCase(""))
					  fGDBServerPortNumberText.setText("3333");
				
				    if(isWindows){
					    fPrgmArgumentsTextexternal.setText(externalpath);
	                }
	                else fPrgmArgumentsTextexternal.setText("/usr/local/share/openocd/scripts/target/snps_starter_kit_arc-em.cfg");
				 
					if(!CommandTab.initcom.isEmpty()&&CommandTab.initcom.startsWith("set remotetimeout")&&!CommandTab.initcom.equalsIgnoreCase("set remotetimeout 15 \ntarget remote :3333 \nload")) 
						{CommandTab.fPrgmArgumentsTextInit.setText(CommandTab.initcom);}
						
					else {
						CommandTab.fPrgmArgumentsTextInit.setText("set remotetimeout 15 \ntarget remote :"+portnumber+" \nload");
						CommandTab.initcom="set remotetimeout 15 \ntarget remote :"+portnumber+" \nload";
					}
					fPrgmArgumentsComCom.setVisible(true);

					fLaunchComButton.setVisible(true);

					fPrgmArgumentsLabelCom.setVisible(true);

				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via Ashling"))
				{
					fGDBServerPortNumberText.setText("2331");
					fPrgmArgumentsTextexternal.setText("C:\\AshlingOpellaXDforARC");
					if(!CommandTab.initcom.isEmpty()&&CommandTab.initcom.startsWith("set arc opella-target arcem")&&!CommandTab.initcom.equalsIgnoreCase("set arc opella-target arcem \ntarget remote :2331 \nload")) 
					{
						CommandTab.fPrgmArgumentsTextInit.setText(CommandTab.initcom);
						}

					else {
						CommandTab.fPrgmArgumentsTextInit.setText("set arc opella-target arcem \ntarget remote :"+portnumber+" \nload");
						CommandTab.initcom="set arc opella-target arcem \ntarget remote :"+portnumber+" \nload";
					}

					//fPrgmArgumentsComCom.setEnabled(true);
					fPrgmArgumentsComCom.setVisible(true);

					//fLaunchComButton.setEnabled(true);
					fLaunchComButton.setVisible(true);

					//fPrgmArgumentsLabelCom.setEnabled(true);		        
					fPrgmArgumentsLabelCom.setVisible(true);

				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("nSIM"))
				{
					fGDBServerPortNumberText.setText("1234");
					fPrgmArgumentsTextexternal.setText(System
							.getenv("NSIM_HOME")
							+ java.io.File.separator
							+ "bin" + java.io.File.separator + "nsimdrv");
					if (!CommandTab.initcom.isEmpty())
						CommandTab.initcom="";

					fLaunchComButton.setSelection(false);
					fLaunchTerminalboolean="false";

					//fPrgmArgumentsComCom.setEnabled(false);
					fPrgmArgumentsComCom.setVisible(false);

					//fLaunchComButton.setEnabled(false);
					fLaunchComButton.setVisible(false);

					//fPrgmArgumentsLabelCom.setEnabled(false);
					fPrgmArgumentsLabelCom.setVisible(false);
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
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("GNU simulator"))
				{

					CommandTab.fPrgmArgumentsTextInit.setText("target sim \nload");

					fPrgmArgumentsComCom.setVisible(true);

					fLaunchComButton.setVisible(true);

					fPrgmArgumentsLabelCom.setVisible(true);
				}
				if (fPrgmArgumentsComboInittext.lastIndexOf("via")>-1){
					fSearchexternalLabel.setText(fPrgmArgumentsComboInittext.substring(fPrgmArgumentsComboInittext.lastIndexOf("via")+4, fPrgmArgumentsComboInittext.length())+" Path:");
					fLaunchernalButton.setText("Launch "+fPrgmArgumentsComboInittext.substring(fPrgmArgumentsComboInittext.lastIndexOf("via")+4, fPrgmArgumentsComboInittext.length()));
				}

				else {
					fSearchexternalLabel.setText(fPrgmArgumentsComboInittext+" Path:");
					fLaunchernalButton.setText("Launch "+fPrgmArgumentsComboInittext);

				}
				if(fLaunchComButton.getSelection()==true){
					fLaunchTerminalboolean="true";
					fPrgmArgumentsComCom.setEnabled(true);
					fPrgmArgumentsLabelCom.setEnabled(true);
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.tm.terminal.view.TerminalView");
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
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
					fLaunchTerminalboolean="false";
					fPrgmArgumentsComCom.setEnabled(false);
					fPrgmArgumentsLabelCom.setEnabled(false);
				}
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
				portnumber=fGDBServerPortNumberText.getText();
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
		fGDBServerIPAddressText.addModifyListener( new ModifyListener() {

			public void modifyText( ModifyEvent evt ) {
				updateLaunchConfigurationDialog();
				IPAddress=fGDBServerIPAddressText.getText();
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
		
		
		fPrgmArgumentsLabelCom = new Label(subComp, SWT.NONE);//5-1
		fPrgmArgumentsLabelCom.setText("COM  Ports:"); //$NON-NLS-1$
		gd = new GridData(SWT.BEGINNING);
		fPrgmArgumentsLabelCom.setLayoutData(gd);
		fPrgmArgumentsComCom =new Combo(subComp, SWT.None);//5-2 and 5-3 
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan =3;
		fPrgmArgumentsComCom.setLayoutData(gd);
		List COM=Launch.COMserialport();
		for (int ii=0;ii<COM.size();ii++)
			{
				fPrgmArgumentsComCom.add((String) COM.get(ii));
		    }
		fPrgmArgumentsComCom.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				Combo combo= (Combo)evt.widget;
				comport=combo.getText();
				updateLaunchConfigurationDialog();
				}
			
		});
		
		fLaunchComButton = new Button(subComp,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchComButton.setSelection(true);
	
		gd = new GridData(SWT.BEGINNING);
		fLaunchComButton.setLayoutData(gd);
		fLaunchComButton.setText("Launch Terminal");
		fLaunchComButton.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent event) {
	        	if(fLaunchComButton.getSelection()==true){
	        		fLaunchTerminalboolean="true";
	        	    fPrgmArgumentsComCom.setEnabled(true);
	        	    fPrgmArgumentsLabelCom.setEnabled(true);
	        	    try {
					    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.tm.terminal.view.TerminalView");
				    } catch (PartInitException e) {
				        // TODO Auto-generated catch block
					    e.printStackTrace();
				    }
	        	}
	        	else {
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

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.debug.mi.internal.ui.GDBDebuggerPage#createTabs(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabs( TabFolder tabFolder ) {
		super.createTabs( tabFolder );
		createGdbserverSettingsTab( tabFolder );
	}
	protected void handleBinaryBrowseButtonSelected() {
		if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via OpenOCD"))
		{
			FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
			fileDialog.setFileName(fPrgmArgumentsTextexternal.getText());
			String text= fileDialog.open();
			if (text != null) {
				fPrgmArgumentsTextexternal.setText(text);
			}
			 
		}
		else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via Ashling")||fPrgmArgumentsComboInittext.equalsIgnoreCase("nSIM"))
		{
			DirectoryDialog directoryDialog = new DirectoryDialog(getShell(), SWT.NONE);
			directoryDialog.setFilterPath(fPrgmArgumentsTextexternal.getText());
			String text= directoryDialog.open();
			if (text != null) {
				fPrgmArgumentsTextexternal.setText(text);
			}
			 
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

