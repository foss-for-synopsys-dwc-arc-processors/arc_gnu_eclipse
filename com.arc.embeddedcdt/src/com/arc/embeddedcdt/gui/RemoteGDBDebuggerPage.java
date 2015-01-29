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

import java.io.File;
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
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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

/**
 * The dynamic debugger tab for remote launches using gdb server.
 * The gdbserver settings are used to start a gdbserver session on the
 * remote and then to connect to it from the host. The DSDP-TM project is
 * used to accomplish this.
 */
@SuppressWarnings("restriction")
public class RemoteGDBDebuggerPage extends GDBDebuggerPage {

	//protected Text fGDBServerCommandText;
	protected Combo fPrgmArgumentsComboInit;//this variable for select which externally tools
	protected static  Text fPrgmArgumentsTextInit;// this variable for showing  which target is be selected
	private static String  fPrgmArgumentsComboInittext=null; //this variable is for getting user's input initial command
	protected Text fGDBServerPortNumberText;
	protected Text fGDBServerIPAddressText;
	protected Button fSearchexternalButton;//this button is for searching the path for external tools
	protected Label fSearchexternalLabel;
	protected Text fPrgmArgumentsTextexternal;//this button is for searching the path for external tools
	private FileFieldEditor fOpenOCDBinPath; // Editor for path to OpenOCD binary
	private FileFieldEditor fOpenOCDConfigPath; // Editor for path to OpenOCD binary
	private FileFieldEditor fAshlingBinPath; // Editor for path to Ashling binary
	private FileFieldEditor  fnSIMBinPath; // Editor for path to nSIM binary
	private FileFieldEditor fnSIMTCFPath; // Editor for path to nSIM TCF path
	private FileFieldEditor fnSIMPropsPath; // Editor for path to nSIM TCF path
	private FileFieldEditor fAshlingXMLPath; // Editor for path to nSIM TCF path
    static String runcom="";//this variable is for saving user's input run command
	private String openocd_bin_path;
	static String fLaunchexternal_openocd_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	static String fLaunchexternal_ashling_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	static String fLaunchexternal_nsim_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	
	private Boolean createTabitemCOMBool=false;
	private Boolean createTabitemnSIMBool=false;
	
	private Boolean createTabitemCOMAshlingBool=false;

	
	protected Label nSIMpropslabel;
	private static Text fnSIMpropsText;
	protected Button fnSIMpropslButton;//this button is for browsing the prop files for nSIM
	private static String nSIMpropsfiles_last="";//this variable is for launching the exactly com port chosen by users
	protected Button fLaunchPropsButton;//this button is for launching the TCF for nsim
	private String fLaunchexternal_nsimprops_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	protected Button fLaunchtcfButton;//this button is for launching the Properties file for nsim
	protected Label nSIMtcflabel;
	private static Text fnSIMtcfText;
	protected Button fnSIMtcfButton;//this button is for browsing the tcf files for nSIM
	private static String nSIMtcffiles_last="";//this variable is for launching the exactly com port chosen by users
	private String fLaunchexternal_nsimtcf_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	
	private static String externaltools="";
	private String externaltools_openocd_path="";
	private String externaltools_ashling_path="";
	private static String externaltools_nsim_path="";
	private String Ashling_xml_path="";
	
