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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tm.internal.terminal.view.TerminalView;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

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

	/**
	 * Show dialog and terminate launch.
	 */
	private static class DialogRunnable implements Runnable {

		private ILaunch fLaunch;

		public DialogRunnable(ILaunch launch, IProcess process) {
			this.fLaunch = launch;
		}

		public void run() {
			//boolean terminate = true;
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				final IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();

				TerminalView viewPart;
				try {
					viewPart = (TerminalView) (activePage.showView(
							"org.eclipse.tm.terminal.view.TerminalView", null,
							IWorkbenchPage.VIEW_ACTIVATE));
					viewPart.dispose();
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
			//if (terminate) {
				DebugPlugin.getDefault().asyncExec(new TerminateRunnable(fLaunch));
			//}
		}
	}

	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent ev : events) {
			int code = ev.getKind();
			if ( code == DebugEvent.TERMINATE && (ev.getSource() instanceof IProcess)) {
				final IProcess p = (IProcess)ev.getSource();
				final ILaunch launch = p.getLaunch();
				/* We used to check for p.canTerminate(), but now we don't do
				   this anymore, so we could close non-processes as well, like
				   connection to serial port. */ 
				if (p.isTerminated() &&
						(p.getLabel() == Launch.OPENOCD_PROCESS_LABEL ||
						 p.getLabel() == Launch.ASHLING_PROCESS_LABEL ||
						 p.getLabel().startsWith(Launch.GDB_PROCESS_LABEL) )) {
					Display.getDefault().asyncExec(new DialogRunnable(launch, p));
					/* If two processes are already terminated, then user
					 * will get two dialogs without this return. */
					return;
				}
			}
		}
	}
}
