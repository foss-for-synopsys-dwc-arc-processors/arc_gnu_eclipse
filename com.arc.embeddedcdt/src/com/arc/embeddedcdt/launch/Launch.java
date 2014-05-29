/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.launch;

import gnu.io.CommPortIdentifier;

import com.arc.embeddedcdt.gui.FirstlaunchDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.eclipse.cdt.core.IAddressFactory;
import org.eclipse.cdt.core.IBinaryParser.IBinaryObject;
import org.eclipse.cdt.core.IBinaryParser.ISymbol;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.debug.core.CDIDebugModel;
import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.ICDebugConfiguration;
import org.eclipse.cdt.debug.core.cdi.CDIException;
import org.eclipse.cdt.debug.core.cdi.ICDISession;
import org.eclipse.cdt.debug.core.cdi.event.ICDIDestroyedEvent;
import org.eclipse.cdt.debug.core.cdi.event.ICDIEvent;
import org.eclipse.cdt.debug.core.cdi.event.ICDIEventListener;
import org.eclipse.cdt.debug.core.cdi.model.ICDITarget;
import org.eclipse.cdt.debug.mi.core.MIException;
import org.eclipse.cdt.debug.mi.core.MISession;
import org.eclipse.cdt.debug.mi.core.cdi.EventManager;
import org.eclipse.cdt.debug.mi.core.cdi.model.Target;
import org.eclipse.cdt.debug.mi.core.command.CLICommand;
import org.eclipse.cdt.debug.mi.core.command.CommandFactory;
import org.eclipse.cdt.debug.mi.core.command.MIStackListFrames;
import org.eclipse.cdt.debug.mi.core.output.MIConsoleStreamOutput;
import org.eclipse.cdt.debug.mi.core.output.MIInfo;
import org.eclipse.cdt.debug.mi.core.output.MIOOBRecord;
import org.eclipse.cdt.debug.mi.core.output.MIOutput;
import org.eclipse.cdt.debug.mi.core.output.MIParser;
import org.eclipse.cdt.debug.mi.core.output.MITargetStreamOutput;
import org.eclipse.cdt.debug.mi.internal.ui.GDBDebuggerPage;
import org.eclipse.cdt.launch.AbstractCLaunchDelegate;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.cdt.utils.BinaryObjectAdapter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tm.internal.terminal.connector.TerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;
import org.eclipse.tm.internal.terminal.serial.SerialConnector;
import org.eclipse.tm.internal.terminal.serial.SerialSettings;
import org.eclipse.tm.internal.terminal.view.TerminalView;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

import com.arc.embeddedcdt.Configuration;
import com.arc.embeddedcdt.EmbeddedGDBCDIDebugger;
import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.LaunchPlugin;
import com.arc.embeddedcdt.gui.RemoteGDBDebuggerPage;
import com.arc.embeddedcdt.proxy.cdt.LaunchMessages;

