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
