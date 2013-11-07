package com.arc.embeddedcdt.sourcelookup.cygwin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.arc.embeddedcdt.sourcelookup.SourceContainer;

public class CygWinSourceContainer extends SourceContainer
{

	static final String TYPE_ID = "com.arc.embeddedcdt.containerType.cygwin";
	public CygWinSourceContainer(String name)
	{
		super(name);
	}
	public IPath getCompilationPath(String sourceName)
	{
		File path = new File(sourceName);
		if (path.exists())
			return new Path(path.getAbsolutePath());
		
		/* replace "/cygdrive/c/" with "c:\". Handle the simplest case without using cygpath for
		 * backwards compatibility(perhaps the best would be to use cygpath always). */
		IPath p=convertPath(sourceName, "[/\\\\]cygdrive[/\\\\]([a-zA-Z])[/\\\\].*");
		
		if (p==null)
		{
			// Try cygpath
			String[] response=exec("cygpath -w -p \""+sourceName+"\"");
			if (response!=null)
			{
				path = new File(response[0]);
				if (path.exists())
				{
					p=new Path(path.getAbsolutePath());
				}
				
			}
		}
		
		return p;
	}
	private IPath convertPath(String sourceName, String regExp)
	{
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(sourceName);
		if (m.find())
		{
			String fixedPath;
			fixedPath = m.group(1) + ":\\" + sourceName.substring(m.end(1) + 1);
			File path = new File(fixedPath);
			if (path.exists())
			{
				return new Path(path.getAbsolutePath());
			}
		}
		return null;
	}
	public SourceContainer copy()
	{
		CygWinSourceContainer copy = new CygWinSourceContainer( fName );
		return copy;
	}
	protected String getTypeID()
	{
		return TYPE_ID;	 //$NON-NLS-1$
	}
	static final String SP = " "; //$NON-NLS-1$

	private String[] exec(String cmd)
	{
		try
		{
			Process proc = ProcessFactory.getFactory()
					.exec(cmd.split(SP), null);
			if (proc != null)
			{

				InputStream ein = proc.getInputStream();
				BufferedReader d1 = new BufferedReader(new InputStreamReader(
						ein));
				ArrayList ls = new ArrayList(10);
				String s;
				while ((s = d1.readLine()) != null)
				{
					ls.add(s);
				}
				ein.close();
				return (String[]) ls.toArray(new String[0]);
			}
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return null;
	}

}
