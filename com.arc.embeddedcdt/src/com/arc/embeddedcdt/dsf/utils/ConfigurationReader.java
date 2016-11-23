/*******************************************************************************
 * This program and the accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at http:/*
 * www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf.utils;

import java.util.Locale;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;

/**
 * Utility class with convenient methods for getting options' values from launch configuration.
 *
 * Instead of calling launchConfiguration.getAttribute() surrounded with try/catch and specifying
 * the attribute to be used as a key every time methods from this class can be used.
 */
public class ConfigurationReader {

  private final ILaunchConfiguration lc;

  public ConfigurationReader(final ILaunchConfiguration lc) {
    this.lc = lc;
  }

  private String getAttribute(final String attribute, final String defaultValue) {
    try {
      return lc.getAttribute(attribute, defaultValue);
    } catch (CoreException e) {
      StatusManager.getManager().handle(e, "com.arc.embeddedcdt");
      return defaultValue;
    }
  }

  private boolean getAttribute(final String attribute, final boolean defaultValue) {
    try {
      return lc.getAttribute(attribute, defaultValue);
    } catch (CoreException e) {
      StatusManager.getManager().handle(e, "com.arc.embeddedcdt");
      return defaultValue;
    }
  }

  private int getAttribute(final String attribute, final int defaultValue) {
    try {
      return lc.getAttribute(attribute, defaultValue);
    } catch (CoreException e) {
      StatusManager.getManager().handle(e, "com.arc.embeddedcdt");
      return defaultValue;
    }
  }

  public String getProgramName() {
    return getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, "");
  }

  public String getTimeStamp() {
    return getAttribute(LaunchConfigurationConstants.ATTR_TIMESTAMP, "");
  }

  public int getFileFormatVersion() {
    return getAttribute(LaunchConfigurationConstants.ATTR_FILE_FORMAT_VERSION,
        LaunchConfigurationConstants.UNREAL_FILE_FORMAT_VERSION);
  }

  public String getGdbPath() {
    return getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, "");
  }

  public String getOpenOcdPath() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_OPENOCD_BIN_PATH, "");
  }

  public String getOpenOcdConfig() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH, "");
  }

  public String getHostAddress() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS,
        LaunchConfigurationConstants.DEFAULT_GDB_HOST);
  }

  public String getGdbServerPort() {
    final ArcGdbServer gdbServer = getGdbServer();
    String defaultValue = "";
    switch (gdbServer) {
      case JTAG_OPENOCD:
        defaultValue = LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT;
        break;
      case JTAG_ASHLING:
        defaultValue = LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT;
        break;
      case NSIM:
        defaultValue = LaunchConfigurationConstants.DEFAULT_NSIM_PORT;
        break;
      default:
    }
    return getAttribute(IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT, defaultValue);
  }

  public String getComPort() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT, "");
  }

  public boolean doLaunchTerminal() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, true);
  }

  public boolean getNsimUseDefaultDirectory() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_USE_DEFAULT_DIRECTORY,
        true);
  }

  public ArcGdbServer getGdbServer() {
    return ArcGdbServer
        .fromString(getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,
            ArcGdbServer.DEFAULT_GDB_SERVER.toString()));
  }

  public String getNsimPath() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH, "");
  }

  public String getNsimTcfPath() {
    return getAttribute(LaunchConfigurationConstants.ATTR_NSIM_TCF_FILE, "");
  }

  public boolean getNsimUseTcf() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMTCF, true);
  }

  public String getNsimPropsPath() {
    return getAttribute(LaunchConfigurationConstants.ATTR_NSIM_PROP_FILE, "");
  }

  public boolean getNsimUseProps() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMPROPS, true);
  }

  public boolean getNsimUseNsimHostlink() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMHOSTLINK, true);
  }

  public boolean getNsimUseJit() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMJIT, false);
  }

  public String getNsimJitThreads() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMJITTHREAD, "1");
  }

  public boolean getNsimSimulateMemoryExceptions() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMMEMOEXPT, true);
  }

  public boolean getNsimSimulateExceptions() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMENABLEEXPT, true);
  }

  public boolean getNsimSimulateInvalidInstructionExceptions() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMINVAINSTRUEXPT, true);
  }

  public String getNsimWorkingDirectoryPath() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_WORKING_DIRECTORY, "");
  }

  public String getAshlingPath() {
    /* This code may have local issues without "Locale.ENGLISH" specified, e.g. in Turkey,
     * "I" becomes lower case undotted "i" ("ı"), and "i" becomes upper case dotted "i" ("İ").
     * So "WINDOWS".toLowerCase().indexOf("win") will return -1 in Turkey.
     */
    String defaultValue =
        System.getProperty("os.name").toLowerCase(Locale.ENGLISH).indexOf("windows") > -1
            ? LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_WINDOWS
            : LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_LINUX;
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH,
        defaultValue);
  }

  public String getAshlingXmlPath() {
    return getAttribute(LaunchConfigurationConstants.ATTR_ASHLING_XML_PATH, "");
  }

  public String getAshlingTDescPath() {
    return getAttribute(LaunchConfigurationConstants.ATTR_ASHLING_TDESC_PATH, "");
  }

  public String getAshlingJtagFrequency() {
    return getAttribute(LaunchConfigurationConstants.ATTR_JTAG_FREQUENCY, "");
  }

  public String getCustomGdbServerPath() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_CUSTOM_GDBSERVER_BIN_PATH, "");
  }

  public String getCustomGdbServerArgs() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_CUSTOM_GDBSERVER_COMMAND, "");
  }

  public boolean doStopAtMain() {
    return getAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, true);
  }

  public String getStopSymbol() {
    return getAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL,
        ICDTLaunchConfigurationConstants.DEBUGGER_STOP_AT_MAIN_SYMBOL_DEFAULT);
  }

  public String getUserInitCommands() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, "");
  }

  public String getUserRunCommands() {
    return getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN, "");
  }

  public FtdiDevice getFtdiDevice() {
    try {
      return FtdiDevice.valueOf(getAttribute(LaunchConfigurationConstants.ATTR_FTDI_DEVICE,
          LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE_NAME));
    } catch (IllegalArgumentException e) {
      return LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE;
    }
  }

  public FtdiCore getFtdiCore() {
    try {
      return FtdiCore.valueOf(getAttribute(LaunchConfigurationConstants.ATTR_FTDI_CORE,
          LaunchConfigurationConstants.DEFAULT_FTDI_CORE_NAME));
    } catch (IllegalArgumentException e) {
      return LaunchConfigurationConstants.DEFAULT_FTDI_CORE;
    }
  }

  public <T> T getOrDefault(final T defaultValue,final T empty,final T actual){
    if (actual.equals(empty))
      return defaultValue;
    return actual;
  }
}
