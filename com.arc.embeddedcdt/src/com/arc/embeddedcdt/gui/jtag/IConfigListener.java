/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/
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