public abstract class Launch extends AbstractCLaunchDelegate implements
		ICDIEventListener
{

	private final class RunCommand implements Runnable
	{
		private final CLICommand cli;
		private final MISession miSession;
		private MIException result;

		private RunCommand(CLICommand cli, MISession miSession)
		{
			this.cli = cli;
			this.miSession = miSession;
		}

		public void run()
		{
			try
			{
				miSession.postCommand(cli, 365 * 24 * 3600 * 1000);
			} catch (MIException e)
			{
				result = e;
			}
		}
	}

	// Process names useful for LaunchTerminator
	public final static String OPENOCD_PROCESS_LABEL = "OpenOCD";
	public final static String ASHLING_PROCESS_LABEL = "Ashling GDBserver";
	public final static String GDB_PROCESS_LABEL = "arc-elf32-gdb";
	public final static String NSIM_PROCESS_LABEL = "nSIM GDBserver";

	public ICProject myProject;
	private ICDISession dsession;
	private ILaunchConfiguration launch_config;

	abstract public String getSourcePathSeperator();

	public Launch() {
		super();
		DebugPlugin.getDefault().addDebugEventListener(new LaunchTerminator());
	}
	
	/**
	 * Return the save environment variables in the configuration. The array
	 * does not include the default environment of the target. array[n] :
	 */
	public String[] getEnvironment()  
	{
	  try 
	  {
		return super.getEnvironment(launch_config);
	  } 
	  catch (CoreException e) 
	  {		  
	  }	
      return new String[0];
	}

	private boolean isOpenOCD(ILaunchConfiguration configuration) throws CoreException {
		String external_tools = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String());
		return external_tools.equalsIgnoreCase("JTAG via OpenOCD");
		//return external_tools_firstlaunch.equalsIgnoreCase("JTAG via OpenOCD");
	}
	
	private boolean isnSIM(ILaunchConfiguration configuration) throws CoreException {
		String external_tools = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String());
		return external_tools.equalsIgnoreCase("nSIM");
		//return external_tools_firstlaunch.equalsIgnoreCase("nSIM");
	}
	
	private boolean isAshling(ILaunchConfiguration configuration) throws CoreException {
		//return external_tools_firstlaunch.equalsIgnoreCase("JTAG via Ashling");
		String external_tools = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS, new String());
		return external_tools.equalsIgnoreCase("JTAG via Ashling");

	}
	public static String serialport="";

	
	private void startTerminal() {
		IWorkbench workbench = PlatformUI.getWorkbench();

        if (workbench.getDisplay().getThread() != Thread.currentThread()){
            // Note that we do the work asynchronously so that we don't lock this thread. It is used
            // to launch the debugger engine.
            workbench.getDisplay().asyncExec(new Runnable(){
                @Override
                public void run () {
                	startTerminal();
                }});
            return;
        }
		
		// Assertion: we're in the UI thread.
		final IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();

		class MyClass extends SerialConnector {
			public MyClass(SerialSettings settings) { super(settings); }
			@Override
			public void initialize() { }
		}

		TerminalConnector.Factory factory = new TerminalConnector.Factory() {
			public TerminalConnectorImpl makeConnector() throws Exception {
				SerialSettings mySettings = new SerialSettings();
				mySettings.setBaudRate("115200");
				mySettings.setDataBits("8");
				mySettings.setFlowControl("XON/XOFF");
				mySettings.setParity("N");
				mySettings.setSerialPort(serialport);
				mySettings.setStopBits("1");
				return new MyClass(mySettings);
			}
		};

		TerminalConnector c1 = new TerminalConnector(factory, "connector-id",
				"ARC GNU IDE", false /* is hidden */);

		TerminalView viewPart;
		try {
			viewPart = (TerminalView) (activePage.showView(
					"org.eclipse.tm.terminal.view.TerminalView", null,
					IWorkbenchPage.VIEW_ACTIVATE));
			viewPart.newTerminal(c1);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	public void launch(ILaunchConfiguration configuration, String mode,
			final ILaunch launch, IProgressMonitor monitor)
			throws CoreException
	{
	
		
		String external_tools=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,new String());
		String external_tools_launch = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_DEFAULT,"true");
		String gdbserver_port=configuration.getAttribute( IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT, new String());
		serialport=configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT, new String());
			
		if(external_tools.equalsIgnoreCase("")||gdbserver_port.equalsIgnoreCase(""))
			return;
		
		String gdbserver_IPAddress = configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS, "localhost");
		
		if(gdbserver_IPAddress.equalsIgnoreCase("")||gdbserver_IPAddress==null)
		{
			gdbserver_IPAddress="localhost";
		}
		
	
		
		launch_config = configuration.getWorkingCopy();
		
		if (monitor == null)
		{
			monitor = new NullProgressMonitor();
		}

		monitor.subTask("Embedded debugger launch"); //$NON-NLS-1$
		// check for cancellation
		if (monitor.isCanceled())
		{
			return;
		}
		try
		{
			monitor.worked(1);
			ICProject project;
			String name = org.eclipse.cdt.debug.core.CDebugUtils.getProjectName(configuration);
			if ((name!=null)&&(name.length()>0))
			{
				project =  org.eclipse.cdt.debug.core.CDebugUtils.verifyCProject(configuration);
			} else
			{
				// normal project
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject genProject = root.getProject("arc-cdt-debugging");
				if (!genProject.exists())
				{
					genProject.create(null);
				}
				genProject.open(monitor);
				// add C nature
				IProjectDescription d = genProject.getDescription();
				d.setNatureIds(new String[]
				{ "org.eclipse.cdt.core.cnature" });
				genProject.setDescription(d, null);
				
				project = CoreModel.getDefault().getCModel().getCProject(
						genProject.getName());
			}

			IPath exePath = verifyProgramPath(configuration, project);
			// Accessed later to set up cwd for GDB
			myProject = project;
			IBinaryObject exeFile = verifyBinary(project, exePath);
			// set the default source locator if required
			setDefaultSourceLocator(launch, configuration);

			String terminal_launch= configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT,"true");
			
			/* Do we need to connect to serial port? */
			if (terminal_launch.equalsIgnoreCase("true") && !external_tools.equalsIgnoreCase("nSIM")
					&& !serialport.equalsIgnoreCase("") && !external_tools.equalsIgnoreCase("Generic gdbserver")) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					// Do nothing.
					e2.printStackTrace();
				}
				startTerminal();
			}
			
			if (mode.equals(ILaunchManager.DEBUG_MODE)||mode.equals(ILaunchManager.RUN_MODE))
			{
				ICDebugConfiguration debugConfig = getDebugConfig(configuration);
				dsession = null;

				String debugMode = configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);

				if (debugMode.equals(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN))
				{

					LaunchFrontend l = new LaunchFrontend(launch);
                    
                    String extenal_tools_openocd_path= configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH,new String());
                    String extenal_tools_ashling_path= configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH,new String());
                    String extenal_tools_nsim_path= configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH,new String());
                    
                    prepareSession();

					// Start GDB first. This is required to ensure that if gdbserver
					// will fail to start up then GDB will be closed. Eclipse cannot
					// kill GDB when it is trying to execute script commands.
					if (exeFile!=null)
					{
						dsession = ((EmbeddedGDBCDIDebugger) debugConfig.createDebugger()).createDebuggerSession(this, l,exeFile, new SubProgressMonitor(monitor, 8));
					} else
					{
						/* no executable for session*/
						dsession = ((EmbeddedGDBCDIDebugger) debugConfig.createDebugger()).createSession(this, launch, null, new SubProgressMonitor(monitor, 8));
					}

					dsession.getEventManager().addEventListener(this);
					patchSession(configuration);

					ICDITarget[] dtargets = dsession.getTargets();
					
					/* Do not allow processing of events while we launch as this
					 * would query the target with e.g. process list before we're
					 * ready.
					 */
					EventManager eventManager = (EventManager) dsession.getEventManager();
					boolean prevstateAllowEvents = eventManager.isAllowingProcessingEvents();
					eventManager.allowProcessingEvents(false);

					setupTargets(dtargets);

					// Start gdbserver
					String eclipsehome= Platform.getInstallLocation().getURL().toString();
					eclipsehome=eclipsehome.substring(eclipsehome.lastIndexOf("file:/")+6, eclipsehome.length());
					
					// TODO Replace hardcoded line with something more error-proof.
					if (external_tools.equalsIgnoreCase("JTAG via Ashling")&&external_tools_launch.equalsIgnoreCase("true"))
					{
						// TODO Path to Ashling GDB server is also configurable in CommandTab.
						if(extenal_tools_ashling_path.equalsIgnoreCase("")) 
						{
							if (RemoteGDBDebuggerPage.isWindowsOS())
								extenal_tools_ashling_path = RemoteGDBDebuggerPage.ASHLING_DEFAULT_PATH_WINDOWS;
							else
								extenal_tools_ashling_path = RemoteGDBDebuggerPage.ASHLING_DEFAULT_PATH_LINUX;
						}
						System.setProperty("Ashling", extenal_tools_ashling_path);
						String ash_dir = System.getProperty("Ashling");
						File ash_wd = new java.io.File(ash_dir);
						String ashling_exe = "ash-arc-gdb-server" + (RemoteGDBDebuggerPage.isWindowsOS() ? ".exe" : ""); 
						String[] ash_cmd = {
								ash_dir + java.io.File.separator + ashling_exe,
								"--jtag-frequency", "8mhz",
								"--device", "arc",
								"--arc-reg-file", ash_dir + java.io.File.separator + "arc-opella-em.xml"
								};
						DebugPlugin.newProcess(launch, DebugPlugin.exec(ash_cmd, ash_wd), ASHLING_PROCESS_LABEL);
					} 
					else if (external_tools.equalsIgnoreCase("JTAG via OpenOCD")&&external_tools_launch.equalsIgnoreCase("true"))
					{
						// Start OpenOCD GDB server
						if(extenal_tools_openocd_path.isEmpty()) {
							extenal_tools_openocd_path = RemoteGDBDebuggerPage.getOpenOCDDefaultPath();
					    }
						String[] openocd_cmd = { "openocd", "-f",extenal_tools_openocd_path,"-c","init","-c","halt","-c","\"reset halt\""  };
						DebugPlugin.newProcess(launch, DebugPlugin.exec(openocd_cmd, null), OPENOCD_PROCESS_LABEL);
					}
					else if (external_tools.equalsIgnoreCase("nSIM")&&external_tools_launch.equalsIgnoreCase("true"))
					{						
						// Start nSIM GDB server
						if (extenal_tools_nsim_path.isEmpty()) {
							extenal_tools_nsim_path = RemoteGDBDebuggerPage.getNsimdrvDefaultPath();
						}
						System.setProperty("nSIM", extenal_tools_nsim_path);
						String nsim_exec = System.getProperty("nSIM");
						File nsim_wd = (new java.io.File(nsim_exec))
								.getParentFile();
//						String nsimProps = nsim_wd.getParentFile().getPath()
//								+ java.io.File.separator + "systemc"
//								+ java.io.File.separator + "configs"
//								//+ java.io.File.separator + "nsim_av2em11.props";
//					            + java.io.File.separator + RemoteGDBDebuggerPage.nSIMpropsfiles;
						String nsimProps = RemoteGDBDebuggerPage.nSIMpropsfiles;
						String[] nsim_cmd = { nsim_exec, "-gdb", "-propsfile",
								nsimProps };

						DebugPlugin.newProcess(launch,
								DebugPlugin.exec(nsim_cmd, nsim_wd),
								NSIM_PROCESS_LABEL);
					}

				
					try
					{
						monitor.subTask("Running .gdbinit");
						runGDBInit(configuration, dtargets, monitor);

						uploadFile(monitor, configuration);

						monitor.subTask("Running GDB init script");
						String initcommand =configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT,new String());
						if(!initcommand.equalsIgnoreCase(""))
						     executeGDBScript("GDB commands",configuration,dtargets,	getExtraCommands(configuration,	configuration.getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT,new String())), monitor);
						
						String gdb_init ="";
						
						if (!isAshling(configuration))
							gdb_init=String.format("target remote %s:%s\nload",gdbserver_IPAddress,gdbserver_port);
						else 
							gdb_init=String.format("set arc opella-target arcem \ntarget remote %s:%s\nload",gdbserver_IPAddress, gdbserver_port);

						executeGDBScript("GDB commands",configuration,dtargets,	getExtraCommands(configuration,	gdb_init), monitor);
											
						monitor.worked(2);
						monitor.subTask("Creating launch target");
						createLaunchTarget(launch, project, exeFile,debugConfig, dtargets, configuration, mode);
						monitor.subTask("Query target state");
						queryTargetState(dtargets);
						// This will make the GDB console frontmost.
						l.addStragglers();
						
						eventManager.allowProcessingEvents(prevstateAllowEvents);
						
						
					} catch (Exception e)
					{
						try
						{
							dsession.terminate();
						} catch (CDIException e1)
						{
							// ignore
						}
						MultiStatus status = new MultiStatus(
								LaunchPlugin.PLUGIN_ID,
								ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR,
								"Could not start debug session", e);
						LaunchUIPlugin.log(status);
						throw new CoreException(status);

					}
				}
			} else
			{
				cancel("TargetConfiguration not supported",
						ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
			}
		} finally
		{
			monitor.done();
		}

	}

	/**
	 * Allow subclasses to do their bit before launching. Could be to start a
	 * simulator.
	 */
	protected void prepareSession()
	{

	}

	private void createLaunchTarget(final ILaunch launch, ICProject project,
			IBinaryObject exeFile, ICDebugConfiguration debugConfig,
			ICDITarget[] dtargets, ILaunchConfiguration configuration, String mode)
			throws CoreException
	{
		boolean appConsole = getAppConsole(configuration);
		// create the Launch targets/processes for eclipse.
		for (int i = 0; i < dtargets.length; i++)
		{
			Target target = (Target) dtargets[i];
			target.setConfiguration(new Configuration(target));
			Process process = target.getProcess();
			IProcess iprocess = null;
			if (appConsole && (process != null))
			{
				iprocess = DebugPlugin.newProcess(launch, process,
						renderProcessLabel(exeFile.getPath().toOSString()));
			}

			boolean stopInMain = configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, false);
			String stopSymbol = null;
			// Do not stop at symbol (usually it is main) if it is "Run" configuration, not "Debug" one.
			if (stopInMain && mode.equalsIgnoreCase(ILaunchManager.DEBUG_MODE))
				stopSymbol = launch.getLaunchConfiguration().getAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL, 
						ICDTLaunchConfigurationConstants.DEBUGGER_STOP_AT_MAIN_SYMBOL_DEFAULT);


			CDIDebugModel.newDebugTarget(launch, project.getProject(),
					dtargets[i], renderTargetLabel(debugConfig), iprocess,exeFile, true, true, stopSymbol, true);
		}
	}

	private boolean getAppConsole(ILaunchConfiguration configuration)
			throws CoreException
	{
		boolean appConsole = configuration.getAttribute(
				LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE,
				LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE_DEFAULT);
		return appConsole;
	}

	public void runGDBInit(ILaunchConfiguration configuration,
			ICDITarget[] dtargets, IProgressMonitor monitor)
			throws CoreException
	{
		String iniFile = configuration.getAttribute(
				IMILaunchConfigurationConstants.ATTR_GDB_INIT,
				IMILaunchConfigurationConstants.DEBUGGER_GDB_INIT_DEFAULT);

		if (iniFile.equals("") || !new File(iniFile).exists())
			return;

		try
		{
			executeGDBScript("GDB commands", configuration, dtargets,getExtraCommands(configuration, "source " +fixPath(iniFile)), monitor);

		} catch (Exception e)
		{
			MultiStatus status = new MultiStatus(getPluginID(),
					ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR,
					"Executing GDB startup file", e);
			CDebugCorePlugin.log(status);
		}
	}

	// Do we need to upload the file to the target?
	protected void uploadFile(IProgressMonitor monitor,
			ILaunchConfiguration configuration) throws CoreException
	{

	}

	// We need to override the parser to be able to handle output "monitor"
	// commands.
	private void patchSession(ILaunchConfiguration configuration)
			throws CoreException
	{
		if (getAppConsole(configuration))
			return;
		ICDITarget[] dtargets = dsession.getTargets();
		for (int i = 0; i < dtargets.length; ++i)
		{
			Target target = (Target) dtargets[i];
			MISession miSession = target.getMISession();

			miSession.setMIParser(new MIParser()
			{

				@Override
				public MIOutput parse(String buffer)
				{
					// Convert from:
					// @"target already halted\\n"\n
					// to:
					// &"set remotetimeout 100\\n"\n
					//
					// Also CDT interprets any other string e.g. from "shell"
					// command
					// as coming from the target. We want everything into the
					// gdb console.
					//
					// This will make output from "monitor" commands visible as
					// the
					// remote protocol does not have a way to distinguish
					// between
					// output from the application and "monitor" commands.
					MIOutput o = super.parse(buffer);

					MIOOBRecord[] oobs = o.getMIOOBRecords();
					for (int i = 0; i < oobs.length; i++)
					{
						if (oobs[i] instanceof MITargetStreamOutput)
						{
							MITargetStreamOutput t = (MITargetStreamOutput) oobs[i];
							MIConsoleStreamOutput c = new MIConsoleStreamOutput();
							c.setCString(t.getCString());
							oobs[i] = c;

						}
					}
					return o;
				}

			});
		}
	}

	
	/** We always talk to a GDB server, set it up. */
	private void setupTargets(ICDITarget[] dtargets)
	{
		for (int i = 0; i < dtargets.length; ++i)
		{
			Target target = (Target) dtargets[i];
			MISession miSession = target.getMISession();
			
			/*
			 * We're running against a GDB server. 
			 */
//			miSession.getMIInferior().setIsRemoteInferior(true);
			/* Tell CDT not to try to figure out the process ID! It makes
			 * no sense for embedded.
			 */
			miSession.getMIInferior().setInferiorPID(-1);
		}
	}


	private void queryTargetState(ICDITarget[] dtargets)
	{
		// Try to detect if we have been attach/connected via "target remote
		// localhost:port"
		// or "attach" and set the state to be suspended.
		for (int i = 0; i < dtargets.length; ++i)
		{
			Target target = (Target) dtargets[i];
			MISession miSession = target.getMISession();
			CommandFactory factory = miSession.getCommandFactory();
			try
			{
				MIStackListFrames frames = factory.createMIStackListFrames();
				miSession.postCommand(frames, 1000);
				MIInfo info = frames.getMIInfo();
				if (info == null)
				{
					throw new MIException("GDB state query failed"); //$NON-NLS-1$
				} else
				{
					// @@@ We have to manually set the suspended state since we
					// have some stackframes
					miSession.getMIInferior().setSuspended();
					miSession.getMIInferior().update();
				}
			} catch (MIException e)
			{
				// If an exception is thrown that means ok
				// we did not attach/connect to any target.
			}
		}
	}

	public void executeGDBScript(String scriptSource,
			ILaunchConfiguration configuration, ICDITarget[] dtargets,
			String[] extraCommands2, IProgressMonitor monitor)
			throws CoreException
	{
		// Try to execute any extract command
		String[] commands = extraCommands2;
		for (int i = 0; i < dtargets.length; ++i)
		{
			Target target = (Target) dtargets[i];
			final MISession miSession = target.getMISession();
			// Could we manage to add back the prompt here by creating or
			// ownMIParser? It wouldn't be pretty...
// MIParser m=new EmbeddedMIParser();
// miSession.setMIParser(m);
			for (int j = 0; j < commands.length; ++j)
			{
				try
				{
					if (monitor.isCanceled())
						return;
					// Skip comments
					if (commands[j].startsWith("#"))
						continue;
					monitor.subTask(scriptSource + ": " + commands[j]);

					final CLICommand cli = new CLICommand(commands[j]);
					// cli.setSilent(false);
					RunCommand result = new RunCommand(cli, miSession);
					Thread t = new Thread(result);
					t.start();
					while (t.isAlive())
					{
						try
						{
							Thread.sleep(100);
						} catch (InterruptedException e)
						{
							throw new RuntimeException(e);
						}
						if (monitor.isCanceled())
						{
							// stop session
							miSession.terminate();
						}
					}

					if (result.result != null)
					{
						throw result.result;
					}

					MIInfo info = cli.getMIInfo();
					if (info == null)
					{
						throw new MIException("Timeout: " + commands[j]); //$NON-NLS-1$
					}
				} catch (MIException e)
				{
					MultiStatus status = new MultiStatus(
							getPluginID(),
							ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR,
							"Failed command: " + commands[j], e);
					status
							.add(new Status(
									IStatus.ERROR,
									getPluginID(),
									ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR,
									e == null ? "" : e.getLocalizedMessage(), //$NON-NLS-1$
									e));
					CDebugCorePlugin.log(status);
				}
			}
		}
	}

	protected String getPluginID()
	{
		return LaunchPlugin.PLUGIN_ID;
	}

	protected String[] getExtraCommands(ILaunchConfiguration configuration,
			String commands2) throws CoreException
	{
		String commands = commands2;
		if (commands != null && commands.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(commands, "\r\n");
			String[] cmds = new String[st.countTokens()];
			for (int i = 0; st.hasMoreTokens(); ++i)
			{
				cmds[i] = st.nextToken();
			}
			return cmds;
		}
		return new String[0];
	}

	/**
	 * embedded targets should not rebuild before launching the debugger as
	 * flash programming might be done out of bands.
	 */
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException
	{
		return false;
	}

	/**
	 * Translate Windows speak to host GDB speak(e.g. CygWin or MinGW).
	 */
	abstract public String fixPath(String line);

	@SuppressWarnings("deprecation")
	protected IPath verifyProgramPath(ILaunchConfiguration config,
			ICProject project) throws CoreException
	{
		IPath p = getProgramPath(config);
		boolean dummy=false;
		if (p == null)
		{
			// no executable specified, we just need something to silence
			// the angry CDT code, which doesn't have a concept of launching
			// a debug session without an executable
			p = new Path("dummy.elf");
			dummy=true;
		}

		// always exercise this statement.
		File f;
		
		if (p.isAbsolute())
		{
			f = new File(p.toOSString());
		} else
		{
			f = new File(project.getResource().getLocation().toFile(), p.toOSString());
			 /* convert to absolute path */
			p = new Path(f.getAbsolutePath());  
		}

		if (!dummy&&!f.exists())
		{
			abort(
					LaunchMessages.getString("AbstractCLaunchDelegate.Program_file_does_not_exist"), //$NON-NLS-1$
					new FileNotFoundException(
							LaunchMessages.getFormattedString(
																"AbstractCLaunchDelegate.PROGRAM_PATH_not_found", f.toString())), //$NON-NLS-1$
					ICDTLaunchConfigurationConstants.ERR_PROGRAM_NOT_EXIST);
			return null;
		}
		
		return p;
	}

	@Override
	protected IBinaryObject verifyBinary(ICProject proj, IPath exePath)
			throws CoreException {
		
			try
			{
				 IBinaryObject tmp = super.verifyBinary(proj, exePath);
				 if (tmp == null)
				 {
					 /* CDT 7 started returning null from verifyBinary()... */
					 throw new Exception();
				 }
				 return tmp;
			} catch (Exception e)
			{
				return new BinaryObjectAdapter(null, exePath,	-1 // none of CDT's business
						)
				{
					IAddressFactory factory=new FakeAddressFactory();
					@Override
					public IAddressFactory getAddressFactory()
					{
						return factory;
					}

					@Override
					protected BinaryObjectInfo getBinaryObjectInfo()
					{
						return null; // seems to work...
					}

					@Override
					public ISymbol[] getSymbols()
					{
						return null; // seems to work
					}
				};
				
			}
	}

	public File getStartDir()
	{
		return myProject.getResource().getLocation().toFile();
	}

	public void handleDebugEvents(ICDIEvent[] events)
	{
		for (int i = 0; i < events.length; i++)
		{
			ICDIEvent event = events[i];
			if (event instanceof ICDIDestroyedEvent)
			{
			}
		}
	}

	/** Convenient spot for subclasses to destroy things belonging to this event */
	protected void debugSessionEnded()
	{
	}

	public static List COMserialport()
	{
		List<String> list = new ArrayList<String>();
		try {
			Enumeration portIdEnum= CommPortIdentifier.getPortIdentifiers();
			while (portIdEnum.hasMoreElements()) {
				CommPortIdentifier identifier = (CommPortIdentifier) portIdEnum.nextElement();
				String strName = identifier.getName();
				int nPortType = identifier.getPortType();

				if (nPortType == CommPortIdentifier.PORT_SERIAL)
					list.add(strName);
			}

		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(list.size()<1) {
			list.add("Please connect to EM Starter Kit");
		}
		return list;
	}

	  
}
