/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf;

import org.eclipse.cdt.dsf.gdb.launching.GdbLaunchDelegate;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

/**
 * Launch delegate for DSF/GDB debugger.
 */
public class ArcLaunchDelegate extends GdbLaunchDelegate {

    // We need to launch debugger not only if the mode is DEBUG_MODE, but also in RUN_MODE, so
    // overriding the method.
    @Override
    public void launch(ILaunchConfiguration config, String mode, ILaunch launch,
            IProgressMonitor monitor) throws CoreException {

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        if (mode.equals(ILaunchManager.DEBUG_MODE) || mode.equals(ILaunchManager.RUN_MODE)) {
            launchDebugger(config, launch, monitor);
        }
    }

    // Just the copy of super.launchDebugger() method, which we can't use since it's private.
    private void launchDebugger(ILaunchConfiguration config, ILaunch launch,
            IProgressMonitor monitor) throws CoreException {

        monitor.beginTask(LaunchMessages.getString("GdbLaunchDelegate.0"), 10);
        if (monitor.isCanceled()) {
            cleanupLaunch();
            return;
        }
        try {
            launchDebugSession(config, launch, monitor);
        } finally {
            monitor.done();
        }
    }

}
