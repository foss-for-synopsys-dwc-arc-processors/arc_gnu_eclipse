/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.cdt.dsf.concurrent.CountingRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.debug.service.IMemory.IMemoryDMContext;
import org.eclipse.cdt.dsf.gdb.launching.FinalLaunchSequence;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.IGDBMemory;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMIContainerDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIProcesses;
import org.eclipse.cdt.dsf.mi.service.MIBreakpointsManager;
import org.eclipse.cdt.dsf.mi.service.MIProcesses;
import org.eclipse.cdt.dsf.mi.service.command.commands.CLICommand;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;

import com.arc.embeddedcdt.LaunchPlugin;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;

/**
 * Sequence executed by DSF that has steps to launch GDB and GDB servers, pass arguments to GDB and
 * initialize DSF services.
 * 
 * Class is based on the implementation of the final launch sequence for the Jtag hardware debugging
 * <code>org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence</code>.
 * 
 * Adds specific steps to <code>org.eclipse.cdt.dsf.gdb.launching.FinalLaunchSequence</code> necessary
 * for initializing remote target and remote debugging.
 */
public class ArcFinalLaunchSequence extends FinalLaunchSequence {

    private void queueCommands(List<String> commands, RequestMonitor rm) {
        if (!commands.isEmpty()) {
            commandControl.queueCommand(
                    new CLICommand<MIInfo>(commandControl.getContext(), composeCommand(commands)),
                    new DataRequestMonitor<MIInfo>(getExecutor(), rm));
        } else {
            rm.done();
        }
    }

    private IGDBControl commandControl;
    private IGDBBackend gdbBackend;
    private IMIProcesses procService;

    private GdbServerBackend serverBackend;
    private DsfServicesTracker tracker;
    private IMIContainerDMContext containerCtx;
    private DsfSession session;
    private GdbLaunch launch;

    public ArcFinalLaunchSequence(DsfSession session, Map<String, Object> attributes,
            RequestMonitorWithProgress rm) {
        super(session, attributes, rm);
        this.session = session;
    }

    protected IMIContainerDMContext getContainerContext() {
        return containerCtx;
    }

    protected void setContainerContext(IMIContainerDMContext ctx) {
        containerCtx = ctx;
    }

    protected static final String GROUP_ARC = "GROUP_ARC";

    @Override
    protected String[] getExecutionOrder(String group) {
        if (GROUP_TOP_LEVEL.equals(group)) {
            /*
             * It is necessary to create new ArrayList here and not just use ArrayList returned by
             * Arrays.asList(). The reason is that Arrays.asList() returns an ArrayList which has a
             * reference to the source array in it and when ArrayList is modified, the source array
             * is modified as well. Apparently somewhere in DSF they rely on that this array is
             * immutable because if I use ArrayList returned from Arrays.asList() everything hangs
             * after the first step.
             */
            List<String> orderList = new ArrayList<String>(
                    Arrays.asList(super.getExecutionOrder(GROUP_TOP_LEVEL)));
            orderList.removeAll(Arrays.asList(new String[] { "stepNewProcess", }));
            orderList.add(orderList.indexOf("stepDataModelInitializationComplete"), GROUP_ARC);
            return orderList.toArray(new String[orderList.size()]);
        }

        if (GROUP_ARC.equals(group)) {
            return new String[] {
                    "stepInitializeArcFinalLaunchSequence",
                    "stepCreateGdbServerService",
                    "stepOpenGdbServerConsole",
                    "stepStartTerminal",
                    "stepSpecifyFileToDebug",
                    "stepUserInitCommands",
                    "stepConnectToTarget",
                    "stepUpdateContainer",
                    "stepInitializeMemory",
                    "stepSetEnvironmentVariables",
                    "stepStartTrackingBreakpoints",
                    "stepStopScript",
                    "stepResumeScript",
                    "stepUserDebugCommands",
                    "stepArcCleanup", };
        }
        return super.getExecutionOrder(group);
    }

    // Initialize the members of the class. Necessary for the rest of the sequence to complete.
    @Execute
    public void stepInitializeArcFinalLaunchSequence(RequestMonitor rm) {
        tracker = new DsfServicesTracker(LaunchPlugin.getBundleContext(), getSession().getId());
        gdbBackend = tracker.getService(IGDBBackend.class);
        if (gdbBackend == null) {
            rm.done(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, -1,
                    "Cannot obtain GDBBackend service", null));
            return;
        }

