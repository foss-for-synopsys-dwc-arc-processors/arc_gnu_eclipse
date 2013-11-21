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

import org.eclipse.cdt.debug.mi.core.output.MIGDBShowDirectoriesInfo;
import org.eclipse.cdt.debug.mi.core.output.MIOutput;

import com.arc.embeddedcdt.copied.MIGDBShowDirectoriesCopied;

public class EmbeddedMIGDBShowDirectories extends MIGDBShowDirectoriesCopied {

	private EmbeddedCommandFactory factory;

	public EmbeddedMIGDBShowDirectories(EmbeddedCommandFactory factory) 
	{
		super(factory.getMIVersion());
		this.factory=factory;
	}

	protected MIGDBShowDirectoriesInfo createMIGDBShowDirectoriesInfo(MIOutput out) 
	{
		return new EmbeddedMIGDBShowDirectoriesInfo(factory, out);
	}
	
	

}
