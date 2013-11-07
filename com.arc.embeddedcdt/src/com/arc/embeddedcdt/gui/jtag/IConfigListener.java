package com.arc.embeddedcdt.gui.jtag;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public interface IConfigListener
{

	public abstract void setDefaults(
			ILaunchConfigurationWorkingCopy configuration);

	public abstract void initializeFrom(ILaunchConfiguration configuration)
			throws CoreException;

	public abstract void performApply(
			ILaunchConfigurationWorkingCopy configuration);

}