/*******************************************************************************
* Copyright (c) 2013 Synopsys, Inc.  All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0  which accompanies this
* distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*      Synopsys, Inc - ARC GNU Toolchain support
*******************************************************************************/

package com.arc.embeddedcdt.launch;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Debug event listener to terminate debug sessions when required.
 */
public class LaunchTerminator implements IDebugEventSetListener {

	/**
	 * Runnable to terminate launch asynchronously.
	 */
	private class TerminateRunnable implements Runnable {

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

	/**
	 * Show dialog and terminate launch.
	 */
	private class DialogRunnable implements Runnable {

		private ILaunch fLaunch;
		private IProcess fProcess;

		public DialogRunnable(ILaunch launch, IProcess process) {
			this.fLaunch = launch;
			this.fProcess = process;
		}

		public void run() {
			boolean terminate = true;
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				Shell parent = window.getShell();
				terminate = MessageDialog.openQuestion(
						parent,
						"Child process exited",
						"Process `" + fProcess.getLabel() + "' required for debugging has exited. Do you want to end debug session?"
				);
			}
			if (terminate) {
				DebugPlugin.getDefault().asyncExec(new TerminateRunnable(fLaunch));
			}
		}
	}

	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent ev : events) {
			int code = ev.getKind();
			if ( code == DebugEvent.TERMINATE && (ev.getSource() instanceof IProcess)) {
				final IProcess p = (IProcess)ev.getSource();
				final ILaunch launch = p.getLaunch();
				if (p.isTerminated() &&
						(p.getLabel() == Launch.OPENOCD_PROCESS_LABEL ||
						 p.getLabel() == Launch.ASHLING_PROCESS_LABEL ||
						 p.getLabel().startsWith(Launch.GDB_PROCESS_LABEL) ) &&
					 launch.canTerminate()) {
					Display.getDefault().asyncExec(new DialogRunnable(launch, p));
					/* If two processes are already terminated, then user
					 * will get two dialogs without this return. */
					return;
				}
			}
		}
	}
}
