/*******************************************************************************
 * Copyright (c) 2004, 2005 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package com.arc.embeddedcdt.sourcelookup; 

import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.ui.sourcelookup.AbstractSourceContainerBrowser;
import org.eclipse.swt.widgets.Shell;
 
/**
 * Adds a path mapping to the source lookup path.
 */
abstract public class SourceContainerBrowser extends AbstractSourceContainerBrowser {

	//private static final String MAPPING = SourceLookupUIMessages.getString( "MappingSourceContainerBrowser.0" ); //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.sourcelookup.AbstractSourceContainerBrowser#canAddSourceContainers(org.eclipse.debug.core.sourcelookup.ISourceLookupDirector)
	 */
	public boolean canAddSourceContainers( ISourceLookupDirector director ) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.sourcelookup.AbstractSourceContainerBrowser#canEditSourceContainers(org.eclipse.debug.core.sourcelookup.ISourceLookupDirector, org.eclipse.debug.core.sourcelookup.ISourceContainer[])
	 */
	public boolean canEditSourceContainers( ISourceLookupDirector director, ISourceContainer[] containers ) {
			return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.sourcelookup.AbstractSourceContainerBrowser#editSourceContainers(org.eclipse.swt.widgets.Shell, org.eclipse.debug.core.sourcelookup.ISourceLookupDirector, org.eclipse.debug.core.sourcelookup.ISourceContainer[])
	 */
	public ISourceContainer[] editSourceContainers( Shell shell, ISourceLookupDirector director, ISourceContainer[] containers ) {
		return new ISourceContainer[0];
	}

}
