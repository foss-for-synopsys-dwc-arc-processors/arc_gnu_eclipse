/*******************************************************************************
 * This program and the accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Copyright (c) 2016 Synopsys, Inc.
 *******************************************************************************/

package com.arc.embeddedcdt.dsf.utils;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import com.arc.embeddedcdt.LaunchConfigurationConstants;

/*
 * Utility class with convenient methods for setting options' values for launch configuration.
 *
 * Instead of calling launchConfiguration.setAttribute() and specifying the attribute to be used as
 * a key every time methods from this class can be used.
 */
public class ConfigurationWriter {

  private final ILaunchConfigurationWorkingCopy lc;

  public ConfigurationWriter(final ILaunchConfigurationWorkingCopy lc) {
    this.lc = lc;
  }

  private void setAttribute(final String attribute, final String defaultValue) {
    lc.setAttribute(attribute, defaultValue);
  }

  private void setAttribute(final String attribute, final boolean defaultValue) {
    lc.setAttribute(attribute, defaultValue);
  }

  private void setAttribute(final String attribute, final int defaultValue) {
    lc.setAttribute(attribute, defaultValue);
  }

  public void setProgramName(final String value) {
    setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, value);
  }

  public void setTimeStamp(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_TIMESTAMP, value);
  }

  public void setFileFormatVersion(final int value) {
    setAttribute(LaunchConfigurationConstants.ATTR_FILE_FORMAT_VERSION, value);
  }

  public void setGdbPath(final String value) {
    setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, value);
  }

  public void setOpenOcdPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_OPENOCD_BIN_PATH, value);
  }

  public void setOpenOcdConfig(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH, value);
  }

  public void setHostAddress(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS, value);
  }

  public void setGdbServerPort(final String value) {
    setAttribute(IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT, value);
  }

  public void setComPort(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT, value);
  }

  public void setLaunchTerminal(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, value);
  }

  public void setNsimUseDefaultDirectory(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_USE_DEFAULT_DIRECTORY, value);
  }

  public void setNsimUseNsimHostLink(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMHOSTLINK, value);
  }

  public void setGdbServer(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, value);
  }

  public void setLoadElf(boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_LOAD_ELF, value);
  }

  public void setNsimPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH, value);
  }

  public void setNsimTcfPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_NSIM_TCF_FILE, value);
  }

  public void setNsimUseTcf(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMTCF, value);
  }

  public void setNsimPropsPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_NSIM_PROP_FILE, value);
  }

  public void setNsimUseProps(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMPROPS, value);
  }

  public void setNsimUseNsimHostlink(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMHOSTLINK, value);
  }

  public void setNsimUseJit(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMJIT, value);
  }

  public void setNsimJitThreads(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMJITTHREAD, value);
  }

  public void setNsimSimulateMemoryExceptions(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMMEMOEXPT, value);
  }

  public void setNsimSimulateExceptions(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMENABLEEXPT, value);
  }

  public void setNsimSimulateInvalidInstructionExceptions(final boolean value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMINVAINSTRUEXPT, value);
  }

  public void setNsimWorkingDirectoryPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_WORKING_DIRECTORY, value);
  }

  public void setAshlingPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH, value);
  }

  public void setAshlingXmlPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_ASHLING_XML_PATH, value);
  }

  public void setAshlingTDescPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_ASHLING_TDESC_PATH, value);
  }

  public void setAshlingJtagFrequency(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_JTAG_FREQUENCY, value);
  }

  public void setCustomGdbServerPath(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_CUSTOM_GDBSERVER_BIN_PATH, value);
  }

  public void setCustomGdbServerArgs(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_CUSTOM_GDBSERVER_COMMAND, value);
  }

  public void setStopAtMain(final boolean value) {
    setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, value);
  }

  public void setStopSymbol(final String value) {
    setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL, value);
  }

  public void setUserInitCommands(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, value);
  }

  public void setUserRunCommands(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN, value);
  }

  public void setFtdiDevice(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_FTDI_DEVICE, value);
  }

  public void setFtdiCore(final String value) {
    setAttribute(LaunchConfigurationConstants.ATTR_FTDI_CORE, value);
  }

  public void setGdbServerCommand(final String value) {
    setAttribute(IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND, value);
  }

  public void setDoLaunchTerminal(final boolean value){
    setAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, value);
  }
}

