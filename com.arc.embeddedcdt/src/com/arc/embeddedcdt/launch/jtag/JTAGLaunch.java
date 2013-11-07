package com.arc.embeddedcdt.launch.jtag;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import com.arc.embeddedcdt.gui.jtag.ConfigJTAGTab;
import com.arc.embeddedcdt.launch.cygwin.CygWinLaunch;

public class JTAGLaunch extends CygWinLaunch implements
		ILaunchConfigurationDelegate
{

	@Override
	protected void uploadFile(IProgressMonitor monitor,
			ILaunchConfiguration configuration) throws CoreException
	{
		ConfigJTAGTab.uploadFile(configuration);
	}


}
