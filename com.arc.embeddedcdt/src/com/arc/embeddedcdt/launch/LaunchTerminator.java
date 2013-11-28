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
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class LaunchTerminator implements IDebugEventSetListener {
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

					// Active help does not run on the UI thread, so we must use syncExec
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							boolean terminate = true;
							IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
							if (window != null) {
								Shell parent = window.getShell();
								terminate = MessageDialog.openQuestion(
										parent,
										"Child process failed",
										"Process " + p.getLabel() + " requried for debugging has failed. Do you want to end debug session?"
								);
							}
							if (terminate) {
								try {
									launch.terminate();
								} catch (DebugException e) {
									e.printStackTrace();
								}
							}
						}
					});
				}
			}
		}
	}

}
