/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf.utils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

import com.arc.embeddedcdt.LaunchPlugin;

public class DebugUtils {

    /**
     * Test if the launch configuration is already started. Enumerate all launches and check by name
     * and non terminated status.
     * 
     * @param configuration
     * @return true if already present.
     */
    public static boolean isLaunchConfigurationStarted(ILaunchConfiguration configuration) {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunch[] launches = launchManager.getLaunches();
        for (ILaunch launch : launches) {
            if (!launch.isTerminated() && (launch.getLaunchConfiguration() != null)
                    && configuration.getName().equals(launch.getLaunchConfiguration().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the launch configuration is already started and throw a CoreException.
     * 
     * @param configuration
     * @throws CoreException
     */
    public static void checkLaunchConfigurationStarted(ILaunchConfiguration configuration)
            throws CoreException {
        if (isLaunchConfigurationStarted(configuration)) {
            throw new CoreException(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID,
                    "Debug session '" + configuration.getName()
                            + "' already started. Terminate the first one before restarting."));
        }
    }
}
