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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.AbstractSourceContainerTypeDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
 
/**
 * The mapping container type.
 */
abstract public class SourceContainerType extends AbstractSourceContainerTypeDelegate {

	private final static String ELEMENT_MAPPING = "cygwinSourceMapper"; //$NON-NLS-1$
	private final static String ATTR_NAME = "name"; //$NON-NLS-1$
	

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourceContainerTypeDelegate#getMemento(org.eclipse.debug.core.sourcelookup.ISourceContainer)
	 */
	public String getMemento( ISourceContainer container ) throws CoreException {
		Document document = newDocument();
		Element element = document.createElement( ELEMENT_MAPPING );
		element.setAttribute( ATTR_NAME, container.getName() );
		document.appendChild( element );
		return serializeDocument( document );
	}
}
