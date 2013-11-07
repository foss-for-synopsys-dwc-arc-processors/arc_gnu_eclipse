/**
 * 
 */
package com.arc.embeddedcdt;

import java.io.IOException;

import org.eclipse.cdt.debug.mi.core.MIProcess;
import org.eclipse.cdt.debug.mi.core.command.CommandFactory;
import org.eclipse.cdt.debug.mi.core.command.MIBreakInsert;
import org.eclipse.cdt.debug.mi.core.command.MIEnvironmentDirectory;
import org.eclipse.cdt.debug.mi.core.command.MIGDBShowDirectories;
import org.eclipse.core.runtime.IProgressMonitor;

import com.arc.embeddedcdt.launch.Launch;

public final class EmbeddedCommandFactory extends CommandFactory
{
	

	Launch launch;

	public EmbeddedCommandFactory(Launch embeddedLaunch, String miVersion)
	{
		super(miVersion);
		this.launch=embeddedLaunch;
	}

	public MIGDBShowDirectories createMIGDBShowDirectories() 
	{
		return new EmbeddedMIGDBShowDirectories(this);
	}

	public MIEnvironmentDirectory createMIEnvironmentDirectory(boolean reset, String[] pathdirs)
	{
		pathdirs=(String[])pathdirs.clone();
		for (int i=0; i<pathdirs.length; i++)
		{
			pathdirs[i]=fixPath(pathdirs[i]);
		}
		return super.createMIEnvironmentDirectory(reset, pathdirs);
	}

	public MIBreakInsert createMIBreakInsert(String func)
	{
		return super.createMIBreakInsert(func);
	}

	public MIBreakInsert createMIBreakInsert(boolean isTemporary, boolean isHardware, String condition, int ignoreCount, String line, int tid)
	{
		/* absolute paths kill GDB??? */
		line=fixPath(line);
		return super.createMIBreakInsert(isTemporary, isHardware, condition,
				ignoreCount, line, tid);
	}

	public MIProcess createMIProcess(String[] args, int launchTimeout, IProgressMonitor monitor) throws IOException {
		return new EmbeddedMIProcessAdapter(args, launchTimeout, 
				monitor, this.launch);
	}
	
	private String fixPath(String line)
	{
		String t=line;
		t=launch.fixPath(line);
		return t;
	}
}
