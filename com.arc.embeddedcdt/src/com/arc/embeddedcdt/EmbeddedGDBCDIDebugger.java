/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/

package com.arc.embeddedcdt;
/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

import java.io.File;

import org.eclipse.cdt.core.IBinaryParser.IBinaryObject;
import org.eclipse.cdt.debug.core.cdi.ICDISession;
import com.arc.embeddedcdt.launch.AbstractGDBCDIDebugger;
import org.eclipse.cdt.debug.mi.core.MIPlugin;
import org.eclipse.cdt.debug.mi.core.command.CommandFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.arc.embeddedcdt.launch.Launch;

/**
 * Implementing cdebugger extension point
 */
public class EmbeddedGDBCDIDebugger extends AbstractGDBCDIDebugger {

	ILaunch fLaunch;
	private Launch embeddedLaunch;

	
	public ICDISession createSession(Launch launch2, ILaunch launch, File executable,
			IProgressMonitor monitor) throws CoreException {
		embeddedLaunch=launch2;
		fLaunch = launch;
		return super.createSession(launch, executable, monitor);
	}

	public ICDISession createDebuggerSession(Launch launch2, ILaunch launch, IBinaryObject exe, IProgressMonitor monitor)
			throws CoreException {
		embeddedLaunch=launch2;
		fLaunch = launch;
		IPath gdbPath = getGDBPath( launch );
		ICDISession dsession = null;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dsession=super.createDebuggerSession(launch, exe, monitor);
		return dsession;
	}

	protected ILaunch getLauch()
	{
		return fLaunch;
	}

	protected String getMIVersion( ILaunchConfiguration config )
	{
		return MIPlugin.getMIVersion( config );
	}
	protected CommandFactory getCommandFactory(ILaunchConfiguration config) throws CoreException
	{
		return new EmbeddedCommandFactory(embeddedLaunch, getMIVersion( config )); 
	}

	protected String[] getExtraArguments(ILaunchConfiguration config)
			throws CoreException {
		
		File dir=embeddedLaunch.getStartDir();
		// Danger! The CDT spawner does not support arguments that contain quotes.
		return new String[]{"--cd="+embeddedLaunch.fixPath(dir.getAbsolutePath())};
	}

}
