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
@SuppressWarnings("restriction")
public class RemoteGDBDebuggerPage extends GDBDebuggerPage {

	//protected Text fGDBServerCommandText;
	protected Combo fPrgmArgumentsComboInit;//this variable for select which externally tools
	protected static  Text fPrgmArgumentsTextInit;// this variable for showing  which target is be selected
	public static String  fPrgmArgumentsComboInittext=null; //this variable is for getting user's input initial command
	protected Text fGDBServerPortNumberText;
	protected Text fGDBServerIPAddressText;
	protected Button fSearchexternalButton;//this button is for searching the path for external tools
	protected Label fSearchexternalLabel;
	protected Text fPrgmArgumentsTextexternal;//this button is for searching the path for external tools
	private FileFieldEditor fOpenOCDBinPath; // Editor for path to OpenOCD binary
	
    static String runcom="";//this variable is for saving user's input run command
	public String external_openocd_path="";//this variable is for saving user's external path
	public String external_ashling_path="";//this variable is for saving user's external path
	public String external_nsim_path="";//this variable is for saving user's external path
	private String openocd_bin_path;
	static String fLaunchexternal_openocd_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	static String fLaunchexternal_ashling_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	static String fLaunchexternal_nsim_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	
	public Boolean createTabitemCOMBool=false;
	public Boolean createTabitemnSIMBool=false;
	
	public Boolean createTabitemCOMAshlingBool=false;
	// Constants
	public static final String ASHLING_DEFAULT_PATH_WINDOWS = "C:\\AshlingOpellaXDforARC";
	public static final String ASHLING_DEFAULT_PATH_LINUX = "/usr/bin";
	
	protected Label nSIMpropslabel;
	public static Text fnSIMpropsText;
	protected Button fnSIMpropslButton;//this button is for browsing the prop files for nSIM
	public static String nSIMpropsfiles="";
	public static String nSIMpropsfiles_last="";//this variable is for launching the exactly com port chosen by users
	protected Button fLaunchPropsButton;//this button is for launching the TCF for nsim
	static String fLaunchexternal_nsimprops_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	protected Button fLaunchtcfButton;//this button is for launching the Properties file for nsim
	protected Label nSIMtcflabel;
	public static Text fnSIMtcfText;
	protected Button fnSIMtcfButton;//this button is for browsing the tcf files for nSIM
	public static String nSIMtcffiles="";
	public static String nSIMtcffiles_last="";//this variable is for launching the exactly com port chosen by users
	static String fLaunchexternal_nsimtcf_Buttonboolean="true";//this variable is to get external tools current status (Enable/disable)
	
	public static String externaltools="";
	public static String externaltools_openocd_path="";
	public static String externaltools_ashling_path="";
	public static String externaltools_nsim_path="";
	
