package com.arc.embeddedcdt.sourcelookup.cygwin;

import java.util.ArrayList;

import com.arc.embeddedcdt.sourcelookup.SourcePathComputerDelegate;

public class CygWinSourcePathComputerDelegate extends
		SourcePathComputerDelegate
{
	protected void addSourceContainer(ArrayList containers)
	{
		containers.add(new CygWinSourceContainer("Cygwin source path lookup"));
	}

}
