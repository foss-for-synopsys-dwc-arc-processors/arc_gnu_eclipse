/*******************************************************************************
* This program and the accompanying materials 
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

package com.arc.embeddedcdt;

import org.eclipse.cdt.debug.mi.core.output.MIGDBShowDirectoriesInfoCopied;
import org.eclipse.cdt.debug.mi.core.output.MIOutput;


public class EmbeddedMIGDBShowDirectoriesInfo extends MIGDBShowDirectoriesInfoCopied {

	private EmbeddedCommandFactory factory;

	public EmbeddedMIGDBShowDirectoriesInfo(EmbeddedCommandFactory factory, MIOutput o) 
	{
		super(o);
		this.factory=factory;
	}

	protected String getPathSeperator() 
	{
		return factory.launch.getSourcePathSeperator();
	}

}
