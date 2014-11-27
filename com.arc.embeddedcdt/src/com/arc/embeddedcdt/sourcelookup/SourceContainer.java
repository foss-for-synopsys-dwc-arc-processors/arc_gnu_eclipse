/*******************************************************************************
 * Copyright (c) 2004, 2014 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX Software Systems - Initial API and implementation
 * Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/
package com.arc.embeddedcdt.sourcelookup; 

import java.util.ArrayList;

import org.eclipse.cdt.debug.core.sourcelookup.AbsolutePathSourceContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
 
/**
 * The source container for path mappings.
 */
public abstract class SourceContainer extends AbsolutePathSourceContainer {

	protected String fName;

	/** 
	 * Constructor for MappingSourceContainer. 
	 */
	public SourceContainer( String name ) {
		fName = name;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
	 */
	public String getName() {
		return fName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
	 */
	public ISourceContainerType getType()
	{
		return getSourceContainerType( getTypeID() );
	}

	/**
	 * Unique identifier for the mapping source container type
	 * (value <code>org.eclipse.cdt.debug.core.containerType.mapping</code>).
	 */
	abstract protected String getTypeID(); 

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceContainer#isComposite()
	 */
	public boolean isComposite() {
		return false;
	}

	private int count;
	
	public Object[] findSourceElements( String rawName ) throws CoreException {
		ArrayList sources = new ArrayList();
		
			
		/*
		 * we only want to do this path translation if none of the other
		 * SourceContainer's can do it.
		 * 
		 * This avoids duplicate lookups.
		 */
		if (count == 0)
		{
			IPath file = getCompilationPath(rawName);
			if (file != null)
			{
				String name = file.toFile().getAbsolutePath();
				count++;
				try
				{
					Object[] t=super.findSourceElements(name);
					if (t.length == 0)
					{
						/* this should never happen.... The SourceLookupDirectory should be able to
						 * open a file based upon a valid absolute path..... */
						sources.add(new LocalFileStorage(file.toFile()));
					} else
					{
						return t;
					}
				} finally
				{
					count--;
				}
			}
		} else
		{
			// do not recurse
		}
		return sources.toArray();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer#getSourceContainers()
	 */
	public ISourceContainer[] getSourceContainers() throws CoreException {
		return new ISourceContainer[0];
	}


	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainer#isFindDuplicates()
	 */
	protected boolean isFindDuplicates()
	{
		return false;
	}

	public void setName( String name ) {
		fName = name;
	}	

	/** 
	 * translate from GDB(CygWin or MinGW) speak to Windows speak or 
	 * return null if the file can not be found.
	 */
	abstract public IPath getCompilationPath( String sourceName );
	
}
