package com.arc.embeddedcdt.sourcelookup.cygwin;

import org.eclipse.debug.core.sourcelookup.ISourceContainerType;

import com.arc.embeddedcdt.sourcelookup.SourceLookupDirector;

public class CygWinSourceLookupDirector extends SourceLookupDirector
{

	public CygWinSourceLookupDirector()
	{
		super();
		container=new CygWinSourceContainer("Cygwin source container");
	}

	public boolean supportsSourceContainerType(ISourceContainerType type)
	{
		if (type.getId().equals(CygWinSourceContainer.TYPE_ID))
		{
			return true;
		} else
		{
			return super.supportsSourceContainerType(type);
		}
	}
	
}
