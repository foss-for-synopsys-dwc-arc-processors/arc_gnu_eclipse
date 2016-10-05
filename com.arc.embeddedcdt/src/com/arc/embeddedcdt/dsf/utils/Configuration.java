/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf.utils;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;

/**
 * Utility class with convenient methods for getting options' values from launch configuration.
 *
 * Instead of calling launchConfiguration.getAttribute() surrounded with try/catch and specifying
 * the attribute to be used as a key every time methods from this class can be used.
 */
public class Configuration {

    private static String getAttribute(ILaunchConfiguration lc, String attribute,
            String defaultValue) {
        try {
            return lc.getAttribute(attribute, defaultValue);
        } catch (CoreException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private static boolean getAttribute(ILaunchConfiguration lc, String attribute, boolean defaultValue) {
        try {
            return lc.getAttribute(attribute, defaultValue);
        } catch (CoreException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private static int getAttribute(ILaunchConfiguration lc, String attribute, int defaultValue) {
        try {
            return lc.getAttribute(attribute, defaultValue);
        } catch (CoreException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static String getProgramName(ILaunchConfiguration lc) {
        return getAttribute(lc, ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, "");
    }

    public static int getDebuggerPluginVersion(ILaunchConfiguration lc){
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_DEBUGGER_PLUGIN_VERSION,
                LaunchConfigurationConstants.UNREAL_DEBUGGER_PLUGIN_VERSION_NUMBER);
    }

    public static String getGdbPath(ILaunchConfiguration lc) {
        return getAttribute(lc, IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, "");
    }

    public static String getOpenOcdPath(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_DEBUGGER_OPENOCD_BIN_PATH, "");
    }

    public static String getOpenOcdConfig(ILaunchConfiguration lc) {
        return getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH, "");
    }

    public static String getHostAddress(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_DEBUGGER_GDB_ADDRESS,
                LaunchConfigurationConstants.DEFAULT_GDB_HOST);
    }

    public static String getGdbServerPort(ILaunchConfiguration lc) {
        ArcGdbServer gdbServer = getGdbServer(lc);
        String defaultValue = "";
        switch (gdbServer) {
        case JTAG_OPENOCD:
            defaultValue = LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT;
            break;
        case JTAG_ASHLING:
            defaultValue = LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT;
            break;
        case NSIM:
            defaultValue = LaunchConfigurationConstants.DEFAULT_NSIM_PORT;
            break;
        default:
        }
        return getAttribute(lc, IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT,
                defaultValue);
    }

    public static String getComPort(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_DEBUGGER_COM_PORT, "");
    }

    public static boolean doLaunchTerminal(ILaunchConfiguration lc) {
        return Boolean.parseBoolean(getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_TERMINAL_DEFAULT, "true"));
    }

    public static ArcGdbServer getGdbServer(ILaunchConfiguration lc) {
        return ArcGdbServer.fromString(
                getAttribute(lc, LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS,
                        ArcGdbServer.DEFAULT_GDB_SERVER.toString()));
    }

    public static String getNsimPath(ILaunchConfiguration lc) {
        return getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH, "");
    }

    public static String getNsimTcfPath(ILaunchConfiguration lc) {
        return getAttribute(lc,
                LaunchConfigurationConstants.ATTR_NSIM_TCF_FILE, "");
    }

    public static boolean getNsimUseTcf(ILaunchConfiguration lc) {
        return Boolean.parseBoolean(getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMTCF, "true"));
    }

    public static String getNsimPropsPath(ILaunchConfiguration lc) {
        return getAttribute(lc,
                LaunchConfigurationConstants.ATTR_NSIM_PROP_FILE, "");
    }

    public static boolean getNsimUseProps(ILaunchConfiguration lc) {
        return Boolean.parseBoolean(getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMPROPS, "true"));
    }

    public static boolean getNsimUseNsimHostlink(ILaunchConfiguration lc) {
        return Boolean.parseBoolean(getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMHOSTLINK, "true"));
    }

    public static boolean getNsimUseJit(ILaunchConfiguration lc) {
        return Boolean.parseBoolean(getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMJIT, "false"));
    }

    public static String getNsimJitThreads(ILaunchConfiguration lc) {
        String jitThreads = getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMJITTHREAD, "1");
        return jitThreads;
    }

    public static boolean getNsimSimulateMemoryExceptions(ILaunchConfiguration lc) {
        return Boolean.parseBoolean(getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMMEMOEXPT, "true"));
    }

    public static boolean getNsimSimulateExceptions(ILaunchConfiguration lc) {
        return Boolean.parseBoolean(getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMENABLEEXPT, "true"));
    }

    public static boolean getNsimSimulateInvalidInstructionExceptions(ILaunchConfiguration lc) {
        return Boolean.parseBoolean(getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_USE_NSIMINVAINSTRUEXPT, "true"));
    }

    public static String getNsimWorkingDirectoryPath(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_WORKING_DIRECTORY,
                (String)null);
    }

    public static String getAshlingPath(ILaunchConfiguration lc) {
        String defaultValue = System.getProperty("os.name").toLowerCase().indexOf("windows") > -1
                ? LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_WINDOWS
                : LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_LINUX;
        return getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH,
                defaultValue);
    }

    public static String getAshlingXmlPath(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_ASHLING_XML_PATH, "");
    }

    public static String getAshlingTDescPath(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_ASHLING_TDESC_PATH, "");
    }

    public static String getAshlingJtagFrequency(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_JTAG_FREQUENCY, "");
    }

    public static String getCustomGdbServerPath(ILaunchConfiguration lc) {
        return getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_CUSTOM_GDBSERVER_BIN_PATH, "");
    }

    public static String getCustomGdbServerArgs(ILaunchConfiguration lc) {
        return getAttribute(lc,
                LaunchConfigurationConstants.ATTR_DEBUGGER_CUSTOM_GDBSERVER_COMMAND, "");
    }

    public static boolean doStopAtMain(ILaunchConfiguration lc) {
        return getAttribute(lc,
                ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, true);
    }

    public static String getStopSymbol(ILaunchConfiguration lc) {
        return getAttribute(lc, ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL,
                ICDTLaunchConfigurationConstants.DEBUGGER_STOP_AT_MAIN_SYMBOL_DEFAULT);
    }

    public static String getUserInitCommands(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_INIT, "");
    }

    public static String getUserRunCommands(ILaunchConfiguration lc) {
        return getAttribute(lc, LaunchConfigurationConstants.ATTR_DEBUGGER_COMMANDS_RUN, "");
    }

    public static FtdiDevice getFtdiDevice(ILaunchConfiguration lc) {
        return FtdiDevice.valueOf(getAttribute(lc, LaunchConfigurationConstants.ATTR_FTDI_DEVICE,
                LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE_NAME));
    }

    public static FtdiCore getFtdiCore(ILaunchConfiguration lc) {
        return FtdiCore.valueOf(getAttribute(lc, LaunchConfigurationConstants.ATTR_FTDI_CORE,
                LaunchConfigurationConstants.DEFAULT_FTDI_CORE_NAME));
    }
}
