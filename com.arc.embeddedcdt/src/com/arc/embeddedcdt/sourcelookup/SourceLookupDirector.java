package com.arc.embeddedcdt.sourcelookup;

import java.io.File;

import org.eclipse.cdt.debug.core.model.ICBreakpoint;
import org.eclipse.cdt.debug.internal.core.sourcelookup.CSourceLookupDirector;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;

public class SourceLookupDirector extends CSourceLookupDirector 
{


	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector#getSourceElement(java.lang.Object)
	 */
	public Object getSourceElement(Object element)
	{
		Object t;
		t=super.getSourceElement(element);
		if (t==null && element instanceof String)
		{
			IPath p=getCompilationPath((String)element);
			if (p!=null)
			{
				t=new LocalFileStorage(p.toFile());
			}
			/* In the case of a dummy project being used the file could be in a non-C/C++ project... */
			File f=new File((String)element);
			if (f.exists())
			{
				t=new LocalFileStorage(f);
			}
		} 
		return t; 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.debug.internal.core.sourcelookup.CSourceLookupDirector#initializeParticipants()
	 */
	public void initializeParticipants()
	{
		// TODO Auto-generated method stub
		super.initializeParticipants();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.cdt.debug.internal.core.sourcelookup.CSourceLookupDirector#contains(org.eclipse.cdt.debug.core.model.ICBreakpoint)
	 */
	public boolean contains(ICBreakpoint breakpoint)
	{
		try
		{
			return super.contains(breakpoint)||
			container.getCompilationPath(breakpoint.getSourceHandle())!=null;
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean contains(IProject project)
	{
		return super.contains(project);
	}

	protected SourceContainer container;
	
	public IPath getCompilationPath(String sourceName)
	{
		IPath t=super.getCompilationPath(sourceName);
		if (t==null)
		{
			t=container.getCompilationPath( sourceName );
		}
		if ((t==null)&&(new File(sourceName).exists()))
		{
			t=new Path(sourceName);
		}
		
		return t;
	}

	

}
