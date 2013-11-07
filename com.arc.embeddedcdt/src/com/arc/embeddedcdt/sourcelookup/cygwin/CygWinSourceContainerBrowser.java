package com.arc.embeddedcdt.sourcelookup.cygwin;

import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.swt.widgets.Shell;

import com.arc.embeddedcdt.sourcelookup.SourceContainerBrowser;

public class CygWinSourceContainerBrowser extends
		SourceContainerBrowser
{
	public ISourceContainer[] addSourceContainers( Shell shell, ISourceLookupDirector director ) {
		return new ISourceContainer[] { new CygWinSourceContainer("Cygwin source container" ) };
	}
}
