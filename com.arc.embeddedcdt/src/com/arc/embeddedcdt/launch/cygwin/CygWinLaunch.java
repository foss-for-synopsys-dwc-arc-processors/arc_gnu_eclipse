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

package com.arc.embeddedcdt.launch.cygwin;

import com.arc.embeddedcdt.launch.Launch;


public class CygWinLaunch extends Launch {

	/**
	 * Translate Windows speak to CygWin speak. This expression will
	 * not match anything under Linux.
	 */
	@Override
	public String fixPath(String line)
	{
		return cygwinToWindowsPath(line);
	}

	/** Convert from native to GDB path's, could be */
	public static String cygwinToWindowsPath(String line)
	{
		// Note! No UNC support here...
		return line.replaceAll("([a-zA-Z]):[/\\\\]", "/cygdrive/$1/").replaceAll("\\\\", "/");
	}

	public String getSourcePathSeperator() 
	{
		return ":";
	}
}
