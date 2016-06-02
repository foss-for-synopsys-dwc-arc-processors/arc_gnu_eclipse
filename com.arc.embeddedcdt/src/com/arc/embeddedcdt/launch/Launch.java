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

package com.arc.embeddedcdt.launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

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
import org.eclipse.cdt.launch.AbstractCLaunchDelegate;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.cdt.utils.BinaryObjectAdapter;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
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
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.tm.internal.terminal.connector.TerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;
import org.eclipse.tm.internal.terminal.serial.SerialConnector;
import org.eclipse.tm.internal.terminal.serial.SerialSettings;
import org.eclipse.tm.internal.terminal.view.TerminalView;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.embeddedcdt.Configuration;
import com.arc.embeddedcdt.EmbeddedGDBCDIDebugger;
import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.LaunchPlugin;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;
import com.arc.embeddedcdt.common.InvalidDirectoryPathException;
import com.arc.embeddedcdt.gui.ARCWorkingDirectoryBlock;
import com.arc.embeddedcdt.proxy.cdt.LaunchMessages;

import gnu.io.CommPortIdentifier;

@SuppressWarnings("restriction")
public abstract class Launch extends AbstractCLaunchDelegate implements ICDIEventListener {
    private final static class RunCommand implements Runnable {
        private final CLICommand cli;
        private final MISession miSession;
        private MIException result;

        private RunCommand(CLICommand cli, MISession miSession) {
            this.cli = cli;
            this.miSession = miSession;
        }

        public void run() {
            try {
                miSession.postCommand(cli, 365 * 24 * 3600 * 1000);
            } catch (MIException e) {
                result = e;
            }
        }
    }

    // Process names useful for LaunchTerminator
    public final static String OPENOCD_PROCESS_LABEL = "OpenOCD";
    public final static String ASHLING_PROCESS_LABEL = "Ashling GDBserver";
    public final static String GDB_PROCESS_LABEL = "arc-elf32-gdb";
    public final static String NSIM_PROCESS_LABEL = "nSIM GDBserver";
    public final static String CUSTOM_GDBSERVER_LABEL = "Custom GDBserver";

    public ICProject myProject;
    private ICDISession dsession;
    private ILaunchConfiguration launch_config;

    abstract public String getSourcePathSeperator();

    public Launch() {
        super();
        DebugPlugin.getDefault().addDebugEventListener(new LaunchTerminator());
    }

    /**
     * Return the save environment variables in the configuration. The array does not include the
     * default environment of the target. array[n] :
     */
    public String[] getEnvironment() {
        try {
            return super.getEnvironment(launch_config);
        } catch (CoreException e) {
        }
        return new String[0];
    }

    public static String serialport = "";

