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



import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;





import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.launch.ui.CLaunchConfigurationTab;
import org.eclipse.cdt.launch.ui.ICDTLaunchHelpContextIds;
import org.eclipse.cdt.ui.CElementLabelProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.LaunchImages;
import com.arc.embeddedcdt.launch.Launch;
import com.arc.embeddedcdt.launch.WinRegistry;
import com.arc.embeddedcdt.proxy.cdt.LaunchMessages;


/**
 * @author User
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommandTab extends CLaunchConfigurationTab {

	protected Label fPrgmArgumentsLabelInit;
	protected Label fPrgmArgumentsLabelRun; //this variable is for showing run  command
	protected Text fPrgmArgumentsTextRun;   //this variable is for getting user's input run command
	static String initcom="";//this variable is for saving user's input initial command
    static String runcom="";//this variable is for saving user's input run command
    protected static  Text fPrgmArgumentsTextInit;// this variable for showing  which target is be selected
	/*
	protected Combo fPrgmArgumentsComboInit;//this variable for select which externally tools
	
	protected Label fPrgmArgumentsLabelCom;//this variable is for showing COM port
	protected Combo fPrgmArgumentsComCom;//this variable is for getting user's input COM port
	public static String  fPrgmArgumentsComboInittext=null; //this variable is for getting user's input initial command
    
    static String externalpath="";//this variable is for saving user's external path

    public static String comport=null;//this variable is for launching the exactly com port chosen by users
    protected Button fSearchexternalButton;//this button is for searching the path for external tools
    protected Button fLaunchComButton;//this variable is for launching COM port
    protected Button fLaunchernalButton;//this button is for launching the external tools
    protected Text fPrgmArgumentsTextexternal;//this button is for searching the path for external tools
    static String fLaunchexternalButtonboolean="true";//this variable is to get external tools current status (Enable/disable)
    static String fLaunchputtyboolean="true";//this variable is to get external tools current status (Enable/disable)*/
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
		createCommandsComponent(comp, 1);
	}


	protected void createCommandsComponent(Composite comp, int i) {
		Composite argsComp = new Composite(comp, SWT.NONE);
		GridLayout projLayout = new GridLayout();
		projLayout.numColumns = 1;
		projLayout.marginHeight = 0;
		projLayout.marginWidth = 0;
		argsComp.setLayout(projLayout);		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = i;
		argsComp.setLayoutData(gd);

		
		fPrgmArgumentsLabelInit = new Label(argsComp, SWT.NONE);//1-1 
		fPrgmArgumentsLabelInit.setText("'Initialize' commands"); //$NON-NLS-1$
		fPrgmArgumentsTextInit = new Text(argsComp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);//2-1
		fPrgmArgumentsTextInit.setLayoutData(gd);
		fPrgmArgumentsTextInit.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				if (initcom.equalsIgnoreCase("")){
			  	initcom=fPrgmArgumentsTextInit.getText();
				}
				updateLaunchConfigurationDialog();
				}
			});
		/*fPrgmArgumentsComboInit =new Combo(argsComp, SWT.None);//1-2 and 1-3
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 80;
		gd.horizontalSpan =2;
		fPrgmArgumentsComboInit.setLayoutData(gd);
		fPrgmArgumentsComboInit.add("JTAG via OpenOCD");
		fPrgmArgumentsComboInit.add("JTAG via Ashling");
		//fPrgmArgumentsComboInit.add("nSIM");
		//fPrgmArgumentsComboInit.add("GNU simulator");
		fPrgmArgumentsComboInit.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				Combo combo= (Combo)evt.widget;
				fPrgmArgumentsComboInittext=combo.getText();
				if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via OpenOCD"))
				{
					fPrgmArgumentsTextexternal.setText(externalpath);
					if(!initcom.isEmpty()&&initcom.startsWith("set remotetimeout")&&!initcom.equalsIgnoreCase("set remotetimeout 15 \ntarget remote :3333 \nload")) 
						{fPrgmArgumentsTextInit.setText(initcom);}
						
					else fPrgmArgumentsTextInit.setText("set remotetimeout 15 \ntarget remote :3333 \nload");
					 
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via Ashling"))
				{
					fPrgmArgumentsTextexternal.setText("C:\\AshlingOpellaXDforARC");
					if(!initcom.isEmpty()&&initcom.startsWith("set arc opella-target arcem")&&!initcom.equalsIgnoreCase("set arc opella-target arcem \ntarget remote :2331 \nload")) 
						{fPrgmArgumentsTextInit.setText(initcom);}
						
					else fPrgmArgumentsTextInit.setText("set arc opella-target arcem \ntarget remote :2331 \nload");
					 
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("nSIM"))
				{
					if(!initcom.isEmpty()&&initcom.startsWith("target remote localhost:")&&!initcom.equalsIgnoreCase("target remote localhost:1234 \r\nload")) 
					{fPrgmArgumentsTextInit.setText(initcom);}
					
					else fPrgmArgumentsTextInit.setText("target remote localhost:1234 \nload");
				}
				else if(fPrgmArgumentsComboInittext.equalsIgnoreCase("GNU simulator"))
				{
								
					fPrgmArgumentsTextInit.setText("target sim \nload");
				}
				fSearchexternalButton.setText(fPrgmArgumentsComboInittext.substring(fPrgmArgumentsComboInittext.lastIndexOf("via")+3, fPrgmArgumentsComboInittext.length())+" Path");
				fLaunchernalButton.setText("Launch "+fPrgmArgumentsComboInittext.substring(fPrgmArgumentsComboInittext.lastIndexOf("via")+3, fPrgmArgumentsComboInittext.length()));
				
				updateLaunchConfigurationDialog();
				
			
			}
			});*/
		//GridData gdebugtext = new GridData(GridData.FILL_HORIZONTAL);
		
	    //gdebugtext = new GridData();
	    //gdebugtext.heightHint = 60;gdebugtext.widthHint=400;
	    //gdebugtext.horizontalAlignment = GridData.FILL;
	    //gdebugtext.grabExcessHorizontalSpace = true;
	    //gdebugtext.horizontalSpan = 3;//2-1 and 2-2 and 2-3
		//fPrgmArgumentsTextInit.setLayoutData(gdebugtext);
		
		
		//yunlu change for debug session preset value end
		
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 25;
		fPrgmArgumentsLabelRun = new Label(argsComp, SWT.NONE);//3-1 and 3-2 and 3-3
		fPrgmArgumentsLabelRun.setText("'Run' commands"); //$NON-NLS-1$
		fPrgmArgumentsLabelRun.setLayoutData(gd);
		fPrgmArgumentsTextRun = new Text(argsComp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);//4-1 and 4-2
		//fPrgmArgumentsTextRun = new Text(argsComp, SWT.NONE);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 3;
		gd.heightHint = 50;
		fPrgmArgumentsTextRun.setLayoutData(gd);
		fPrgmArgumentsTextRun.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
			
				runcom=fPrgmArgumentsTextRun.getText();
				updateLaunchConfigurationDialog();
			}
		});
				
		/*fPrgmArgumentsComCom =new Combo(argsComp, SWT.None);//5-2 and 5-3 
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 25;
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
		fPrgmArgumentsLabelCom = new Label(argsComp, SWT.NONE);//5-1
		fPrgmArgumentsLabelCom.setText("COM  Ports"); //$NON-NLS-1$
		fLaunchComButton = new Button(argsComp,SWT.CHECK); //$NON-NLS-1$ //6-3
		fLaunchComButton.setSelection(true);
		gd = new GridData(SWT.BEGINNING);
		fLaunchComButton.setLayoutData(gd);
		fLaunchComButton.setText("Launch PuTTY");
		fLaunchComButton.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent event) {
	        	if(fLaunchComButton.getSelection()==true){
	        		fLaunchputtyboolean="true";
	        	    fPrgmArgumentsComCom.setEnabled(true);
	        	    fPrgmArgumentsLabelCom.setEnabled(true);
	        	}
	        	else {
	        		fLaunchputtyboolean="false";
		        	fPrgmArgumentsComCom.setEnabled(false);
		        	fPrgmArgumentsLabelCom.setEnabled(false);
	           	}
	        	updateLaunchConfigurationDialog();
	        }

	        public void widgetDefaultSelected(SelectionEvent event) {
	        }
	        
	      });
		
		fPrgmArgumentsTextexternal=new Text(argsComp, SWT.SINGLE | SWT.BORDER);//6-1
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint=400;

		fPrgmArgumentsTextexternal.setLayoutData(gd);
		fPrgmArgumentsTextexternal.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				externalpath=fPrgmArgumentsTextexternal.getText();
				updateLaunchConfigurationDialog();
			}
		});
		fSearchexternalButton = createPushButton(argsComp, "Externaltools Path", null); //$NON-NLS-1$  //6-2
		gd = new GridData(SWT.BEGINNING);
		fSearchexternalButton.setLayoutData(gd);
		fSearchexternalButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleBinaryBrowseButtonSelected();
				updateLaunchConfigurationDialog();
			}
		});
	
		fLaunchernalButton = new Button(argsComp,SWT.CHECK); //$NON-NLS-1$ //6-3
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
	        	fPrgmArgumentsTextexternal.setEnabled(true);
	        	}
	        	else {
		        	fLaunchexternalButtonboolean="false";
		        	fSearchexternalButton.setEnabled(false);
		        	fPrgmArgumentsTextexternal.setEnabled(false);
	           	}
	        	updateLaunchConfigurationDialog();
	        }

	        public void widgetDefaultSelected(SelectionEvent event) {
	        }
	        
	      });
		    
         */

