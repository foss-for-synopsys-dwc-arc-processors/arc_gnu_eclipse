package com.arc.embeddedcdt.sourcelookup.cygwin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;

import com.arc.embeddedcdt.sourcelookup.SourceContainerType;

public class CygWinSourceContainerType extends SourceContainerType
{
	public ISourceContainer createSourceContainer( String memento ) throws CoreException {
		return new CygWinSourceContainer("Cygwin source container");
	}

}