        commandControl = tracker.getService(IGDBControl.class);
        if (commandControl == null) {
            rm.done(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, -1,
                    "Cannot obtain control service", null));
            return;
        }

        procService = tracker.getService(IMIProcesses.class);
        if (procService == null) {
            rm.done(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, -1,
                    "Cannot obtain process service", null));
            return;
        }

        launch = (GdbLaunch) session.getModelAdapter(ILaunch.class);
        if (launch == null) {
            rm.done(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, -1,
                    "Cannot obtain launch", null));
            return;
        }

        /*
         * When we are starting to debug a new process, the container is the default process
         * used by GDB. We don't have a pid yet, so we can simply create the container with 
         * the UNIQUE_GROUP_ID.
         */
        setContainerContext(procService.createContainerContextFromGroupId(
                commandControl.getContext(), MIProcesses.UNIQUE_GROUP_ID));

        rm.done();
    }

    /**
     * Rollback method for {@link #stepInitializeJTAGFinalLaunchSequence()}.
     * 
     * If sequence can not be completed, for example, there was an error, it is rolled back
     * to run a clean up for steps that need it.
     * All service trackers must be disposed (or closed), otherwise it could lead to services'
     * references' leaks.
     */
    @RollBack("stepInitializeArcFinalLaunchSequence")
    public void rollBackInitializeFinalLaunchSequence(RequestMonitor rm) {
        if (tracker != null)
            tracker.dispose();
        tracker = null;
        rm.done();
    }

    @Execute
    public void stepCreateGdbServerService(final RequestMonitor rm) {
        serverBackend = launch.getServiceFactory().createService(GdbServerBackend.class,
                launch.getSession(), launch.getLaunchConfiguration());
        if (serverBackend != null) {
            serverBackend.initialize(rm);
        } else {
            rm.setStatus(new Status(Status.ERROR, LaunchPlugin.PLUGIN_ID,
                    "Unable to start GdbServerBackend"));
            rm.done();
        }
    }

    @Execute
    public void stepOpenGdbServerConsole(final RequestMonitor rm) {
        try {
            serverBackend.initializeServerConsole();
        } catch (CoreException|InterruptedException e) {
            rm.setStatus(new Status(Status.ERROR, LaunchPlugin.PLUGIN_ID,
                    "Unable to initialize GdbServer console", e));
        } finally {
            rm.done();
        }
    }

    @Execute
    public void stepSpecifyFileToDebug(final RequestMonitor rm) {
        ConfigurationReader cfgReader = new ConfigurationReader(launch.getLaunchConfiguration());
        String command = "file " + cfgReader.getProgramName();
        queueCommands(Arrays.asList(command), rm);
    }

    // Execute user define 'initialize' commands
    @Execute
    public void stepUserInitCommands(final RequestMonitor rm) {
        try {
            ConfigurationReader cfgReader = new ConfigurationReader(launch.getLaunchConfiguration());
            String userCmd = cfgReader.getUserInitCommands();
            userCmd = VariablesPlugin.getDefault().getStringVariableManager()
                    .performStringSubstitution(userCmd);
            if (userCmd.length() > 0) {
                String[] commands = userCmd.split("\\r?\\n");

                CountingRequestMonitor crm = new CountingRequestMonitor(getExecutor(), rm);
                crm.setDoneCount(commands.length);
                for (int i = 0; i < commands.length; ++i) {
                    commandControl.queueCommand(
                            new CLICommand<MIInfo>(commandControl.getContext(), commands[i]),
                            new DataRequestMonitor<MIInfo>(getExecutor(), crm));
                }
            } else {
                rm.done();
            }
        } catch (CoreException e) {
            rm.setStatus(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, -1,
                    "Cannot run user defined init commands", e));
            rm.done();
        }
    }

    /*
     * Now that we are connected to the target, we should update our container to properly fill in
     * its pid.
     */
    @Execute
    public void stepUpdateContainer(RequestMonitor rm) {
        String groupId = getContainerContext().getGroupId();
        setContainerContext(procService
                .createContainerContextFromGroupId(commandControl.getContext(), groupId));
        rm.done();
    }

    @Execute
    public void stepSetEnvironmentVariables(RequestMonitor rm) {
        boolean clear = false;
        Properties properties = new Properties();
        try {
            clear = gdbBackend.getClearEnvironment();
            properties = gdbBackend.getEnvironmentVariables();
        } catch (CoreException e) {
            rm.setStatus(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, -1,
                    "Cannot get environment information", e));
            rm.done();
            return;
        }

        if (clear == true || properties.size() > 0) {
            commandControl.setEnvironment(properties, clear, rm);
        } else {
            rm.done();
        }
    }

    /*
     * Start tracking the breakpoints once we know we are connected to the target (necessary for
     * remote debugging)
     */
    @Execute
    public void stepStartTrackingBreakpoints(final RequestMonitor rm) {
        MIBreakpointsManager bpmService = tracker.getService(MIBreakpointsManager.class);
        bpmService.startTrackingBpForProcess(getContainerContext(), rm);
    }

    /*
     * Run user defined commands to start debugging
     */
    @Execute
    public void stepUserDebugCommands(final RequestMonitor rm) {
        try {
            ConfigurationReader cfgReader = new ConfigurationReader(launch.getLaunchConfiguration());
            String userCmd = cfgReader.getUserRunCommands();
            if (!userCmd.isEmpty()) {
                userCmd = VariablesPlugin.getDefault().getStringVariableManager()
                        .performStringSubstitution(userCmd);
                String[] commands = userCmd.split("\\r?\\n");

                CountingRequestMonitor crm = new CountingRequestMonitor(getExecutor(), rm);
                crm.setDoneCount(commands.length);
                for (int i = 0; i < commands.length; ++i) {
                    commandControl.queueCommand(
                            new CLICommand<MIInfo>(commandControl.getContext(), commands[i]),
                            new DataRequestMonitor<MIInfo>(getExecutor(), crm));
                }
            } else {
                rm.done();
            }
        } catch (CoreException e) {
            rm.setStatus(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, -1,
                    "Cannot run user defined run commands", e));
            rm.done();
        }
    }

    private String composeCommand(Collection<String> commands) {
        if (commands.isEmpty())
            return null;
        StringBuffer sb = new StringBuffer();
        Iterator<String> it = commands.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
        }
        return sb.toString();
    }

    /**
     * Clean up after completing the sequence. All service trackers must be disposed (or closed),
     * otherwise it could lead to services' references' leaks.
     */
    @Execute
    public void stepArcCleanup(final RequestMonitor requestMonitor) {
        tracker.dispose();
        tracker = null;
        requestMonitor.done();
    }

    @Execute
    public void stepInitializeMemory(final RequestMonitor rm) {
        IGDBMemory memory = tracker.getService(IGDBMemory.class);
        IMemoryDMContext memContext = DMContexts.getAncestorOfType(getContainerContext(),
                IMemoryDMContext.class);
        if (memory == null || memContext == null) {
            rm.done();
            return;
        }
        memory.initializeMemoryData(memContext, rm);
    }

    @Execute
    public void stepConnectToTarget(RequestMonitor rm) {
        String command = serverBackend.getCommandToConnect();
        queueCommands(Arrays.asList(command), rm);
    }

    /*
     * If we're running in DEBUG_MODE, place a temporary breakpoint at the symbol specified in
     * launch configuration.
     */
    @Execute
    public void stepStopScript(final RequestMonitor rm) {
        if (!launch.getLaunchMode().equals(ILaunchManager.DEBUG_MODE)) {
            rm.done();
            return;
        }
        ConfigurationReader cfgReader = new ConfigurationReader(launch.getLaunchConfiguration());
        boolean stopAtMain = cfgReader.doStopAtMain();
        if (stopAtMain) {
            String stopSymbol = cfgReader.getStopSymbol();
            queueCommands(Arrays.asList("tbreak " + stopSymbol), rm);
        } else {
            rm.done();
        }
    }

    @Execute
    public void stepResumeScript(final RequestMonitor rm) {
        queueCommands(Arrays.asList("continue"), rm);
    }

    @Execute
    public void stepStartTerminal(final RequestMonitor rm) {
        ConfigurationReader cfgReader = new ConfigurationReader(launch.getLaunchConfiguration());
        ArcGdbServer gdbServer = cfgReader.getGdbServer();
        String serialPort = cfgReader.getComPort();
        if (cfgReader.doLaunchTerminal()
                && gdbServer != ArcGdbServer.NSIM && !serialPort.isEmpty()) {
            new TerminalService(session).initialize(rm);
        } else {
            rm.done();
        }
    }
}