//		addControlAccessibleListener(fArgumentVariablesButton, fArgumentVariablesButton.getText()); // need to strip the mnemonic from buttons
	

	}
	protected void handleBinarylaunchButtonSelected(){
	}
	/*protected void handleBinaryBrowseButtonSelected() {
		if(fPrgmArgumentsComboInittext.equalsIgnoreCase("JTAG via OpenOCD"))
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
	  
	}*/
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, (String) null);
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN, (String) null);
		//configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, (String) null);
		//configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH, (String) null);
		//configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_PUTTY_DEFAULT, (String) null);
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			/*String status=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT,fPrgmArgumentsComboInit.getItem(0));
			
			if(status.indexOf("JTAG")==-1&&status.indexOf("nSIM")==-1&&status.indexOf("GNU")==-1)
			{   
				if(status.startsWith("set remotetimeout 15"))
			    {
					fPrgmArgumentsComboInit.setText("JTAG via OpenOCD");
					//fSearchexternalButton.setText("Launch OpenOCD");
			    }
				else if(status.startsWith("set arc opella-target arcem"))
			    {
					fPrgmArgumentsComboInit.setText("JTAG via Ashling");
					//fSearchexternalButton.setText("Launch Ashling");
			    }
			    /*else if(status.startsWith("target remote localhost:"))
			    {
			
				fPrgmArgumentsComboInit.setText("nSIM");
			    }
			    else if(status.equalsIgnoreCase("target sim \nload"))
			    {
				fPrgmArgumentsComboInit.setText("GNU simulator");
			    }*/
			//}
			//else  fPrgmArgumentsComboInit.setText(fPrgmArgumentsComboInit.getItem(0));
			if (initcom.equalsIgnoreCase(""))	{
				fPrgmArgumentsTextInit.setText(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, "set remotetimeout 15 \ntarget remote :3333 \nload"));
				
			}
			else fPrgmArgumentsTextInit.setText(initcom);
			
			fPrgmArgumentsTextRun.setText(configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN, "b main \nc"));
	
			 
			/*String externaltools=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String());//get which external tool is in use
			 if(externaltools.lastIndexOf("via")>1&&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("true"))
			 {
				 fSearchexternalButton.setText(externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length())+" Path");
				 fLaunchernalButton.setSelection(true);//setText("Enable Launch "+externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length()));//get current status
				 fLaunchexternalButtonboolean="true";
				 fSearchexternalButton.setEnabled(true);
				 fPrgmArgumentsTextexternal.setEnabled(true);
				 fLaunchernalButton.setText("Launch "+externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length()));
				 }
			 else  if(externaltools.lastIndexOf("via")>1&&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false")) 
			 {
				 fSearchexternalButton.setText(externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length())+" Path");
				 fLaunchernalButton.setSelection(false);//fLaunchernalButton.setText("Disable Launch "+externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length()));//get current status
				 fLaunchexternalButtonboolean="false";
				 fSearchexternalButton.setEnabled(false);
				 fPrgmArgumentsTextexternal.setEnabled(false);
				 fLaunchernalButton.setText("Launch "+externaltools.substring(externaltools.lastIndexOf("via")+3, externaltools.length()));
				
			 }
			 else if (externaltools.lastIndexOf("via")<1&&!configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false"))
			 {
				 fSearchexternalButton.setText("OpenOCD Path");
				 fLaunchernalButton.setSelection(true);
				 fLaunchernalButton.setText("Launch OpenOCD");//fLaunchernalButton.setText("Enable Launch OpenOCD");
				 fSearchexternalButton.setEnabled(true);
				 fPrgmArgumentsTextexternal.setEnabled(true);
				 fLaunchexternalButtonboolean="true";
			 }
			 else if (externaltools.lastIndexOf("via")<1&&configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT, new String()).equalsIgnoreCase("false"))
			 {
				 fSearchexternalButton.setText("OpenOCD Path");
				 fLaunchernalButton.setSelection(false);
				 fLaunchernalButton.setText("Launch OpenOCD");//fLaunchernalButton.setText("Disable Launch OpenOCD");
				 fSearchexternalButton.setEnabled(false);
				 fPrgmArgumentsTextexternal.setEnabled(false);
				 fLaunchexternalButtonboolean="false";
			 }
			
			 String puttylaunch=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_PUTTY_DEFAULT, new String());//get which external tool is in use
			 if(!puttylaunch.equalsIgnoreCase("false"))
			 {
				 fLaunchComButton.setSelection(true);//setText("Enable Launch PuTTY");
				 fLaunchputtyboolean="true";
				 fPrgmArgumentsComCom.setEnabled(true);
		         fPrgmArgumentsLabelCom.setEnabled(true);
				 }
			 else if(puttylaunch.equalsIgnoreCase("false")) 
			 {
				 fLaunchComButton.setSelection(false);//fLaunchComButton.setText("Disable Launch PuTTY");
				 fLaunchputtyboolean="false";
				 fPrgmArgumentsComCom.setEnabled(false);
		        fPrgmArgumentsLabelCom.setEnabled(false);
				
			 }
			 fLaunchComButton.setText("Launch PuTTY");
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
			}*/
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT,initcom);
    	configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN,runcom);
		/*configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH,externalpath);
		//try {
		//	externalpath=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_PATH, "");
		//} catch (CoreException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT,getAttributeValueFromString(comport));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,getAttributeValueFromString(fPrgmArgumentsComboInittext));
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT,getAttributeValueFromString(fLaunchexternalButtonboolean));
		
		configuration.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_PUTTY_DEFAULT,getAttributeValueFromString(fLaunchputtyboolean));*/
	
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "Commands";
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
