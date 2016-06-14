/*******************************************************************************
 * Copyright (c) 2000, 2014 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.gui.jtag;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.launch.ui.CLaunchConfigurationTab;
import org.eclipse.cdt.launch.ui.ICDTLaunchHelpContextIds;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.LaunchImages;
import com.arc.embeddedcdt.gui.buttons.FancyCheckButton;
import com.arc.embeddedcdt.gui.buttons.FancyCombo;
import com.arc.embeddedcdt.gui.buttons.FancyText;
import com.arc.embeddedcdt.gui.buttons.IClick;
import com.arc.embeddedcdt.gui.buttons.IFancyCombo;

/**
 * The whole clue here is that we use the configuration scripts as the persistent storage.
 * 
 * The buttons simply modify/display the relevant statement in the config file
 * in a more human friendly form.
 * 
 * Roundtripping works, so editing the script means that the buttons are updated.
 */
public class ConfigJTAGTab extends CLaunchConfigurationTab implements ITab
{

	private final class FetchIp implements Runnable
	{
		String ip;

		public void run()
		{
			ip=ipAddress.getButtonText();
		}
	}

	private static final String JTAG_SPEED_REGEXP = "(?s).*\\bjtag_speed (\\d)\\b.*";
	private static final String JTAG_TARGET_REGEXP = "(?s).*\\btarget (\\S+)\\b.*";
	private static final String JTAG_FLASH_REGEXP = "(?s).*\\bflash bank (\\S+)\\b.*";
	private static final String TELNET_PORT_REGEXP = "(?s).*\\btelnet_port ([0-9]+)\\b.*";
	private static final String GDB_PORT_REGEXP = "(?s).*\\bgdb_port ([0-9]+)\\b.*";
	private static final String IP_ADDRESS = "(?s).*\\btarget remote ([a-z0-9A-Z\\.]+):?\\b.*";
	protected static final String GDB_PORT_SCRIPT_REGEXP = "(?s).*\\btarget remote [a-z0-9A-Z\\.]+:([0-9]+)\\b.*";
	private static final String WRITE_TO_FLASH_COMMENT = "#strip executable and copy to Synopsys JTAG=";
	private static final String WRITE_TO_FLASH = "(?s).*" + WRITE_TO_FLASH_COMMENT +"([yn]).*";
	private static final String WRITE_TO_FLASH_YES = "y";
	private static final String WRITE_TO_FLASH_NO = "n";
	private FancyCombo fSpeed;

	
	
	private IGDBInit gdbinit;
	private FancyText ipAddress;
	private FancyText telnetPort;
	private Label status;
	private TabFolder tabFolder;
	private Composite container;
	private FancyText gdb_port;
	private CommandTab commandTab;
	private FancyCombo targetCombo;
	private FancyCombo flashCombo;
	ConfigScriptTab configScriptTab;
	public ConfigJTAGTab(
			IGDBInit gdbinit)
	{
		this.gdbinit=gdbinit;
	}

	public static String LINE_END()
	{
		return System.getProperty("line.separator");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		container=new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout());
		GridData gd;
		gd = new GridData(GridData.GRAB_HORIZONTAL|GridData.FILL_HORIZONTAL|GridData.GRAB_VERTICAL|GridData.FILL_VERTICAL);
		container.setLayoutData(gd);
		
		
		tabFolder=new TabFolder(container, SWT.NONE);
		gd = new GridData(GridData.GRAB_HORIZONTAL|GridData.FILL_HORIZONTAL|GridData.GRAB_VERTICAL|GridData.FILL_VERTICAL);
		tabFolder.setLayoutData(gd);
		
		configScriptTab=new ConfigScriptTab(gdbinit,  "/config/openocd.cfg",  "OpenOCD init script", "Synopsys JTAG/OpenOCD config commands", LaunchConfigurationConstants.ATTR_DEBUGGER_CONFIG);
		commandTab=new CommandTab(gdbinit);
		addConfigListener(commandTab);
		
		//createUploadTab();
		
		createConfigTab();
		

		status=new Label(container, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		status.setLayoutData(gd);
		setStatus("idle");

		configScriptTab.createTab();
		
		commandTab.createTab();
	}


	private void createUploadTab() 
	{
		Composite comp = createTab("Upload/flash programming");
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		comp.setLayout(topLayout);
		gdbinit.setDisplay(comp.getDisplay());
		
		
		
	}
	private void createConfigTab() 
	{
		Composite comp = createTab("Target configuration");
		gdbinit.setDisplay(comp.getDisplay());

		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(
						getControl(),
						ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_ARGUMNETS_TAB);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		comp.setLayout(topLayout);

		createVerticalSpacer(comp, 2);
		createTargetDropdown(comp, 2);
		
	}