	private static String portnumber="";
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
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH, (String) null);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH, (String) null);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH, (String) null);
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
	
	public static String getIDERootDirPath() {
		String s = System.getProperty("eclipse.home.location");
		s = s.substring("file:/".length()).replace("/", "\\");
		String path = s + "\\..";
		try {
			return Paths.get(path).toRealPath().toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getOpenOCDExecutableDefaultPath() {
		if (isWindowsOS()) {
			return getIDERootDirPath() + "\\bin\\openocd.exe";
		} else {
			return LaunchConfigurationConstants.DEFAULT_OPENOCD_BIN_PATH_LINUX;
		}
	}

	public static String getOpenOCDScriptDirectory() {
		if (isWindowsOS()) {
			return getIDERootDirPath() + "\\share\\openocd\\scripts";
		} else {
			return "/usr/local/share/openocd/scripts";
		}
	}

	public static String getOpenOCDScriptDefaultPath() {
		if (isWindowsOS()) {
			return getIDERootDirPath() + "\\share\\openocd\\scripts\\board\\snps_em_sk.cfg";
		} else {
			return "/usr/local/share/openocd/scripts/board/snps_em_sk.cfg";
		}
	}

	@Override
	public void initializeFrom( ILaunchConfiguration configuration ) {
		createTabitemCOMBool=false;
		createTabitemCOMAshlingBool=false;
		createTabitemnSIMBool=false;
		super.initializeFrom(configuration);
		
		fGDBCommandText.setText( "arc-elf32-gdb" );
		try {
 		    externaltools = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, "");
		    externaltools_openocd_path=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH, getOpenOCDScriptDefaultPath());
		    openocd_bin_path = configuration.getAttribute(
		    	LaunchConfigurationConstants.ATTR_DEBUGGER_OPENOCD_BIN_PATH,
		    	getOpenOCDExecutableDefaultPath());
		    
		    String default_ashling_path= isWindowsOS() ? LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_WINDOWS : LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_LINUX;
		    externaltools_ashling_path=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH,default_ashling_path);
		    
		    String ash_xml_path = new File(default_ashling_path).getParentFile().getPath()+ java.io.File.separator + "arc-opella-em.xml";
		    Ashling_xml_path=configuration.getAttribute(LaunchConfigurationConstants.ATTR_ASHLING_XML_PATH, ash_xml_path);
		    externaltools_nsim_path=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH, getNsimdrvDefaultPath());
		    
		    fLaunchexternal_openocd_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_OPENOCD_DEFAULT, "true");
		    fLaunchexternal_ashling_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_ASHLING_DEFAULT, "true");
		    fLaunchexternal_nsim_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIM_DEFAULT, "true");
		    fLaunchexternal_nsimprops_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIMPROPS_DEFAULT, "false");
		    fLaunchexternal_nsimtcf_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIMTCF_DEFAULT, "false");
		    fLaunchexternal_nsimprops_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIMPROPS_DEFAULT, "true");
		    fLaunchexternal_nsimtcf_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIMTCF_DEFAULT, "true");

		    nSIMpropsfiles_last = configuration.getAttribute(LaunchConfigurationConstants.ATTR_NSIM_PROP_FILE, "");
		    nSIMtcffiles_last = configuration.getAttribute(LaunchConfigurationConstants.ATTR_NSIM_TCF_FILE, "");
		    
		if (configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, "").equalsIgnoreCase(""))
		{
			fPrgmArgumentsComboInit.setText(fPrgmArgumentsComboInit.getItem(0));
		}
		else fPrgmArgumentsComboInit.setText(externaltools);	
		

		 // Set host and IP.
		 try {
			 portnumber = configuration.getAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,"" );
     		 fGDBServerPortNumberText.setText( portnumber );
			 String hostname = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS, "");
			 if(hostname.equalsIgnoreCase("")){
				 hostname = LaunchConfigurationConstants.DEFAULT_GDB_HOST;
			 }
			 fGDBServerIPAddressText.setText(hostname);
			
		 }
		 catch( CoreException e ) {
		 }
		
		 String gdbserver=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, "JTAG via OpenOCD"/*""*/);
		
		 if(!gdbserver.equalsIgnoreCase(""))
		 {
			 int privious=fPrgmArgumentsComboInit.indexOf(gdbserver);
			 if(privious>-1)
				 fPrgmArgumentsComboInit.remove(privious);
			 fPrgmArgumentsComboInit.add(gdbserver, 0);
			 fPrgmArgumentsComboInit.select(0);
		 }
		
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void performApply( ILaunchConfigurationWorkingCopy configuration ) {
		super.performApply(configuration);
		//configuration.setAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND, str );
		String str = fGDBServerPortNumberText.getText();
		str=str.trim();
	
		configuration.setAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT, str );
		
		String gdbStr = fGDBCommandText.getText();
		gdbStr=gdbStr.trim();
		configuration.setAttribute(IMILaunchConfigurationConstants.ATTR_DEBUG_NAME, gdbStr);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,CommandTab.getAttributeValueFromString(fPrgmArgumentsComboInit.getItem(0)));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH,externaltools_openocd_path);

		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_OPENOCD_BIN_PATH, openocd_bin_path);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH,externaltools_ashling_path);
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_ASHLING_XML_PATH,Ashling_xml_path);
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH,externaltools_nsim_path);
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,getAttributeValueFromString(fPrgmArgumentsComboInittext));
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_OPENOCD_DEFAULT,getAttributeValueFromString(fLaunchexternal_openocd_Buttonboolean));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_ASHLING_DEFAULT,getAttributeValueFromString(fLaunchexternal_ashling_Buttonboolean));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIM_DEFAULT,getAttributeValueFromString(fLaunchexternal_nsim_Buttonboolean));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIMTCF_DEFAULT,getAttributeValueFromString(fLaunchexternal_nsimtcf_Buttonboolean));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIMPROPS_DEFAULT,getAttributeValueFromString(fLaunchexternal_nsimprops_Buttonboolean));
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_NSIM_PROP_FILE,nSIMpropsfiles_last);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_NSIM_TCF_FILE,nSIMtcffiles_last);
		
		String hostname = fGDBServerIPAddressText.getText();
		configuration.setAttribute(
				LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS,
				getAttributeValueFromString(hostname)
		);
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

	static Group groupcom;
	static Group groupcomashling;
	static Group groupnsim;
	
	protected void createGdbserverSettingsTab( TabFolder tabFolder ) {
		// Lets set minimal width of text field to 2 inches. If more required text fields will stretch.
		final int screen_ppi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
		final int min_text_width = 2 * screen_ppi;

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
		
		GridData server_type_combo_gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		server_type_combo_gd.horizontalSpan = 4;
		server_type_combo_gd.minimumWidth = min_text_width;
		fPrgmArgumentsComboInit =new Combo(subComp, SWT.None|SWT.READ_ONLY);//1-2 and 1-3
		fPrgmArgumentsComboInit.setLayoutData(server_type_combo_gd);
		fPrgmArgumentsComboInit.add("JTAG via OpenOCD");
		fPrgmArgumentsComboInit.add("JTAG via Ashling");
		fPrgmArgumentsComboInit.add("nSIM");
		fPrgmArgumentsComboInit.add("Generic gdbserver");
		
		fPrgmArgumentsComboInit.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				Combo combo= (Combo)evt.widget;
				fGDBServerPortNumberText.getText();
				fPrgmArgumentsComboInittext = combo.getText();
						    
				if (fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via OpenOCD")) {
					if(!portnumber.equalsIgnoreCase(""))
						fGDBServerPortNumberText.setText(portnumber);
					else
						fGDBServerPortNumberText.setText(LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT);
					
					
					groupnsim.dispose();
					groupcomashling.dispose();
					
					if(createTabitemCOMBool==false)
                    {
						if (!groupcom.isDisposed())
							groupcom.dispose();

						createTabitemCOM(subComp);
					}
					groupcom.setText("JTAG via OpenOCD");
					createTabitemnSIMBool=false;
					createTabitemCOMAshlingBool=false;
					
					if(!groupcom.isVisible())
						groupcom.setVisible(true);
					
					
					
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via Ashling"))
				{
					if(!portnumber.equalsIgnoreCase(""))
						fGDBServerPortNumberText.setText(portnumber);
					else
						fGDBServerPortNumberText.setText(LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);
					
					
					groupnsim.dispose();
					groupcom.dispose();
					createTabitemnSIMBool=false;
					createTabitemCOMBool=false;
					
					if(createTabitemCOMAshlingBool==false){
						if (!groupcomashling.isDisposed())
							groupcomashling.dispose();

						createTabitemCOMAshling(subComp);
					}
						 
					groupcomashling.setText("JTAG via Ashling");
    				if(!groupcomashling.isVisible())
						groupcomashling.setVisible(true);
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("nSIM"))
				{
					
					if(!portnumber.equalsIgnoreCase(""))
						fGDBServerPortNumberText.setText(portnumber);
					else
						fGDBServerPortNumberText.setText(LaunchConfigurationConstants.DEFAULT_NSIM_PORT);
				
					if (!CommandTab.initcom.isEmpty())
						CommandTab.initcom="";

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
					
					groupcom.dispose();
					groupcomashling.dispose();
					if(createTabitemnSIMBool==false){
						if(!groupnsim.isDisposed())
						    groupnsim.dispose();
					    createTabitemnSIM(subComp);
					    
					    fLaunchPropsButton.setSelection(Boolean.parseBoolean(fLaunchexternal_nsimprops_Buttonboolean));
					    fLaunchtcfButton.setSelection(Boolean.parseBoolean(fLaunchexternal_nsimtcf_Buttonboolean));
					    
//					    if(externaltools_nsim_path.equalsIgnoreCase(""))
//					    	fPrgmArgumentsTextexternal.setText(getNsimdrvDefaultPath());
//						
//						else
//						     fPrgmArgumentsTextexternal.setText(externaltools_nsim_path);
					    
//					    if(nSIMpropsfiles_last.equalsIgnoreCase(""))
//					    {
//					    	fnSIMpropsText.setText(nSIMpropsfiles);
//					    	nSIMpropsfiles_last=nSIMpropsfiles;
//					    }
//					    	
//					    else 
//					    	fnSIMpropsText.setText(nSIMpropsfiles_last);
//					    
//					    if(nSIMtcffiles_last.equalsIgnoreCase(""))
//					    {
//					    	fnSIMtcfText.setText(nSIMtcffiles);
//					    	nSIMtcffiles_last=nSIMtcffiles;
//					    }
//					    	
//					    else 
//					    	fnSIMtcfText.setText(nSIMtcffiles_last);
					}
					groupnsim.setText("nSIM");
					createTabitemCOMBool=false;
					createTabitemCOMAshlingBool=false;
					if(!groupnsim.isVisible())
						groupnsim.setVisible(true);
					
					
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("Generic gdbserver"))
				{

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

					if(!groupcom.isDisposed())
					      groupcom.setVisible(false);
					if(!groupnsim.isDisposed())
					    groupnsim.setVisible(false);
					if(!groupcomashling.isDisposed())
						groupcomashling.setVisible(false);
					
				}

				updateLaunchConfigurationDialog();

			
			}
			});

		// GDB port label
		label = new Label(subComp, SWT.LEFT);
		label.setText(Messages.Port_number_textfield_label);
		GridData gdb_port_label_gd = new GridData();
		gdb_port_label_gd.horizontalSpan = 1;
		label.setLayoutData(gdb_port_label_gd);

		// GDB port text field
		fGDBServerPortNumberText = new Text(subComp, SWT.SINGLE | SWT.BORDER| SWT.BEGINNING);
		GridData gdb_port_text_gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdb_port_text_gd.horizontalSpan = 4;
		gdb_port_text_gd.minimumWidth = min_text_width;
		fGDBServerPortNumberText.setLayoutData(gdb_port_text_gd);
		fGDBServerPortNumberText.addModifyListener( new ModifyListener() {
			public void modifyText( ModifyEvent evt ) {
				updateLaunchConfigurationDialog();
			}
		} );

		// GDB host label
		label = new Label(subComp, SWT.LEFT);
		label.setText("Host Address:");
		GridData gdb_host_label_gd = new GridData();
		gdb_host_label_gd.horizontalSpan = 1;
		label.setLayoutData(gdb_host_label_gd);

		// GDB host text field
		fGDBServerIPAddressText = new Text(subComp, SWT.SINGLE | SWT.BORDER| SWT.BEGINNING);
		GridData gdb_host_field_gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdb_host_field_gd.horizontalSpan = 4;
		gdb_host_field_gd.minimumWidth = min_text_width;
		fGDBServerIPAddressText.setLayoutData(gdb_host_field_gd);
		fGDBServerIPAddressText.setText(LaunchConfigurationConstants.DEFAULT_GDB_HOST);
		fGDBServerIPAddressText.addModifyListener( new ModifyListener() {
			public void modifyText( ModifyEvent evt ) {
				updateLaunchConfigurationDialog();
			}
		} );
		
				

        if(createTabitemnSIMBool==false)
        	createTabitemnSIM(subComp);
        if(createTabitemCOMBool==false)
        	createTabitemCOM(subComp);
        if(createTabitemCOMAshlingBool==false)
        	createTabitemCOMAshling(subComp);
		
		
		
	}
	
