/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.launch.nativeos;

import java.io.File;

import com.arc.embeddedcdt.launch.Launch;


public class NativeLaunch extends Launch {

	public String fixPath(String line)
	{
		return line;
	}

	public String getSourcePathSeperator() 
	{
		return File.pathSeparator;
	}
	
}