	Composite createTab(String string) 
	{
		TabItem configItem=new TabItem(tabFolder, SWT.NONE);
		configItem.setText(string);
		
		Composite parent=new Composite(tabFolder, SWT.NONE);
		GridLayout g=new GridLayout();
		g.numColumns=2;
		parent.setLayout(g);
		configItem.setControl(parent);
		
		return parent;
	}

	


	private void createUploadButton(final Composite comp, int i)
	{
		new FancyCheckButton(this, commandTab.getGdbScript(), comp, "Copy stripped application image to /ram/app upon launch", WRITE_TO_FLASH)
		{
			@Override
			protected String createFirstEntry(String newText)
			{
				return WRITE_TO_FLASH_COMMENT+WRITE_TO_FLASH_YES;
			}

			@Override
			protected String getButtonText()
			{
				return checkButton.getSelection()?WRITE_TO_FLASH_YES:WRITE_TO_FLASH_NO;
			}

			@Override
			protected void setButtonText(String group)
			{
				checkButton.setSelection(group.equals(WRITE_TO_FLASH_YES));
			}
			
		};

		
		
	}
	
	
	
	public static byte[] read(File file)
	{
		try
		{
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			byte[] data = new byte[(int) fc.size()]; // fc.size returns the
														// size of the file
														// which backs the
														// channel
			ByteBuffer bb = ByteBuffer.wrap(data);
			fc.read(bb);
			return data;
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	 public static void copyFile(File in, File out)
	{
		try
		{
			FileInputStream fis = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			try
			{
				byte[] buf = new byte[1024];
				int i = 0;
				while ((i = fis.read(buf)) != -1)
				{
					fos.write(buf, 0, i);
				}
			} finally
			{
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			}
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	static ITransform stripper = new ITransform()
	{

		public void transform(String strip, File from, File app)
		{
			Process p;
			try
			{
				p = Runtime.getRuntime().exec(
						strip + " \"" + app.getAbsolutePath() + "\"");
				p.waitFor();
			} catch (IOException e)
			{
				throw new RuntimeException(e);
			} catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}

		}

	};
	static ITransform binConvert = new ITransform()
	{

		public void transform(String strip, File from, File app)
		{
			Process p;
			try
			{
				p = Runtime.getRuntime().exec(strip + " -O binary \"" + from.getAbsolutePath() + "\" \"" + app.getAbsolutePath() +"\"");
				p.waitFor();
			} catch (IOException e)
			{
				throw new RuntimeException(e);
			} catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}

		}

	};

	/** Flash target */
	private void flash(Shell shell)
	{
		gdbinit.runAsync(new Runnable()
		{
			public void run()
			{
//				uploadFileToSynopsysJTAG(s, executable, strip, stripper, "/ram/app");
			}

		});

	}

	private static String calcToolPath(String debugger, String tool)
	{
		Pattern p = Pattern.compile("(.*)gdb(.*)\\z");
		
		Matcher m = p.matcher(debugger);	
		if (!m.matches())
		{				
			throw new RuntimeException("Could not find strip executable based on GDB path");
		}
		final String strip;
		strip=m.group(1)+tool+m.group(2);
		return strip;
	}
	public void setStatus(final String string)
	{
		status.getDisplay().syncExec(new Runnable()
		{

			public void run() 
			{
				status.setText("Communication status: "+ string);
			}
			
		});
		
	}
	
	
	private void setupSupportedOptions(String c)
	{
		addOptions(c, targetCombo, "(?s).*?target ([a-zA-Z0-9]+)\\b.*?");
		addOptions(c, flashCombo, "(?s).*?flashdriver ([a-zA-Z0-9]+)\\b.*?");

	}

	private void addOptions(String c, FancyCombo fancyCombo, String regexp)
	{
		Matcher m = Pattern.compile(regexp).matcher(c);
		String prev=fancyCombo.getButtonText();
		fancyCombo.clear();
		while (m.find())
		{
			fancyCombo.add(m.group(1));
		}
		fancyCombo.setButtonText(prev);
	}

	

	
	public static void actionButton(Composite comp, String string, final IClick click)
	{
		final Button b = new Button(comp, SWT.NONE);
		b.setText(string);
		b.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				click.click(b.getShell());
			}
		});
	}

	protected void createTargetDropdown(Composite comp, int i)
	{
		createUploadButton(comp, 2);

		fSpeed = new FancyCombo(this, gdbinit.getConfigScript(), comp, "Speed", JTAG_SPEED_REGEXP)
		{

			public String createFirstEntry(String actual)
			{
				return "jtag_speed " + actual;
			}
			
		};
		fSpeed.add("4", "1MHz");
		fSpeed.add("3", "2MHz");
		fSpeed.add("2", "4MHz");
		fSpeed.add("1", "8MHz");
		fSpeed.add("0", "16MHz (overclocked)");

		targetCombo = new FancyCombo(this, gdbinit.getConfigScript(), comp, "Target type", JTAG_TARGET_REGEXP)
		{

			public String createFirstEntry(String actual)
			{
				return "target " + actual + "little" ;
			}
			
		};
		flashCombo = new FancyCombo(this, gdbinit.getConfigScript(), comp, "Flash type", JTAG_FLASH_REGEXP)
		{

			public String createFirstEntry(String actual)
			{
				return "flash bank " + actual + "little" ;
			}
			
		};
		
		setIpAddress(new FancyText(this, commandTab.getGdbScript(), comp, "IP address", IP_ADDRESS)
		{

			@Override
			public String createFirstEntry(String script, String ipAddr)
			{
				return createTargetRemote(script, gdb_port.getButtonText(), ipAddr);
			}

			
		});
		
		
		gdb_port=new FancyText(this, new IScript()
		{
			public void add(IFancyCombo fancyButton)
			{
				gdbinit.getConfigScript().add(fancyButton);
				commandTab.getGdbScript().add(fancyButton);
			}

			public void changeScript(IFirstExpression fancyButton,
					String regexp, String text)
			{
				gdbinit.getConfigScript().changeScript(fancyButton, regexp, text);
			}

			public String getText()
			{
				return gdbinit.getConfigScript().getText();
			}

			public void scriptChangedEvent()
			{
				gdbinit.getConfigScript().scriptChangedEvent();
			}

			public void setText(String text)
			{
				gdbinit.getConfigScript().setText(text);
			}
			
		}, comp, "GDB port(default 3333)", GDB_PORT_REGEXP )
		{
			@Override
			public void scriptChangedEvent(IScript source)
			{
				if (source==gdbinit.getConfigScript())
				{
					super.scriptChangedEvent(source);
				} else if (source==commandTab.getGdbScript())
				{
					scriptChangedEvent(source, GDB_PORT_SCRIPT_REGEXP);
				}
			}


			public String createFirstEntry(String actual) 
			{
				return "gdb_port " + actual;
			}


			@Override
			public void buttonChangedEvent()
			{
				super.buttonChangedEvent();
				
				updateGDBPortNumber();
			}

			private void updateGDBPortNumber()
			{
				// we must also update the GDB script
				commandTab.getGdbScript().changeScript(new IFirstExpression()
				{

					public String createFirstEntry(String script, String portNumber)
					{
						return createTargetRemote(script, portNumber, ipAddress.getButtonText());
					}

					
				}, GDB_PORT_SCRIPT_REGEXP, getButtonText());
			}


		};
		gdb_port.setDefaultValue("3333");
		setTelnetPort(new FancyText(this, gdbinit.getConfigScript(), comp, "Telnet port(default 4444)", TELNET_PORT_REGEXP )
		{
			public String createFirstEntry(String actual) 
			{
				return "telnet_port " + actual;
			}
		});
		getTelnetPort().setDefaultValue("4444");
		actionButton(comp, "Read options from Synopsys JTAG debugger", new IClick()
		{
			public void click(Shell shell)
			{
//				readOptions(shell);
			}

		});
	}

	private String createTargetRemote(String script,
			String portNumber, String ipAddr)
	{
		String t="target remote " + ipAddr + ":" + portNumber + LINE_END();
		t+=script;
		return t;
	}

	

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		for (IConfigListener l : list)
		{
			l.setDefaults(configuration);
		}
	}


	public void initializeFrom(ILaunchConfiguration configuration)
	{
		for (IConfigListener l : list)
		{
			try
			{
				l.initializeFrom(configuration);
			
			} catch (CoreException e)
			{
				setErrorMessage(e.getStatus().getMessage()); //$NON-NLS-1$
				LaunchUIPlugin.log(e);
			}
		}

	}


	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		for (IConfigListener l : list)
		{
			l.performApply(configuration);
		}
	}

	

	public String getName()
	{
		return "Synopsys JTAG debugger";
	}

	public Image getImage()
	{
		return LaunchImages.get(LaunchImages.IMG_VIEW_COMMANDS_TAB);
	}

	/**
	 * Retuns the string in the text widget, or <code>null</code> if empty.
	 * 
	 * @return text or <code>null</code>
	 */
	public static String getAttributeValueFrom(Text text)
	{
		String content = text.getText().trim();
		if (content.length() > 0)
		{
			return content;
		}
		return null;
	}

	public void setTelnetPort(FancyText telnetPort)
	{
		this.telnetPort = telnetPort;
	}

	public FancyText getTelnetPort()
	{
		return telnetPort;
	}

	public void setIpAddress(FancyText ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public FancyText getIpAddress()
	{
		return ipAddress;
	}



	public void updateIt()
	{
		updateLaunchConfigurationDialog();
	}


	static private void uploadFileToSynopsysJTAG(final String ip,
			final String executable, final String strip, ITransform transform, String destFileName)
	{
		try
		{
			File app=File.createTempFile("strip", "symbols");
			try
			{
				File from=new File(executable);
				copyFile(from, app);
				transform.transform(strip, from, app);
				
				String tcl="";
				tcl+="set fp [aio.open "+destFileName+" w]\n";
				tcl+="$fp puts -nonewline $form_filecontent\n";
				tcl+="$fp close\n";
				executeCommandTcl(ip, tcl, app);
			} finally
			{
				Boolean deletesucc=app.delete();
			}
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	/** Check if the script contains a statement to upload the executable */
	static public boolean checkUploadFile(String script)
	{
		String s=script;
		Matcher m = Pattern.compile(WRITE_TO_FLASH).matcher(s);
		if (m.matches())
		{				
			return m.group(1).equals(WRITE_TO_FLASH_YES);
		}
		return false;
	}

	public static void uploadFile(ILaunchConfiguration configuration) throws CoreException
	{
		String gdb=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, "");
		if (checkUploadFile(gdb))
		{
			String host=ConfigJTAGTab.getValue(gdb, ConfigJTAGTab.IP_ADDRESS, "Could not find host script");
//			String port=ConfigJTAGTab.getValue(openOCD, TELNET_PORT_REGEXP, "Could not find port script");
			
			String strip=calcToolPath(configuration.getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, ""), "strip");
			String executable=configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, "");
			
			//String strip=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, "")
			
			uploadFileToSynopsysJTAG(host, executable, strip, stripper, "/ram/app");
			
		}		
	}

	private static String getValue(String openOCD, String regexp, String error)
	{
		Matcher m = Pattern.compile(regexp).matcher(openOCD);
		if (m.matches())
		{				
			return m.group(1);
		} else
		{
			throw new RuntimeException(error);
		}
	}
	List<IConfigListener> list=new LinkedList<IConfigListener>();
	public void addConfigListener(IConfigListener configScriptTab2)
	{
		list.add(configScriptTab2);
		
	}

	public String executeCommand(String string)
	{
		return executeCommandTcl("openocd {" + string + "}", null);
	}
	
	
	public String executeCommandTcl(String string)
	{
		return executeCommandTcl(string, null);
	}
	/**
	 * Execute tcl script w/file payload
	 */
	public String executeCommandTcl(String string, File f)
	{
		FetchIp runIp = new FetchIp();
		status.getDisplay().syncExec(runIp);
		return executeCommandTcl(runIp.ip, string, f);
	}

	static private String executeCommandTcl(String ip, String command, File f)
	{
        PostMethod filePost = new PostMethod("http://" + ip + "/ram/cgi/execute.tcl");
        
        try
		{
//			filePost.getParams().setBooleanParameter(
//					HttpMethodParams.USE_EXPECT_CONTINUE, true);
			
			Part[] parts ;
			if (f!=null)
			{
				parts = new Part[]
				{ new StringPart("form_command", command),
				new FilePart("form_filecontent", "file", f)
				};
			} else
			{
				parts = new Part[]
				{ new StringPart("form_command", command)};
			}
			MultipartRequestEntity re = new MultipartRequestEntity(parts,
					filePost.getParams());
			filePost.setRequestEntity(re);
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			int status = client.executeMethod(filePost);
			if (status == HttpStatus.SC_OK)
			{
				String result=filePost.getResponseBodyAsString();
				
				Pattern p = Pattern.compile("(?s)Error: ([0-9]+)\n.*");
				Matcher m = p.matcher(result);	
				if (!m.matches())
				{
					throw new RuntimeException("Unknown result from execute.tcl: " + result);
				}
				Integer errorCode=Integer.parseInt(m.group(1));
				if (errorCode!=0)
				{
					throw new RuntimeException("Command \"" + command + "\" failed with error code " + errorCode );
				}
				p = Pattern.compile("(?s)Error: [0-9]+\n(.*)");
				m = p.matcher(result);
				if (!m.matches())
				{
					throw new RuntimeException("Unknown result from execute.tcl: " + result);
				}
				return m.group(1);
				
			} else
			{
				throw new RuntimeException("Upload failed");
			}
		} catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		} catch (HttpException e)
		{
			throw new RuntimeException(e);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		} finally
		{
			filePost.releaseConnection();
		}
	}

}