	public static String portnumber="";
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
		    externaltools_openocd_path=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH, "");
		    openocd_bin_path = configuration.getAttribute(
		    	LaunchConfigurationConstants.ATTR_DEBUGGER_OPENOCD_BIN_PATH,
		    	getOpenOCDExecutableDefaultPath());
		    externaltools_ashling_path=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH, "");
		    externaltools_nsim_path=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH, "");
		    
		    fLaunchexternal_openocd_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_OPENOCD_DEFAULT, "true");
		    fLaunchexternal_ashling_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_ASHLING_DEFAULT, "true");
		    fLaunchexternal_nsim_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIM_DEFAULT, "true");
		    fLaunchexternal_nsimprops_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIMPROPS_DEFAULT, "false");
		    fLaunchexternal_nsimtcf_Buttonboolean=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_NSIMTCF_DEFAULT, "false");
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
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH,external_openocd_path);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_OPENOCD_BIN_PATH, openocd_bin_path);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH,external_ashling_path);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH,external_nsim_path);
		
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
						if (externaltools_openocd_path.equalsIgnoreCase(""))
							fPrgmArgumentsTextexternal.setText(getOpenOCDScriptDefaultPath());
						else
							fPrgmArgumentsTextexternal.setText(externaltools_openocd_path);
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
						String defaultValue = isWindowsOS() ? ASHLING_DEFAULT_PATH_WINDOWS : ASHLING_DEFAULT_PATH_LINUX;
						
						if(externaltools_ashling_path.equalsIgnoreCase(""))
							fPrgmArgumentsTextexternal.setText(defaultValue);
						else 
							fPrgmArgumentsTextexternal.setText(externaltools_ashling_path);
						
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
					    if(externaltools_nsim_path.equalsIgnoreCase(""))
					    	fPrgmArgumentsTextexternal.setText(getNsimdrvDefaultPath());
						
						else
						     fPrgmArgumentsTextexternal.setText(externaltools_nsim_path);
					    
					    if(nSIMpropsfiles_last.equalsIgnoreCase(""))
					    {
					    	fnSIMpropsText.setText(nSIMpropsfiles);
					    	nSIMpropsfiles_last=nSIMpropsfiles;
					    }
					    	
					    else 
					    	fnSIMpropsText.setText(nSIMpropsfiles_last);
					    
					    if(nSIMtcffiles_last.equalsIgnoreCase(""))
					    {
					    	fnSIMtcfText.setText(nSIMtcffiles);
					    	nSIMtcffiles_last=nSIMtcffiles;
					    }
					    else 
					    	fnSIMtcfText.setText(nSIMtcffiles_last);
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
		groupcomashling = SWTFactory.createGroup(subComp, fPrgmArgumentsComboInit.getItem(0), 5, 5, GridData.FILL_HORIZONTAL);
		Composite compCOM = SWTFactory.createComposite(groupcomashling, 5, 5, GridData.FILL_BOTH);
		
	
		fSearchexternalLabel=new Label(compCOM, SWT.LEFT);
		fSearchexternalLabel.setText("Ashling Path");
		GridData gd = new GridData();
		fSearchexternalLabel.setLayoutData(gd);
			
		fPrgmArgumentsTextexternal=new Text(compCOM, SWT.SINGLE | SWT.BORDER);//6-1
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint=400;
		gd.horizontalSpan =2;
		fPrgmArgumentsTextexternal.setLayoutData(gd);
		fPrgmArgumentsTextexternal.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				external_ashling_path=fPrgmArgumentsTextexternal.getText();
				updateLaunchConfigurationDialog();
			}
		});
		String defaultValue = isWindowsOS() ? ASHLING_DEFAULT_PATH_WINDOWS : ASHLING_DEFAULT_PATH_LINUX;
		
		if(externaltools_ashling_path.equalsIgnoreCase(""))
			fPrgmArgumentsTextexternal.setText(defaultValue);
		else 
			fPrgmArgumentsTextexternal.setText(externaltools_ashling_path);
		
		
		
		
		fSearchexternalButton = createPushButton(compCOM, "Browse", null); //$NON-NLS-1$  //6-2
		gd = new GridData(SWT.BEGINNING);
		gd.horizontalSpan =2;
		fSearchexternalButton.setLayoutData(gd);
		fSearchexternalButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
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

		fSearchexternalLabel=new Label(compCOM, SWT.LEFT);
		fSearchexternalLabel.setText("OpenOCD configuration");
		gd = new GridData();
		fSearchexternalLabel.setLayoutData(gd);
			
		fPrgmArgumentsTextexternal=new Text(compCOM, SWT.SINGLE | SWT.BORDER);//6-1
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint=400;
		fPrgmArgumentsTextexternal.setLayoutData(gd);
		fPrgmArgumentsTextexternal.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				external_openocd_path=fPrgmArgumentsTextexternal.getText();
				updateLaunchConfigurationDialog();
			}
		});
		if(externaltools_openocd_path.equalsIgnoreCase(""))
		     fPrgmArgumentsTextexternal.setText(getOpenOCDScriptDefaultPath());
		else  fPrgmArgumentsTextexternal.setText(externaltools_openocd_path);
		
		
		fSearchexternalButton = createPushButton(compCOM, "Browse", null); //$NON-NLS-1$  //6-2
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fSearchexternalButton.setLayoutData(gd);
		fSearchexternalButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});
	}

	private void createTabitemnSIM(Composite subComp) { 
		createTabitemnSIMBool=true;

		groupnsim = SWTFactory.createGroup(subComp, fPrgmArgumentsComboInit.getItem(0), 5, 5, GridData.FILL_HORIZONTAL);
		Composite compnSIM = SWTFactory.createComposite(groupnsim, 5, 5, GridData.FILL_BOTH);
		
		fSearchexternalLabel=new Label(compnSIM, SWT.LEFT);
		fSearchexternalLabel.setText("nSIM Path");
		GridData gd = new GridData();
		fSearchexternalLabel.setLayoutData(gd);
		
		fPrgmArgumentsTextexternal=new Text(compnSIM, SWT.SINGLE | SWT.BORDER);//6-1
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint=400;
		gd.horizontalSpan =3;
		fPrgmArgumentsTextexternal.setLayoutData(gd);
		fPrgmArgumentsTextexternal.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				external_nsim_path=fPrgmArgumentsTextexternal.getText();
				updateLaunchConfigurationDialog();
			}
		});
		
		if(externaltools_nsim_path.equalsIgnoreCase(""))
	    	fPrgmArgumentsTextexternal.setText(getNsimdrvDefaultPath());
		
		else
		    	fPrgmArgumentsTextexternal.setText(externaltools_nsim_path);
		
		fSearchexternalButton = createPushButton(compnSIM, "Browse", null); //$NON-NLS-1$  //6-2
		gd = new GridData(SWT.BEGINNING);
		fSearchexternalButton.setLayoutData(gd);
		fSearchexternalButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});
	
