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
