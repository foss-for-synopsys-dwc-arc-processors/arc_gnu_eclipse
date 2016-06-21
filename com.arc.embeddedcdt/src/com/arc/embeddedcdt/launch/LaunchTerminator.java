/*******************************************************************************
* Copyright (c) 2013, 2014 Synopsys, Inc.  All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0  which accompanies this
* distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*      Synopsys, Inc - ARC GNU Toolchain support
*******************************************************************************/

package com.arc.embeddedcdt.launch;

import org.eclipse.cdt.dsf.gdb.launching.GDBProcess;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;

/**
 * Debug event listener to terminate debug sessions when required.
 */
public class LaunchTerminator implements IDebugEventSetListener {

    /**
     * Runnable to terminate launch asynchronously.
     */
    private static class TerminateRunnable implements Runnable {

        private ILaunch fLaunch;

        public TerminateRunnable(ILaunch launch) {
            this.fLaunch = launch;
        }

        public void run() {
            try {
                fLaunch.terminate();
            } catch (DebugException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleDebugEvents(DebugEvent[] events) {
        for (DebugEvent ev : events) {
            int code = ev.getKind();
            if (code == DebugEvent.TERMINATE && ev.getSource() instanceof GDBProcess) {
                final GDBProcess p = (GDBProcess) ev.getSource();
                final ILaunch launch = p.getLaunch();
                if (p.isTerminated()) {
                    DebugPlugin.getDefault().asyncExec(new TerminateRunnable(launch));
                    /*
                     * If two processes are already terminated, then user will get two dialogs
                     * without this return.
                     */
                    return;
                }
            }
        }
    }
}
