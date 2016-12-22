/*******************************************************************************
 * This program and the accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Copyright (c) 2016 Synopsys, Inc.
 *******************************************************************************/

package com.arc.embeddedcdt.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Observable;

import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;
import com.arc.embeddedcdt.dsf.utils.ConfigurationWriter;

/**
 * This class provides the content and control elements for the Gdbserver Settings tab in the
 * Debug Configurations' Debugger tab.
 */
public class DebuggerGroupContainer extends Observable{

  public static final String DEFAULT_OOCD_BIN;
  public static final String DEFAULT_OOCD_CFG;
  private Combo jtagFrequencyCombo;
  public String jtagFrequency = null;
  private String hostName = "";
  private String portNumber = "";
  private String externalToolsNsimPath = "";
  private String jitThread = "1";
  private String ashlingTdescPath = "";

  public void setAshlingTdescPath(final String ashlingTdescPath){
    this.ashlingTdescPath = ashlingTdescPath;
  }

  public String getAshlingTdescPath(){
    return ashlingTdescPath;
  }

  public void setJitThread(final String jitThread){
    this.jitThread = jitThread;
  }

  public String getJitThread(){
    return jitThread;
  }

  public void setExternalToolsNsimPath(final String externalToolsNsimPath){
    this.externalToolsNsimPath = externalToolsNsimPath;
  }

  public String getExternalToolsNsimPath(){
    return externalToolsNsimPath;
  }

  public String getPortNumber(){
    return portNumber;
  }

  public void setHostName(final String hostName){
    this.hostName = hostName;
  }

  public String getHostName(){
    return hostName;
  }

  public void initializeFrom(ConfigurationReader configurationReader){
    // Set host and IP.
    portNumber = configurationReader.getGdbServerPort();
    setHostName(configurationReader.getHostAddress());

    jtagFrequency = configurationReader.getAshlingJtagFrequency();
    if (!isJtagFrequencyComboDisposed() && !jtagFrequency.isEmpty())
      selectJtagFrequencyInCombo(jtagFrequency);
    setTextForJtagFrequencyCombo(configurationReader);

    setExternalToolsNsimPath(configurationReader.getOrDefault(
        DebuggerGroupContainer.getNsimdrvDefaultPath(), "", configurationReader.getNsimPath()));
    setJitThread(configurationReader.getNsimJitThreads());
  }

  public boolean isJtagFrequencyComboDisposed(){
    return jtagFrequencyCombo.isDisposed();
  }

  public void setTextForJtagFrequencyCombo(ConfigurationReader configurationReader){
    if (!isJtagFrequencyComboDisposed()) {
      if (configurationReader.getAshlingJtagFrequency().isEmpty())
        jtagFrequencyCombo.setText(jtagFrequencyCombo.getItem(0));
      else
        jtagFrequencyCombo.setText(jtagFrequency);
    }
  }

  public void performApply(ConfigurationWriter configurationWriter) {
    configurationWriter.setNsimPath(getExternalToolsNsimPath());
    configurationWriter.setNsimJitThreads(getJitThread());

    configurationWriter.setAshlingTDescPath(ashlingTdescPath);
  }

  public void selectJtagFrequencyInCombo(String jtagFrequency){
    int previous = jtagFrequencyCombo.indexOf(jtagFrequency);
    if (previous > -1)
        jtagFrequencyCombo.remove(previous);
    jtagFrequencyCombo.add(jtagFrequency, 0);
    jtagFrequencyCombo.select(0);
  }