//		nSIMtcflabel = new Label(compnSIM, SWT.CENTER);
//		nSIMtcflabel.setText("nSIM TCF:");
		fLaunchtcfButton = new Button(compnSIM,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchtcfButton.setSelection(false);
		gd = new GridData(SWT.BEGINNING);
		fLaunchtcfButton.setLayoutData(gd);
		fLaunchtcfButton.setText("TCF");
	
		fnSIMtcfText = new Text(compnSIM, SWT.SINGLE | SWT.BORDER| SWT.BEGINNING);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint=400;
        gd.horizontalSpan=2;
        fnSIMtcfText.setLayoutData(gd);
        
	    if(nSIMtcffiles_last.equalsIgnoreCase(""))
	    {
	    	fnSIMtcfText.setText(nSIMtcffiles);
	    	nSIMtcffiles_last=nSIMtcffiles;
	    	
	    }
	    else 
	    	fnSIMtcfText.setText(nSIMtcffiles_last);
	    fnSIMtcfText.addModifyListener( new ModifyListener() {

			public void modifyText( ModifyEvent evt ) {
				nSIMtcffiles_last=fnSIMtcfText.getText();
				updateLaunchConfigurationDialog();
			}
		} );
		
	    fnSIMtcfButton = createPushButton(compnSIM, "Browse", null); //$NON-NLS-1$  //6-2
	    gd = new GridData(SWT.BEGINNING);
        gd.horizontalSpan=2;
        fnSIMtcfButton.setLayoutData(gd);
	
	    fnSIMtcfButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handletcfBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});
	    
//		nSIMpropslabel = new Label(compnSIM, SWT.CENTER);
//		nSIMpropslabel.setText("nSIM Props:");
	
		fLaunchPropsButton = new Button(compnSIM,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchPropsButton.setSelection(false);
		gd = new GridData(SWT.BEGINNING);
		fLaunchPropsButton.setLayoutData(gd);
		fLaunchPropsButton.setText("Properties file");
		
		fnSIMpropsText = new Text(compnSIM, SWT.SINGLE | SWT.BORDER| SWT.BEGINNING);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint=400;
        gd.horizontalSpan=2;
        fnSIMpropsText.setLayoutData(gd);
	    if(nSIMpropsfiles_last.equalsIgnoreCase(""))
		    fnSIMpropsText.setText(nSIMpropsfiles);
	    else 
	    	fnSIMpropsText.setText(nSIMpropsfiles_last);
		fnSIMpropsText.addModifyListener( new ModifyListener() {

			public void modifyText( ModifyEvent evt ) {
				nSIMpropsfiles_last=fnSIMpropsText.getText();
				updateLaunchConfigurationDialog();
			}
		} );
		
		fnSIMpropslButton = createPushButton(compnSIM, "Browse", null); //$NON-NLS-1$  //6-2
		gd = new GridData(SWT.BEGINNING);
        gd.horizontalSpan=2;
        fnSIMpropslButton.setLayoutData(gd);
		fnSIMpropslButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handlepropsBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});

		fLaunchtcfButton.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent event) {
				if (fLaunchtcfButton.getSelection()==true) {
					fLaunchexternal_nsimtcf_Buttonboolean="true";
					fLaunchtcfButton.setSelection(true);
		        	fnSIMtcfText.setEnabled(true); 
		        	fnSIMtcfButton.setEnabled(true);

				} else {
					fLaunchexternal_nsimtcf_Buttonboolean="false";
					fLaunchtcfButton.setSelection(false);
		        	fnSIMtcfText.setEnabled(false);
		        	fnSIMtcfButton.setEnabled(false);
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
					fLaunchPropsButton.setSelection(true);
		        	fnSIMpropsText.setEnabled(true); 
		        	fnSIMpropslButton.setEnabled(true);

				} else {
					fLaunchexternal_nsimprops_Buttonboolean="false";
					fLaunchPropsButton.setSelection(false);
		        	fnSIMpropsText.setEnabled(false); 
		        	fnSIMpropslButton.setEnabled(false);
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

