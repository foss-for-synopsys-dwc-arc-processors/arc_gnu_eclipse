/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
package com.arc.embeddedcdt.launch;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

final class LaunchFrontend implements ILaunch
	{
		private final ILaunch launch;
//		private IProcess pending;

		LaunchFrontend(ILaunch launch)
		{
			this.launch = launch;
		}

		public void addDebugTarget(IDebugTarget target)
		{
			launch.addDebugTarget(target);
		}

		public void addProcess(IProcess process)
		{
//			if ((pending==null)||(pending==process))
//			{
//				pending=process;
//				return;
//			}
			launch.addProcess(process);
			
		}
		
		public void addStragglers()
		{
//			launch.addProcess(pending);
		}

		public String getAttribute(String key)
		{
			return launch.getAttribute(key);
		}

		public Object[] getChildren()
		{
			return launch.getChildren();
		}

		public IDebugTarget getDebugTarget()
		{
			return launch.getDebugTarget();
		}

		public IDebugTarget[] getDebugTargets()
		{
			return launch.getDebugTargets();
		}

		public ILaunchConfiguration getLaunchConfiguration()
		{
			return launch.getLaunchConfiguration();
		}

		public String getLaunchMode()
		{
			return launch.getLaunchMode();
		}

		public IProcess[] getProcesses()
		{
			return launch.getProcesses();
		}

		public ISourceLocator getSourceLocator()
		{
			return launch.getSourceLocator();
		}

		public boolean hasChildren()
		{
			return launch.hasChildren();
		}

		public void removeDebugTarget(IDebugTarget target)
		{
			launch.removeDebugTarget(target);
			
		}

		public void removeProcess(IProcess process)
		{
			launch.removeProcess(process);
			
		}

		public void setAttribute(String key, String value)
		{
			launch.setAttribute(key, value);
			
		}

		public void setSourceLocator(ISourceLocator sourceLocator)
		{
			launch.setSourceLocator(sourceLocator);
			
		}

		public boolean canTerminate()
		{
			return launch.canTerminate();
		}

		public boolean isTerminated()
		{
			return launch.isTerminated();
		}

		public void terminate() throws DebugException
		{   
			//com.arc.embeddedcdt.launch.Launch.killexternaltools();
			launch.terminate();
		}

		public Object getAdapter(Class adapter)
		{
			return launch.getAdapter(adapter);
		}
	}