  public void createJtagFrequencyCombo(Composite composite) {
    Label label = new Label(composite, SWT.LEFT);
    label.setText("JTAG frequency:");
    jtagFrequencyCombo = new Combo(composite, SWT.None);// 1-2 and 1-3

    GridData gridDataJtag = new GridData(GridData.BEGINNING);
    gridDataJtag.widthHint = 100;
    jtagFrequencyCombo.setLayoutData(gridDataJtag);

    jtagFrequencyCombo.add("100MHz");
    jtagFrequencyCombo.add("90MHz");
    jtagFrequencyCombo.add("80MHz");
    jtagFrequencyCombo.add("70MHz");
    jtagFrequencyCombo.add("60MHz");
    jtagFrequencyCombo.add("50MHz");
    jtagFrequencyCombo.add("40MHz");
    jtagFrequencyCombo.add("30MHz");
    jtagFrequencyCombo.add("25MHz");
    jtagFrequencyCombo.add("20MHz");
    jtagFrequencyCombo.add("18MHz");
    jtagFrequencyCombo.add("15MHz");
    jtagFrequencyCombo.add("12MHz");
    jtagFrequencyCombo.add("10MHz");
    jtagFrequencyCombo.add("9MHz");
    jtagFrequencyCombo.add("8MHz");
    jtagFrequencyCombo.add("7MHz");
    jtagFrequencyCombo.add("6MHz");
    jtagFrequencyCombo.add("5MHz");
    jtagFrequencyCombo.add("4MHz");
    jtagFrequencyCombo.add("3MHz");
    jtagFrequencyCombo.add("2500KHz");
    jtagFrequencyCombo.add("2000KHz");
    jtagFrequencyCombo.add("1800KHz");
    jtagFrequencyCombo.add("1500KHz");
    jtagFrequencyCombo.add("1200KHz");
    jtagFrequencyCombo.add("1000KHz");

    jtagFrequencyCombo.addModifyListener(
        new ModifyListener() {
          public void modifyText(ModifyEvent event) {
              Combo combo = (Combo) event.widget;
              jtagFrequency = combo.getText();
              setChanged();
              notifyObservers();
          }
        });
    //Setting text after adding listener to make sure jtagFreq field value is updated
    if (jtagFrequency != null) {
        if (jtagFrequencyCombo.getText().isEmpty() && jtagFrequency.isEmpty())
            jtagFrequencyCombo.setText("10MHz");
        else if (jtagFrequencyCombo.getText().isEmpty() && !jtagFrequency.isEmpty())
            jtagFrequencyCombo.setText(jtagFrequency);
    } else {
        jtagFrequencyCombo.setText("10MHz");
    }
  }

  static {
    if (isWindowsOs()) {
        DEFAULT_OOCD_BIN = getIdeRootDirPath() + "\\bin\\openocd.exe";
        DEFAULT_OOCD_CFG = getIdeRootDirPath()
                + "\\share\\openocd\\scripts\\board\\snps_em_sk.cfg";
    } else {
        String predefinedPath = getIdeBinDir();
        // Checking for OpenOCD binary presence in default path
        if (new File(predefinedPath).isDirectory()) {
            DEFAULT_OOCD_BIN = predefinedPath + "openocd";
            DEFAULT_OOCD_CFG = getIdeRootDir() + "share/openocd/scripts/board/snps_em_sk.cfg";
        } else {
            DEFAULT_OOCD_BIN = LaunchConfigurationConstants.DEFAULT_OPENOCD_BIN_PATH_LINUX;
            DEFAULT_OOCD_CFG = LaunchConfigurationConstants.DEFAULT_OPENOCD_CFG_PATH_LINUX;
        }
    }
  }

  public static boolean isWindowsOs() {
    boolean isWindowsOs = false;
    String osName = System.getProperty("os.name");
    /* This code may have local issues without "Locale.ENGLISH" specified, e.g. in Turkey,
     * "I" becomes lower case undotted "i" ("ı"), and "i" becomes upper case dotted "i" ("İ").
     * So "WINDOWS".toLowerCase().indexOf("win") will return -1 in Turkey.
     */
    if (osName != null && osName.toLowerCase(Locale.ENGLISH).indexOf("windows") > -1) {
        isWindowsOs = true;
    }
    return isWindowsOs;
  }

  /**
   * Get default path to nSIM application nsimdrv.
   */
  public static String getNsimdrvDefaultPath() {
      String nsimHome = System.getenv("NSIM_HOME");
      if (nsimHome == null)
          return "";
      else {
          String path = nsimHome + java.io.File.separator + "bin" + java.io.File.separator
                  + "nsimdrv";
          if (isWindowsOs()) {
              return path + ".exe";
          } else {
              return path;
          }
      }
  }

  private static String getIdeRootDirPath() {
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

  private static String getIdeRootDir() {
    String eclipsehome = Platform.getInstallLocation().getURL().getPath();
    File predefinedPathToDirectory = new File(eclipsehome).getParentFile();
    return predefinedPathToDirectory + File.separator;
  }

  public static String getIdeBinDir() {
    return getIdeRootDir() + "bin" + File.separator;
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration){
    ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
    configurationWriter.setGdbServerCommand(
        IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND_DEFAULT);
    configurationWriter.setGdbServerPort(
        IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT_DEFAULT);
    configurationWriter.setGdbServer(ArcGdbServer.DEFAULT_GDB_SERVER.toString());
    configurationWriter.setOpenOcdConfig(DEFAULT_OOCD_CFG);
    configurationWriter.setAshlingPath("");
    configurationWriter.setNsimPath("");
    configurationWriter.setDoLaunchTerminal(false);
    configurationWriter.setNsimDefaultPath(getNsimdrvDefaultPath());
    configurationWriter.setOpenOcdPath(DEFAULT_OOCD_BIN);
    configurationWriter.setAshlingJtagFrequency("");
    configurationWriter.setFtdiDevice(LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE_NAME);
    configurationWriter.setFtdiCore(LaunchConfigurationConstants.DEFAULT_FTDI_CORE_NAME);
  }

}
