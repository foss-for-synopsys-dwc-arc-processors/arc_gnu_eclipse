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
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;

/**
 * This class is intended for checking the debugger's file format version and notifying user if his
 * debug configuration has incompatible changes in comparison with this debugger plug-in's launch
 * configurations. It makes user aware of possible reason of his problems if they appear.
 *
 * Checker retrieves a launch configuration's file format version from the
 * $WORKSPACE/.metadata/.plugins/org.eclipse.debug.core/.launches/$CONFIGURATION_NAME, warns user
 * when launching, loading to GUI (once per project during a one IDE launch) a debug configuration
 * created with an incompatible debugger's file format version.
 */
public class LaunchFileFormatVersionChecker {

  /* This set is used to identify the launch configurations which are already handled, it stores
     their timestamps, so we will not lose the information if changes with the configuration
     happen (e.g., renaming). */
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
    final ConfigurationReader cfgReader = new ConfigurationReader(launchCfg);
    final String timestamp = cfgReader.getTimeStamp();
    /* timestamp.isEmpty() == true means the debug configuration is now creating. */
    if (!timestamp.isEmpty() && !seenTimestamps.contains(timestamp)) {
      seenTimestamps.add(timestamp);
      final int cfgFileVersion = cfgReader.getFileFormatVersion();
      if (cfgFileVersion != LaunchConfigurationConstants.CURRENT_FILE_FORMAT_VERSION) {
        warnUser(launchCfg, LaunchConfigurationConstants.CURRENT_FILE_FORMAT_VERSION,
            cfgFileVersion);
      }
    }
  }

  private void warnUser(final ILaunchConfiguration launchCfg, final int currentFileVersion,
      final int cfgFileVersion) {
    final String warningMsg = String.format(
        "This launch configuration was created with an incompatible version of ARC plug-in."
        + " Supported version of a launch configuration file is %d, but it is %s.",
        currentFileVersion,
        (cfgFileVersion == LaunchConfigurationConstants.UNREAL_FILE_FORMAT_VERSION ? "an older one"
            : cfgFileVersion));

    /* Try to use eclipse.application property to figure out if this is GUI or console run and to
     * show user an error message in a proper way. Property may not be set, then it is unknown
     * whether the GUI mode or headless is launched, therefore show warning both by creating marker
     * and writing to the System.err. */
    String applicationProperty = System.getProperty("eclipse.application");
    if (applicationProperty == null){
      System.err.println(warningMsg);
      createMarker(launchCfg, warningMsg);
    } else{
        final boolean isHeadless = applicationProperty
          .equals("org.eclipse.cdt.managedbuilder.core.headlessbuild");
        if (isHeadless) {
          System.err.println(warningMsg);
        } else {
          createMarker(launchCfg, warningMsg);
        }
    }
  }

  /**
  * This method makes the warning appear in the Problems view.
  * If the workspace does not exist, a <tt>CoreException</tt> occurs and is reported to the Eclipse's
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
