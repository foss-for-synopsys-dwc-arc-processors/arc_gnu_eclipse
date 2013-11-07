/*******************************************************************************
 * Copyright (c) 2000, 2006 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/

package com.arc.embeddedcdt.copied;

import org.eclipse.cdt.debug.mi.core.MIException;
import org.eclipse.cdt.debug.mi.core.command.MIGDBShowDirectories;
import org.eclipse.cdt.debug.mi.core.output.MIGDBShowDirectoriesInfo;
import org.eclipse.cdt.debug.mi.core.output.MIInfo;
import org.eclipse.cdt.debug.mi.core.output.MIOutput;

/**
 * 
 *      -gdb-show directories
 *
 *   Show the current value of a GDB variable(directories).
 * 
 */
public class MIGDBShowDirectoriesCopied extends MIGDBShowDirectories {
	public MIGDBShowDirectoriesCopied(String miVersion) {
		super(miVersion); //$NON-NLS-1$
	}

	public MIGDBShowDirectoriesInfo getMIGDBShowDirectoriesInfo() throws MIException {
		return (MIGDBShowDirectoriesInfo)getMIInfo();
	}
	public MIInfo getMIInfo() throws MIException {
		MIInfo info = null;
		MIOutput out = getMIOutput();
		if (out != null) {
			info = createMIGDBShowDirectoriesInfo(out);
			if (info.isError()) {
				throwMIException(info, out);
			}
		}
		return info;
	}

	protected MIGDBShowDirectoriesInfo createMIGDBShowDirectoriesInfo(MIOutput out) {
		return new MIGDBShowDirectoriesInfo(out);
	}
}