    private void startTerminal() {
        IWorkbench workbench = PlatformUI.getWorkbench();

        if (workbench.getDisplay().getThread() != Thread.currentThread()) {
            // Note that we do the work asynchronously so that we don't lock this thread. It is used
            // to launch the debugger engine.
            workbench.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    startTerminal();
                }
            });
            return;
        }

        // Assertion: we're in the UI thread.
        final IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();

        class MyClass extends SerialConnector {
            public MyClass(SerialSettings settings) {
                super(settings);
            }

            @Override
            public void initialize() {
            }
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

        TerminalConnector c1 = new TerminalConnector(factory, "connector-id", "ARC GNU IDE", false);

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

    public void launch(ILaunchConfiguration configuration, String mode, final ILaunch launch,
            IProgressMonitor monitor) throws CoreException {

        ArcGdbServer gdbServer = ArcGdbServer.fromString(configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,
                ArcGdbServer.DEFAULT_GDB_SERVER.toString()));

        String gdbserver_port = configuration
                .getAttribute(IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT, "");

        FtdiDevice ftdi_device;
        try {
            ftdi_device = FtdiDevice.valueOf(
                    configuration.getAttribute(LaunchConfigurationConstants.ATTR_FTDI_DEVICE,
                            LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE_NAME));
        } catch (IllegalArgumentException e) {
            ftdi_device = LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE;
        }

        FtdiCore ftdi_core;
        try {
            ftdi_core = FtdiCore
                    .valueOf(configuration.getAttribute(LaunchConfigurationConstants.ATTR_FTDI_CORE,
                            LaunchConfigurationConstants.DEFAULT_FTDI_CORE_NAME));
        } catch (IllegalArgumentException e) {
            ftdi_core = LaunchConfigurationConstants.DEFAULT_FTDI_CORE;
        }

        serialport = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT, "");
        if (gdbServer == ArcGdbServer.JTAG_OPENOCD) {
            if ((ftdi_device == FtdiDevice.AXS101 && ftdi_core == FtdiCore.EM6)
                    || (ftdi_device == FtdiDevice.AXS102 && ftdi_core == FtdiCore.HS34)
                    || (ftdi_device == FtdiDevice.AXS103 && ftdi_core == FtdiCore.HS38_0)) {
                gdbserver_port = String.valueOf(Integer.parseInt(gdbserver_port) + 1);
            } else if (ftdi_device == FtdiDevice.AXS101 && ftdi_core == FtdiCore.AS221_2) {
                gdbserver_port = String.valueOf(Integer.parseInt(gdbserver_port) + 2);
            } else if (ftdi_device == FtdiDevice.AXS101 && ftdi_core == FtdiCore.AS221_1) {
                gdbserver_port = String.valueOf(Integer.parseInt(gdbserver_port) + 3);
            }
        }

        if (gdbserver_port.isEmpty())
            return;

        String gdbserver_IPAddress = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS,
                LaunchConfigurationConstants.DEFAULT_GDB_HOST);

        if (gdbserver_IPAddress.isEmpty()) {
            gdbserver_IPAddress = LaunchConfigurationConstants.DEFAULT_GDB_HOST;
        }

        launch_config = configuration.getWorkingCopy();

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        monitor.subTask("Embedded debugger launch"); //$NON-NLS-1$
        // check for cancellation
        if (monitor.isCanceled()) {
            return;
        }
        try {
            monitor.worked(1);
            ICProject project;
            String name = org.eclipse.cdt.debug.core.CDebugUtils.getProjectName(configuration);

            // This code is for building current project before launching it

            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IProject genProject = root.getProject(name);
            IBuildConfiguration[] buildConfigs = genProject.getBuildConfigs();
            IProgressMonitor buildMonitor = new SubProgressMonitor(monitor, 10,
                    SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
            boolean build_before_launch = configuration.getAttribute(
                    LaunchConfigurationConstants.ATTR_DEBUGGER_BUILD_BEFORE_LAUNCH,
                    LaunchConfigurationConstants.ATTR_DEBUGGER_BUILD_BEFORE_LAUNCH_DEFAULT);
            if (build_before_launch == true)
                ResourcesPlugin.getWorkspace().build(buildConfigs,
                        IncrementalProjectBuilder.INCREMENTAL_BUILD, true,
                        new SubProgressMonitor(buildMonitor, 3));

            if ((name != null) && (name.length() > 0)) {
                project = org.eclipse.cdt.debug.core.CDebugUtils.verifyCProject(configuration);
            } else {
                // normal project
                genProject = root.getProject("arc-cdt-debugging");
                if (!genProject.exists()) {
                    genProject.create(null);
                }
                genProject.open(monitor);
                // add C nature
                IProjectDescription d = genProject.getDescription();
                d.setNatureIds(new String[] { "org.eclipse.cdt.core.cnature" });
                genProject.setDescription(d, null);

                project = CoreModel.getDefault().getCModel().getCProject(genProject.getName());
            }
            IPath exePath = null;
            myProject = project;
            IBinaryObject exeFile = null;
            if (project != null) {
                exePath = verifyProgramPath(configuration, project);
                exeFile = verifyBinary(project, exePath);
            }

            // set the default source locator if required
            setDefaultSourceLocator(launch, configuration);

            String terminal_launch = configuration.getAttribute(
                    LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, "true");

            /* Do we need to connect to serial port? */
            if (terminal_launch.equalsIgnoreCase("true") && gdbServer != ArcGdbServer.NSIM
                    && !serialport.isEmpty()
                    && gdbServer != ArcGdbServer.GENERIC_GDBSERVER) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e2) {
                    // Do nothing.
                    e2.printStackTrace();
                }
                startTerminal();
            }

            if (mode.equals(ILaunchManager.DEBUG_MODE) || mode.equals(ILaunchManager.RUN_MODE)) {
                ICDebugConfiguration debugConfig = getDebugConfig(configuration);
                dsession = null;

                String debugMode = configuration.getAttribute(
                        ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
                        ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);

                if (debugMode.equals(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN)) {
                    LaunchFrontend l = new LaunchFrontend(launch);
                    prepareSession();

                    // Start GDB first. This is required to ensure that if gdbserver
                    // will fail to start up then GDB will be closed. Eclipse cannot
                    // kill GDB when it is trying to execute script commands.
                    if (exeFile != null) {
                        dsession = ((EmbeddedGDBCDIDebugger) debugConfig.createDebugger())
                                .createDebuggerSession(this, l, exeFile,
                                        new SubProgressMonitor(monitor, 8));
                    } else {
                        /* no executable for session */
                        dsession = ((EmbeddedGDBCDIDebugger) debugConfig.createDebugger())
                                .createSession(this, launch, null,
                                        new SubProgressMonitor(monitor, 8));
                    }

                    if (dsession != null)
                        dsession.getEventManager().addEventListener(this);
                    patchSession(configuration);
                    ICDITarget[] dtargets = null;
                    if (dsession != null)
                        dtargets = dsession.getTargets();

                    /*
                     * Do not allow processing of events while we launch as this would query the
                     * target with e.g. process list before we're ready.
                     */
                    EventManager eventManager = null;
                    if (dsession != null)
                        eventManager = (EventManager) dsession.getEventManager();
                    boolean prevstateAllowEvents = false;
                    if (eventManager != null) {
                        prevstateAllowEvents = eventManager.isAllowingProcessingEvents();
                        eventManager.allowProcessingEvents(false);
                    }
                    if (dtargets != null)
                        setupTargets(dtargets);

                    // Start gdbserver
                    String eclipsehome = Platform.getInstallLocation().getURL().toString();
                    eclipsehome = eclipsehome.substring(eclipsehome.lastIndexOf("file:/") + 6,
                            eclipsehome.length());

                    switch (gdbServer) {
                    case JTAG_ASHLING:
                        start_ashling(configuration, launch);
                        break;
                    case JTAG_OPENOCD:
                        start_openocd(configuration, launch);
                        break;
                    case NSIM:
                        start_nsim(configuration, launch);
                        break;
                    case CUSTOM_GDBSERVER:
                        start_custom_gdbserver(configuration, launch);
                    case GENERIC_GDBSERVER:
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown enum value has been used");
                    }

                    try {
                        monitor.subTask("Running .gdbinit");
                        if (dtargets != null)
                            runGDBInit(configuration, dtargets, monitor);

                        uploadFile(monitor, configuration);

                        monitor.subTask("Running GDB init script");
                        String initcommand = configuration.getAttribute(
                                LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, "");
                        if (!initcommand.isEmpty()) {
                            String commands = configuration.getAttribute(
                                    LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, "");
                            String[] extraCommands = getExtraCommands(configuration, commands);
                            executeGDBScript("GDB commands", configuration, dtargets, extraCommands,
                                    monitor);
                        }

                        String gdb_init = "";

                        String defaultGDBHost = LaunchConfigurationConstants.DEFAULT_GDB_HOST;
                        switch (gdbServer) {
                        case JTAG_ASHLING:
                            String ashlingTDescPath = configuration.getAttribute(
                                    LaunchConfigurationConstants.ATTR_ASHLING_TDESC_PATH, "");
                            gdb_init = "set tdesc filename " + ashlingTDescPath + "\n"
                                    + String.format("target remote %s:%s\nload",
                                            defaultGDBHost, gdbserver_port);
                            break;
                        case JTAG_OPENOCD:
                        case NSIM:
                            gdb_init = String.format("target remote %s:%s\nload",
                                    defaultGDBHost, gdbserver_port);
                            break;
                        case CUSTOM_GDBSERVER:
                        case GENERIC_GDBSERVER:
                            gdb_init = String.format("target remote %s:%s\nload",
                                    gdbserver_IPAddress, gdbserver_port);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown enum value has been used");
                        }

                        executeGDBScript("GDB commands", configuration, dtargets,
                                getExtraCommands(configuration, gdb_init), monitor);

                        monitor.worked(2);
                        monitor.subTask("Creating launch target");
                        if (project != null)
                            createLaunchTarget(launch, project, exeFile, debugConfig, dtargets,
                                    configuration, mode);
                        monitor.subTask("Query target state");
                        queryTargetState(dtargets);
                        // This will make the GDB console frontmost.
                        l.addStragglers();

                        if (eventManager != null)
                            eventManager.allowProcessingEvents(prevstateAllowEvents);

                    } catch (Exception e) {
                        try {
                            if (dsession != null)
                                dsession.terminate();
                        } catch (CDIException e1) {
                            // ignore
                        }
                        MultiStatus status = new MultiStatus(LaunchPlugin.PLUGIN_ID,
                                ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR,
                                "Could not start debug session", e);
                        LaunchUIPlugin.log(status);
                        throw new CoreException(status);

                    }
                }
            } else {
                cancel("TargetConfiguration not supported",
                        ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
            }
        } finally {
            monitor.done();
        }

    }

    /*
     * @return true---windows
     */
    private static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") > -1) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    /**
     * Start Ashling GDB Server executable.
     *
     * @throws CoreException
     */
    private void start_ashling(final ILaunchConfiguration configuration, final ILaunch launch)
            throws CoreException {
        String external_tools_ashling_path = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH, "");

        /*
         * TODO Currently we configure directory of Ashling GDBserver not path to executable itself,
         * which is rather clumsy. Let's move UI to specify path to executable.
         */
        if (external_tools_ashling_path.isEmpty()) {
            if (isWindowsOS())
                external_tools_ashling_path = LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_WINDOWS;
            else
                external_tools_ashling_path = LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_LINUX;
        }

        final String gdbserver_port = configuration.getAttribute(
                IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,
                LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

        final String ashling_xml_file = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_ASHLING_XML_PATH, "");
        final String jtag_frequency = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_JTAG_FREQUENCY, "");
        System.setProperty("Ashling", external_tools_ashling_path);
        final File ash_dir = new File(external_tools_ashling_path).getParentFile();

        final String ash_cmd = external_tools_ashling_path + " --jtag-frequency " + jtag_frequency
                + " --device" + " arc" + " --gdb-port " + gdbserver_port + " --arc-reg-file "
                + ashling_xml_file;
        final IProcess ashling_proc = DebugPlugin.newProcess(launch,
                DebugPlugin.exec(DebugPlugin.parseArguments(ash_cmd), ash_dir),
                ASHLING_PROCESS_LABEL);
        ashling_proc.setAttribute(IProcess.ATTR_CMDLINE, ash_cmd);

        /* Additional sleep is required so that Ashling GDB server has time to start
            before GDB connects to it.
            It doesn't seem possible to wait for Ashling server to write a special
            message and then continue, because if everything goes as supposed, it
            doesn't write anything. It is probable that this happens because event
            listener is added only after all the messages are sent to the stream and
            it isn't called until something else is sent there, which doesn't happen
            in our case. */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start nSIM GDB server.
     *
     * @throws CoreException
     */
    private void start_nsim(final ILaunchConfiguration configuration, final ILaunch launch)
            throws CoreException {
        String extenal_tools_nsim_path = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH, "");
        System.setProperty("nSIM", extenal_tools_nsim_path);
        String nsim_exec = System.getProperty("nSIM");
        File nsim_wd = new File(System.getProperty("user.dir"));
        String workingDirectoryPath = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_WORKING_DIRECTORY, (String)null);
        try {
            workingDirectoryPath = ARCWorkingDirectoryBlock.resolveDirectoryPath(workingDirectoryPath);
            nsim_wd = new File(workingDirectoryPath);
        } catch (InvalidDirectoryPathException e) {
            String message = "Working directory for nSIM is not specified or incorrect:\n"
                    + e.getMessage()
                    + "\n\nUsing directory \'" + nsim_wd.getPath() + "\' instead.";
            StatusManager.getManager().handle(
                    new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, message),
                    StatusManager.BLOCK);
        }
        String nsimProps = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_NSIM_PROP_FILE, "");
        String nsimtcf = configuration.getAttribute(LaunchConfigurationConstants.ATTR_NSIM_TCF_FILE,
                "");
        String nsimprops_Buttonboolean = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMPROPS, "true");
        String nsimtcf_Buttonboolean = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMTCF, "true");
        String nsimJIT_Buttonboolean = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMJIT, "false");
        String nsimHostlink_Buttonboolean = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMHOSTLINK, "true");
        String nsimMemoExptButtonboolean = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMMEMOEXPT, "true");
        String nsimEnableExptButtonboolean = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMENABLEEXPT, "true");
        String nsiminvalid_Instru_ExptButtonboolean = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMENABLEEXPT, "true");

        String nsimjit_thread = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMJITTHREAD, "1");
        final String gdbserver_port = configuration.getAttribute(
                IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,
                LaunchConfigurationConstants.DEFAULT_NSIM_PORT);
        String nsim_cmd = nsim_exec + " -port " + gdbserver_port + " -gdb ";

        if (nsiminvalid_Instru_ExptButtonboolean.equalsIgnoreCase("false")) {
            nsim_cmd += "-off invalid_instruction_interrupt ";
        }

        if (nsimEnableExptButtonboolean.equalsIgnoreCase("false")) {
            nsim_cmd += " -off enable_exceptions ";
        }

        if (nsimMemoExptButtonboolean.equalsIgnoreCase("false")) {
            nsim_cmd += " -off memory_exception_interrupt ";
        }

        if (nsimJIT_Buttonboolean.equalsIgnoreCase("true")) {
            nsim_cmd += " -on nsim_fast ";
            if (!nsimjit_thread.equalsIgnoreCase("1")) {
                nsim_cmd += "-p nsim_fast-num-threads=" + nsimjit_thread;
            }

        }

        if (nsimHostlink_Buttonboolean.equalsIgnoreCase("true")) {
            nsim_cmd += " -on nsim_emt ";
        }

        if (nsimtcf_Buttonboolean.equalsIgnoreCase("true")) {
            nsim_cmd += " -tcf " + nsimtcf;
        }

        if (nsimprops_Buttonboolean.equalsIgnoreCase("true")) {
            nsim_cmd += " -propsfile " + nsimProps;
        }

        IProcess nsim_proc = DebugPlugin.newProcess(launch,
                DebugPlugin.exec(DebugPlugin.parseArguments(nsim_cmd), nsim_wd),
                NSIM_PROCESS_LABEL);
        nsim_proc.setAttribute(IProcess.ATTR_CMDLINE, nsim_cmd);
    }

    /**
     * Start OpenOCD executable.
     *
     * @throws CoreException
     */
    private void start_openocd(final ILaunchConfiguration configuration, final ILaunch launch)
            throws CoreException {
        String gdbserver_port = configuration.getAttribute(
                IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,
                LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT);

        String openocd_cfg = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH, "");
        final String openocd_bin = configuration
                .getAttribute(LaunchConfigurationConstants.ATTR_DEBUGGER_OPENOCD_BIN_PATH, "");

        // ${openocd_bin}/../share/openocd/scripts
        final File root_dir = new File(openocd_bin).getParentFile().getParentFile();
        final File scripts_dir = new File(root_dir,
                "share" + File.separator + "openocd" + File.separator + "scripts");
        final String openocd_tcl = scripts_dir.getAbsolutePath();
        /*
         * "gdb_port" is before -f <script> so script file can override our settings. Also in case
         * of configuration scripts supplied by Synopsys we cannot set gdb_port after -f option -
         * our scripts do initialization and changing GDB port after that is not supported OpenOCD.
         */
        final String openocd_cmd = openocd_bin + " -d0 " + " -c \"gdb_port " + gdbserver_port + "\""
                + " -s " + openocd_tcl + " -f " + openocd_cfg;

        final IProcess openocd_proc = DebugPlugin.newProcess(launch,
                DebugPlugin.exec(DebugPlugin.parseArguments(openocd_cmd), null),
                OPENOCD_PROCESS_LABEL);

        openocd_proc.setAttribute(IProcess.ATTR_CMDLINE, openocd_cmd);
    }

    private void start_custom_gdbserver(final ILaunchConfiguration configuration,
            final ILaunch launch) throws CoreException {

        final String custom_gdbserver_bin = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_CUSTOM_GDBSERVER_BIN_PATH, "");

        final String custom_gdbserver_arg = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_CUSTOM_GDBSERVER_COMMAND, "");

        final String custom_gdbsever_cmd = custom_gdbserver_bin + " " + custom_gdbserver_arg;

        final IProcess custom_gdbserver_proc = DebugPlugin.newProcess(launch,
                DebugPlugin.exec(DebugPlugin.parseArguments(custom_gdbsever_cmd), null),
                CUSTOM_GDBSERVER_LABEL);

        custom_gdbserver_proc.setAttribute(IProcess.ATTR_CMDLINE, custom_gdbsever_cmd);
    }

    /**
     * Allow subclasses to do their bit before launching. Could be to start a simulator.
     */
    protected void prepareSession() {

    }

    private void createLaunchTarget(final ILaunch launch, ICProject project, IBinaryObject exeFile,
            ICDebugConfiguration debugConfig, ICDITarget[] dtargets,
            ILaunchConfiguration configuration, String mode) throws CoreException {
        boolean appConsole = getAppConsole(configuration);
        // create the Launch targets/processes for eclipse.
        for (int i = 0; i < dtargets.length; i++) {
            Target target = (Target) dtargets[i];
            target.setConfiguration(new Configuration(target));
            Process process = target.getProcess();
            IProcess iprocess = null;
            if (appConsole && (process != null)) {
                iprocess = DebugPlugin.newProcess(launch, process,
                        renderProcessLabel(exeFile.getPath().toOSString()));
            }

            boolean stopInMain = configuration.getAttribute(
                    ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, false);
            String stopSymbol = null;
            // Do not stop at symbol (usually it is main) if it is "Run" configuration, not "Debug"
            // one.
            if (stopInMain && mode.equalsIgnoreCase(ILaunchManager.DEBUG_MODE))
                stopSymbol = launch.getLaunchConfiguration().getAttribute(
                        ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL,
                        ICDTLaunchConfigurationConstants.DEBUGGER_STOP_AT_MAIN_SYMBOL_DEFAULT);

            CDIDebugModel.newDebugTarget(launch, project.getProject(), dtargets[i],
                    renderTargetLabel(debugConfig), iprocess, exeFile, true, true, stopSymbol,
                    true);
        }
    }

    private boolean getAppConsole(ILaunchConfiguration configuration) throws CoreException {
        boolean appConsole = configuration.getAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE,
                LaunchConfigurationConstants.ATTR_DEBUGGER_APP_CONSOLE_DEFAULT);
        return appConsole;
    }

    public void runGDBInit(ILaunchConfiguration configuration, ICDITarget[] dtargets,
            IProgressMonitor monitor) throws CoreException {
        String iniFile = configuration.getAttribute(IMILaunchConfigurationConstants.ATTR_GDB_INIT,
                IMILaunchConfigurationConstants.DEBUGGER_GDB_INIT_DEFAULT);

        if (iniFile.equals("") || !new File(iniFile).exists())
            return;

        try {
            executeGDBScript("GDB commands", configuration, dtargets,
                    getExtraCommands(configuration, "source " + fixPath(iniFile)), monitor);

        } catch (Exception e) {
            MultiStatus status = new MultiStatus(getPluginID(),
                    ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR,
                    "Executing GDB startup file", e);
            CDebugCorePlugin.log(status);
        }
    }

    // Do we need to upload the file to the target?
    protected void uploadFile(IProgressMonitor monitor, ILaunchConfiguration configuration)
            throws CoreException {

    }

    // We need to override the parser to be able to handle output "monitor"
    // commands.
    private void patchSession(ILaunchConfiguration configuration) throws CoreException {
        if (getAppConsole(configuration))
            return;
        ICDITarget[] dtargets = dsession.getTargets();
        for (int i = 0; i < dtargets.length; ++i) {
            Target target = (Target) dtargets[i];
            MISession miSession = target.getMISession();

            miSession.setMIParser(new MIParser() {

                @Override
                public MIOutput parse(String buffer) {
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
                    for (int i = 0; i < oobs.length; i++) {
                        if (oobs[i] instanceof MITargetStreamOutput) {
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
    private void setupTargets(ICDITarget[] dtargets) {
        for (int i = 0; i < dtargets.length; ++i) {
            Target target = (Target) dtargets[i];
            MISession miSession = target.getMISession();

            /*
             * We're running against a GDB server.
             */
            // miSession.getMIInferior().setIsRemoteInferior(true);
            /*
             * Tell CDT not to try to figure out the process ID! It makes no sense for embedded.
             */
            miSession.getMIInferior().setInferiorPID(-1);
        }
    }

    private void queryTargetState(ICDITarget[] dtargets) {
        // Try to detect if we have been attach/connected via "target remote
        // localhost:port"
        // or "attach" and set the state to be suspended.
        for (int i = 0; i < dtargets.length; ++i) {
            Target target = (Target) dtargets[i];
            MISession miSession = target.getMISession();
            CommandFactory factory = miSession.getCommandFactory();
            try {
                MIStackListFrames frames = factory.createMIStackListFrames();
                miSession.postCommand(frames, 1000);
                MIInfo info = frames.getMIInfo();
                if (info == null) {
                    throw new MIException("GDB state query failed"); //$NON-NLS-1$
                } else {
                    // @@@ We have to manually set the suspended state since we
                    // have some stackframes
                    miSession.getMIInferior().setSuspended();
                    miSession.getMIInferior().update();
                }
            } catch (MIException e) {
                // If an exception is thrown that means ok
                // we did not attach/connect to any target.
            }
        }
    }

    public void executeGDBScript(String scriptSource, ILaunchConfiguration configuration,
            ICDITarget[] dtargets, String[] extraCommands2, IProgressMonitor monitor)
                    throws CoreException {
        // Try to execute any extract command
        String[] commands = extraCommands2;
        for (int i = 0; i < dtargets.length; ++i) {
            Target target = (Target) dtargets[i];
            final MISession miSession = target.getMISession();
            // Could we manage to add back the prompt here by creating or
            // ownMIParser? It wouldn't be pretty...
            // MIParser m=new EmbeddedMIParser();
            // miSession.setMIParser(m);
            for (int j = 0; j < commands.length; ++j) {
                try {
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
                    while (t.isAlive()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        if (monitor.isCanceled()) {
                            // stop session
                            miSession.terminate();
                        }
                    }

                    if (result.result != null) {
                        throw result.result;
                    }

                    MIInfo info = cli.getMIInfo();
                    if (info == null) {
                        throw new MIException("Timeout: " + commands[j]); //$NON-NLS-1$
                    }
                } catch (MIException e) {
                    MultiStatus status = new MultiStatus(getPluginID(),
                            ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR,
                            "Failed command: " + commands[j], e);
                    status.add(new Status(IStatus.ERROR, getPluginID(),
                            ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR,
                            e == null ? "" : e.getLocalizedMessage(), //$NON-NLS-1$
                            e));
                    CDebugCorePlugin.log(status);
                }
            }
        }
    }

    protected String getPluginID() {
        return LaunchPlugin.PLUGIN_ID;
    }

    protected String[] getExtraCommands(ILaunchConfiguration configuration, String commands2)
            throws CoreException {
        String commands = commands2;
        if (commands != null && commands.length() > 0) {
            StringTokenizer st = new StringTokenizer(commands, "\r\n");
            String[] cmds = new String[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); ++i) {
                cmds[i] = st.nextToken();
            }
            return cmds;
        }
        return new String[0];
    }

    /**
     * embedded targets should not rebuild before launching the debugger as flash programming might
     * be done out of bands.
     */
    public boolean buildForLaunch(ILaunchConfiguration configuration, String mode,
            IProgressMonitor monitor) throws CoreException {
        return false;
    }

    /**
     * Translate Windows speak to host GDB speak(e.g. CygWin or MinGW).
     */
    abstract public String fixPath(String line);

    @SuppressWarnings("deprecation")
    protected IPath verifyProgramPath(ILaunchConfiguration config, ICProject project)
            throws CoreException {
        IPath p = getProgramPath(config);
        boolean dummy = false;
        if (p == null) {
            // no executable specified, we just need something to silence
            // the angry CDT code, which doesn't have a concept of launching
            // a debug session without an executable
            p = new Path("dummy.elf");
            dummy = true;
        }

        // always exercise this statement.
        File f;
        IPath path_to_return;

        if (p.isAbsolute()) {
            f = new File(p.toOSString());
            path_to_return = p;
        } else {
            // Convert to absolute path.
            // That will handle cases where path is like ..\a\b\c
            f = new File(project.getResource().getLocation().toFile(), p.toOSString());
            path_to_return = new Path(f.getAbsolutePath());
        }

        // Handle cases with virtual/linked paths. Strangely same code doesn't
        // work properly with physical relative paths, consequently we have to
        // handle those cases separately.
        if (!dummy && !f.exists()) {
            path_to_return = project.getProject().getFile(p).getLocation();
            f = path_to_return.toFile();
        }

        if (!dummy && !f.exists()) {
            abort(LaunchMessages.getString("AbstractCLaunchDelegate.Program_file_does_not_exist"), //$NON-NLS-1$
                    new FileNotFoundException(LaunchMessages.getFormattedString(
                            "AbstractCLaunchDelegate.PROGRAM_PATH_not_found", f.toString())), //$NON-NLS-1$
                    ICDTLaunchConfigurationConstants.ERR_PROGRAM_NOT_EXIST);
            return null;
        }

        return path_to_return;
    }

    @Override
    protected IBinaryObject verifyBinary(ICProject proj, IPath exePath) throws CoreException {

        try {
            IBinaryObject tmp = super.verifyBinary(proj, exePath);
            if (tmp == null) {
                /* CDT 7 started returning null from verifyBinary()... */
                throw new Exception();
            }
            return tmp;
        } catch (Exception e) {
            return new BinaryObjectAdapter(null, exePath, -1 // none of CDT's business
            ) {
                IAddressFactory factory = new FakeAddressFactory();

                @Override
                public IAddressFactory getAddressFactory() {
                    return factory;
                }

                @Override
                protected BinaryObjectInfo getBinaryObjectInfo() {
                    return null; // seems to work...
                }

                @Override
                public ISymbol[] getSymbols() {
                    return null; // seems to work
                }
            };

        }
    }

    public File getStartDir() {
        return myProject.getResource().getLocation().toFile();
    }

    public void handleDebugEvents(ICDIEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            ICDIEvent event = events[i];
            if (event instanceof ICDIDestroyedEvent) {
            }
        }
    }

    /** Convenient spot for subclasses to destroy things belonging to this event */
    protected void debugSessionEnded() {
    }

    public static List COMserialport() {
        List<String> list = new ArrayList<String>();
        try {
            Enumeration portIdEnum = CommPortIdentifier.getPortIdentifiers();
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
        if (list.size() < 1) {
            list.add("Please connect to EM Starter Kit");
        }
        return list;
    }

}
