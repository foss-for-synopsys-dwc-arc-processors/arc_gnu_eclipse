/*******************************************************************************
 * This program and the accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Copyright (c) 2016 Synopsys, Inc.
 *******************************************************************************/

package com.arc.embeddedcdt.common;

import java.util.HashSet;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.dsf.utils.Configuration;

/**
 * This class is intended for checking the debugger's file format version and notifying user if his
 * debug configuration has incompatible changes in comparison with this debugger plug-in's launch
 * configurations. It makes user aware of possible reason of his problems if they appear.
 *
 * It retrieves a launch configuration's file format version from the
 * $WORKSPACE/.metadata/.plugins/org.eclipse.debug.core/.launches/$CONFIGURATION_NAME, warns user
 * when launching, loading to GUI (once per project during a one IDE launch) a debug configuration
 * created with an incompatible debugger's file format version.
 */
public class LaunchFileFormatVersionChecker {

  // It is used to avoid duplicating warnings for the same project during a one IDE launch.
  private final HashSet<String> seenTimestamps = new HashSet<>();

  private static LaunchFileFormatVersionChecker INSTANCE = null;

  private LaunchFileFormatVersionChecker() {}

  public synchronized static LaunchFileFormatVersionChecker getInstance() {
    if (INSTANCE == null){
      INSTANCE = new LaunchFileFormatVersionChecker();
    }
    return INSTANCE;
  }

  public void check(final ILaunchConfiguration launchCfg) {
    final String timestamp = Configuration.getTimeStamp(launchCfg);
    if (!seenTimestamps.contains(timestamp)) {
      seenTimestamps.add(timestamp);
      final int cfgFileVersion = Configuration.getFileFormatVersion(launchCfg);
      if (cfgFileVersion != LaunchConfigurationConstants.CURRENT_FILE_FORMAT_VERSION) {
        warnUser(launchCfg, LaunchConfigurationConstants.CURRENT_FILE_FORMAT_VERSION,
            cfgFileVersion);
      }
    }
  }

  private void warnUser(final ILaunchConfiguration launchCfg, final int currentFileVersion,
      final int cfgFileVersion) {
    final String warningMsg =
        cfgFileVersion == LaunchConfigurationConstants.UNREAL_FILE_FORMAT_VERSION
            ? String.format("Compatibility issues are possible.\n"
                + "Your launch configuration's file format version is %d, but this launch "
                + "configuration was created with a debugger plug-in with an older file format"
                + " version.", currentFileVersion)
            : String.format("Compatibility issues are possible.\n"
                + "Your launch configuration's file format version is %d, but this launch "
                + "configuration was created with a debugger plug-in with the file format "
                + "version %d.\n", currentFileVersion, cfgFileVersion);

    final boolean isHeadless = System.getProperty("eclipse.application")
        .equals("org.eclipse.cdt.managedbuilder.core.headlessbuild");
    if (isHeadless) {
      System.err.println(warningMsg);
    } else {
      createMarker(launchCfg, warningMsg);
    }
  }

  /**
  * This method makes the warning appear in the Problems view.
  * If workspace does not exist, a <tt>CoreException</tt> occurs and is reported to the Eclipse's
  * Error view and error log.
  */
  private void createMarker(final ILaunchConfiguration launchCfg, final String warningMsg) {
    try {
      final IMarker marker = ResourcesPlugin.getWorkspace().getRoot().createMarker(IMarker.PROBLEM);
      marker.setAttribute(IMarker.MESSAGE, warningMsg);
      marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
      marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
      marker.setAttribute(IMarker.LOCATION, launchCfg.getName());
      marker.setAttribute(IMarker.TRANSIENT, true);
    } catch (CoreException e) {
      StatusManager.getManager().handle(e, "com.arc.embeddedcdt");
    }
  }

}
