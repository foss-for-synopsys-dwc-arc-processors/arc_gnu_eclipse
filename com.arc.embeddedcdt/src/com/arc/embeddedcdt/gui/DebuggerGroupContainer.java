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

import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.dsf.utils.ConfigurationWriter;

/**
 * This class provides the content and control elements for the Gdbserver Settings tab in the
 * Debug Configurations' Debugger tab.
 */
public class DebuggerGroupContainer {

  public static final String DEFAULT_OOCD_BIN;
  public static final String DEFAULT_OOCD_CFG;

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
