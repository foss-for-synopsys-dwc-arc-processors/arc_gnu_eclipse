/*******************************************************************************
 * Copyright (c) 2013, 2015 Synopsys, Inc.  All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.launch;

import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.internal.ui.launch.CApplicationLaunchShortcut;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.gui.FirstlaunchDialog;
import com.arc.embeddedcdt.gui.RemoteGDBDebuggerPage;


/**
 * Using "Run As" --> "Node Application" or "Run As" --> "coffee" will lead here
 **/
@SuppressWarnings("restriction")
public class LaunchShortcut extends CApplicationLaunchShortcut implements ILaunchShortcut {


	public void startrunas() {
		IWorkbench workbench = PlatformUI.getWorkbench();

        if (workbench.getDisplay().getThread() != Thread.currentThread()){
            // Note that we do the work synchronously so that we can lock this thread when getting null gdbserver value. It is used
            // to launch Debug As/ Run as pop up window for the first time launching.
            workbench.getDisplay().syncExec(new Runnable(){
                @Override
                public void run () {
                	startrunas();
                }});
            return;
        }
		
		// Assertion: we're in the UI thread.
		final IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		IWorkbenchWindow window=activePage.getWorkbenchWindow();
		if (window != null) {
			Shell parent = window.getShell();
			//MessageDialog.openQuestion(parent,	"The first time launch","Need to create Debug configuration for the first launch");
			FirstlaunchDialog dlg=new FirstlaunchDialog(parent);
			dlg.open();
			System.out.println("gdbserver: \""+dlg.value[0]+"\" COM serial port: \""+dlg.value[1]+"\"");
    	}
	}
	
	@Override
	protected ILaunchConfiguration findLaunchConfiguration(IBinary bin, String mode) {
	    ILaunchConfiguration lc = super.findLaunchConfiguration(bin, mode);
	    return editConfiguration(lc, bin);
	}
  /**
   * Method createConfiguration.
   * @param bin
   * @return ILaunchConfiguration
   */
  private ILaunchConfiguration editConfiguration(ILaunchConfiguration lc, IBinary bin) {
          ILaunchConfiguration config = null;
          try {
                  ILaunchConfigurationWorkingCopy wc = lc.getWorkingCopy();
                  wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, true);
                  wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, "com.arc.embeddedcdt.RemoteGDBDebugger");
                  wc.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, RemoteGDBDebuggerPage.getDefaultGdbPath());

                  startrunas();

                  if(!FirstlaunchDialog.value[0].equalsIgnoreCase("")) {
                      ArcGdbServer gdbServer = ArcGdbServer.fromString(FirstlaunchDialog.value[0]);
                      String gdbserver_port="";

                      switch (gdbServer) {
                      case JTAG_ASHLING:
                          gdbserver_port = LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT;
                          wc.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT,
                                  FirstlaunchDialog.value[1]);
                          break;
                      case JTAG_OPENOCD:
                          gdbserver_port = LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT;
                          wc.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT,
                                  FirstlaunchDialog.value[1]);
                          break;
                      case NSIM:
                          gdbserver_port = LaunchConfigurationConstants.DEFAULT_NSIM_PORT;
                          break;
                      case GENERIC_GDBSERVER:
                          break;
                      default:
                          throw new IllegalArgumentException("Unknown enum value has been used");
                      }

                      wc.setAttribute(IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,gdbserver_port);
                      wc.setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,FirstlaunchDialog.value[0]);
                  }
                  config = wc.doSave();

          } catch (CoreException ce) {
                  LaunchUIPlugin.log(ce);
          }
          return config;
  }

  /**
   * Method getCLaunchConfigType.
   * @return ILaunchConfigurationType
   */
  @Override
  protected ILaunchConfigurationType getCLaunchConfigType() {
          return getLaunchManager().getLaunchConfigurationType(LaunchConfigurationConstants.ID_LAUNCH_C_APP);
  }

}