private void createTabitemCOMAshling(Composite subComp) { 
	    createTabitemCOMAshlingBool=true;
	    
		groupcomashling = SWTFactory.createGroup(subComp, fPrgmArgumentsComboInit.getItem(0), 3, 5, GridData.FILL_HORIZONTAL);
		final Composite compCOM = SWTFactory.createComposite(groupcomashling, 3, 5, GridData.FILL_BOTH);
		
        // Path to Ashling binary
		fAshlingBinPath = new FileFieldEditor("fAshlingBinPath", "Ashling binary path", compCOM);
		fAshlingBinPath.setStringValue(externaltools_ashling_path);

		fAshlingBinPath.setPropertyChangeListener( new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == "field_editor_value") {
					externaltools_ashling_path = (String)event.getNewValue();
					updateLaunchConfigurationDialog();
				}
			}
		});
		
		
		// Path to Ashling XMl file
		fAshlingXMLPath = new FileFieldEditor("fAshlingXMLPath","Ashling XML File", compCOM);
		fAshlingXMLPath.setStringValue(Ashling_xml_path);

		fAshlingXMLPath.setPropertyChangeListener(new IPropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						if (event.getProperty() == "field_editor_value") {
							Ashling_xml_path = (String) event.getNewValue();
							updateLaunchConfigurationDialog();
						}
					}
				});
	}

	private void createTabitemCOM(Composite subComp) { 
		createTabitemCOMBool=true;
		
		groupcom = SWTFactory.createGroup(subComp, fPrgmArgumentsComboInit.getItem(0), 3, 5, GridData.FILL_HORIZONTAL);
		final Composite compCOM = SWTFactory.createComposite(groupcom, 3, 5, GridData.FILL_BOTH);

		GridData gd = new GridData(SWT.BEGINNING);

		// Path to OpenOCD binary
		fOpenOCDBinPath = new FileFieldEditor("fOpenOCDBinPath", "OpenOCD executable", compCOM);
		fOpenOCDBinPath.setStringValue(openocd_bin_path);
		fOpenOCDBinPath.setPropertyChangeListener( new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == "field_editor_value") {
					openocd_bin_path = (String)event.getNewValue();
					updateLaunchConfigurationDialog();
				}
			}
		});

		// Path to OpenOCD configuration file
		fOpenOCDConfigPath = new FileFieldEditor("fOpenOCDConfigPath", "OpenOCD configuration file", compCOM);
		fOpenOCDConfigPath.setStringValue(externaltools_openocd_path);
		fOpenOCDConfigPath.setPropertyChangeListener( new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == "field_editor_value") {
					externaltools_openocd_path = (String)event.getNewValue();
					updateLaunchConfigurationDialog();
				}
			}
		});

	}

	private void createTabitemnSIM(Composite subComp) { 
		createTabitemnSIMBool=true;

		groupnsim = SWTFactory.createGroup(subComp, fPrgmArgumentsComboInit.getItem(0), 3, 5, GridData.FILL_HORIZONTAL);
		final Composite compnSIM = SWTFactory.createComposite(groupnsim, 3, 5, GridData.FILL_BOTH);
		
		GridData gd = new GridData();
	
		fnSIMBinPath = new FileFieldEditor("fnSIMBinPath", "nSIM executable", compnSIM);

		fnSIMBinPath.setStringValue(externaltools_nsim_path);
		fnSIMBinPath.setPropertyChangeListener( new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == "field_editor_value") {
					externaltools_nsim_path = (String)event.getNewValue();
					updateLaunchConfigurationDialog();
				}
			}
		});
		
		fLaunchtcfButton = new Button(compnSIM,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchtcfButton.setSelection(Boolean.parseBoolean(fLaunchexternal_nsimtcf_Buttonboolean));
		gd = new GridData(SWT.BEGINNING);
		gd.horizontalSpan = 3;
		fLaunchtcfButton.setLayoutData(gd);
		fLaunchtcfButton.setText("Use TCF?");


		fnSIMTCFPath = new FileFieldEditor("fnSIMTCFPath", "nSIM TCF path", compnSIM);
		fnSIMTCFPath.setStringValue(nSIMtcffiles_last);
		fnSIMTCFPath.setPropertyChangeListener( new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == "field_editor_value") {
					nSIMtcffiles_last = (String)event.getNewValue();
					updateLaunchConfigurationDialog();
				}
			}
		});
		fnSIMTCFPath.setEnabled(Boolean.parseBoolean(fLaunchexternal_nsimtcf_Buttonboolean), compnSIM);
		
		
		fLaunchPropsButton = new Button(compnSIM,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchPropsButton.setSelection(Boolean.parseBoolean(fLaunchexternal_nsimprops_Buttonboolean));
		gd = new GridData(SWT.BEGINNING);
		gd.horizontalSpan = 3;
		fLaunchPropsButton.setLayoutData(gd);
		fLaunchPropsButton.setText("Use nSIM properties file?");
		fnSIMPropsPath = new FileFieldEditor("fnSIMPropsPath", "nSIM properties file", compnSIM);
		fnSIMPropsPath.setStringValue(nSIMpropsfiles_last);
		fnSIMPropsPath.setPropertyChangeListener( new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == "field_editor_value") {
					nSIMpropsfiles_last = (String)event.getNewValue();
					updateLaunchConfigurationDialog();
				}
			}
		});

		fnSIMPropsPath.setEnabled(Boolean.parseBoolean(fLaunchexternal_nsimprops_Buttonboolean), compnSIM);

		fLaunchtcfButton.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent event) {
				if (fLaunchtcfButton.getSelection()==true) {
					fLaunchexternal_nsimtcf_Buttonboolean="true";
					fnSIMTCFPath.setEnabled(true, compnSIM);

				} else {
					fLaunchexternal_nsimtcf_Buttonboolean="false";
					fLaunchtcfButton.setSelection(false);
					fnSIMTCFPath.setEnabled(false, compnSIM);
				}
	        	updateLaunchConfigurationDialog();
	        }
	        public void widgetDefaultSelected(SelectionEvent event) {
	        }

	      });
		fLaunchPropsButton.addSelectionListener(new SelectionListener() {

	        public void widgetSelected(SelectionEvent event) {
				if (fLaunchPropsButton.getSelection()==true) {
					fLaunchexternal_nsimprops_Buttonboolean="true";
					fnSIMPropsPath.setEnabled(true,compnSIM); 

				} else {
					fLaunchexternal_nsimprops_Buttonboolean="false"; 
		        	fnSIMPropsPath.setEnabled(false,compnSIM);
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
	protected void handlepropsBinaryBrowseButtonSelected() {
			FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
			fileDialog.setFileName(fnSIMpropsText.getText());
			String text= fileDialog.open();
			if (text != null) {
				fnSIMpropsText.setText(text);
			}
	}
	protected void handletcfBinaryBrowseButtonSelected() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
		fileDialog.setFileName(fnSIMtcfText.getText());
		String text= fileDialog.open();
		if (text != null) {
			fnSIMtcfText.setText(text);
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

