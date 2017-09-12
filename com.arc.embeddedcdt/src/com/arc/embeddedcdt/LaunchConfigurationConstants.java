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

package com.arc.embeddedcdt;

import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;

public interface LaunchConfigurationConstants {
    static final String LAUNCH_ID = "com.arc.embeddedcdt"; //$NON-NLS-1$
    static final String ATTR_DEBUGGER_CONFIG = LAUNCH_ID + ".debugger_config";
    static final String ATTR_DEBUGGER_INIT_TARGET = LAUNCH_ID + ".debugger_init_target";
    static final String ATTR_DEBUGGER_APP_CONSOLE = LAUNCH_ID + ".debugger_app_console";
    static final boolean ATTR_DEBUGGER_APP_CONSOLE_DEFAULT = false;
    static final String ATTR_DEBUGGER_BUILD_BEFORE_LAUNCH = LAUNCH_ID
            + ".debugger_build_before_launch";
    static final boolean ATTR_DEBUGGER_BUILD_BEFORE_LAUNCH_DEFAULT = true;

    static final String ATTR_FILE_FORMAT_VERSION =
        LAUNCH_ID + ".debugger_launch_file_format_version";
    static final String ATTR_TIMESTAMP = LAUNCH_ID + ".timestamp";
    String ATTR_DEBUGGER_COMMANDS_INIT = LAUNCH_ID + ".debugger_init_commands"; //$NON-NLS-1$
    String ATTR_DEBUGGER_COMMANDS_RUN = LAUNCH_ID + ".debugger_run_commands"; //$NON-NLS-1$
    String ATTR_DEBUGGER_COMMANDS_LAUNCH = LAUNCH_ID + ".debugger_lauch_commands"; //$NON-NLS-1$
    String ATTR_DEBUGGER_EXTERNAL_TOOLS = LAUNCH_ID + ".debugger_external_tools"; //$NON-NLS-1$
    String ATTR_DEBUGGER_COM_PORT = LAUNCH_ID + ".debugger_com_port"; // $NON-NLS-1$
    String ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH = LAUNCH_ID
            + ".debugger_external_tools_oepnocd_path"; //$NON-NLS-1$
    String ATTR_DEBUGGER_OPENOCD_BIN_PATH = LAUNCH_ID + ".debugger_openocd_bin_path";
    String ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH = LAUNCH_ID
            + ".debugger_external_tools_ashling_path"; //$NON-NLS-1$
    String ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH = LAUNCH_ID
            + ".debugger_external_tools_nsim_path"; //$NON-NLS-1$
    String ATTR_DEBUGGER_NSIM_WORKING_DIRECTORY = LAUNCH_ID + ".nsim_working_directory";
    String ATTR_DEBUGGER_NSIM_USE_DEFAULT_DIRECTORY = LAUNCH_ID + ".nsim_use_default_working_dir";
    String ATTR_DEBUGGER_USE_NSIMTCF = LAUNCH_ID + ".debugger_use_nsimtcf";
    String ATTR_DEBUGGER_USE_NSIMPROPS = LAUNCH_ID + ".debugger_use_nsimprops";
    String ATTR_DEBUGGER_USE_NSIMJITTHREAD = LAUNCH_ID + ".debugger_use_nsimjitthread";
    String ATTR_DEBUGGER_USE_NSIMJIT = LAUNCH_ID + ".debugger_use_nsimjit";
    String ATTR_DEBUGGER_USE_NSIMHOSTLINK = LAUNCH_ID + ".debugger_use_nsimhostlink";
    String ATTR_DEBUGGER_USE_NSIMMEMOEXPT = LAUNCH_ID + ".debugger_use_nsimmemoexpt";
    String ATTR_DEBUGGER_USE_NSIMENABLEEXPT = LAUNCH_ID + ".debugger_use_nsimenableexpt";
    String ATTR_DEBUGGER_USE_NSIMINVAINSTRUEXPT = LAUNCH_ID + ".debugger_use_nsiminvaintruexpt";
    String ATTR_DEBUGGER_TERMINAL_DEFAULT = LAUNCH_ID + ".debugger_putty_default";
    String ATTR_DEBUGGER_GDB_ADDRESS = LAUNCH_ID + ".debugger_gdb_address"; //$NON-NLS-1$
    String ATTR_NSIM_PROP_FILE = LAUNCH_ID + ".nsim_prop_file"; //$NON-NLS-1$
    String ATTR_NSIM_TCF_FILE = LAUNCH_ID + ".nsim_tcf_file"; //$NON-NLS-1$
    String ATTR_ASHLING_XML_PATH = LAUNCH_ID + ".ashling_xml_path"; //$NON-NLS-1$
    String ATTR_ASHLING_TDESC_PATH = LAUNCH_ID + ".ashling_tdesc_path"; //$NON-NLS-1$
    String ATTR_JTAG_FREQUENCY = LAUNCH_ID + ".jtag_frequency"; //$NON-NLS-1$
    String ATTR_FTDI_DEVICE = LAUNCH_ID + ".ftdi_device"; //$NON-NLS-1$
    String ATTR_FTDI_CORE = LAUNCH_ID + ".ftdi_core"; //$NON-NLS-1$

    static final int UNREAL_FILE_FORMAT_VERSION = -1;

    /* This file format number should be incremented when incompatible changes appear in the
       debugger plug-in. */
    static final int CURRENT_FILE_FORMAT_VERSION = 2;

    // Default option values
    static final String DEFAULT_OPENOCD_PORT = "49105";
    static final String DEFAULT_OPELLAXD_PORT = "49105";
    static final String DEFAULT_NSIM_PORT = "49105";
    static final String DEFAULT_GDB_HOST = "localhost";
    static final String DEFAULT_OPENOCD_BIN_PATH_LINUX = "/usr/local/bin/openocd";
    static final String DEFAULT_OPENOCD_CFG_PATH_LINUX = "/usr/local/share/openocd/scripts/board/snps_em_sk.cfg";
    static final FtdiDevice DEFAULT_FTDI_DEVICE = FtdiDevice.EM_SK_v2;
    static final FtdiCore DEFAULT_FTDI_CORE = DEFAULT_FTDI_DEVICE.getCores().get(0);
    static final String DEFAULT_FTDI_DEVICE_NAME = DEFAULT_FTDI_DEVICE.name();
    static final String DEFAULT_FTDI_CORE_NAME = DEFAULT_FTDI_CORE.name();

    // Ashling
    static final String ASHLING_DEFAULT_PATH_WINDOWS = "C:\\AshlingOpellaXDforARC\\ash-arc-gdb-server.exe";
    static final String ASHLING_DEFAULT_PATH_LINUX = "/usr/bin/ash-arc-gdb-server";
    static final String ASHLING_DEFAULT_XML_FILE = "arc-em-cpu.xml";
    static final String ASHLING_DEFAULT_TDESC_FILE = "opella-arcem-tdesc.xml";

    //Custom Gdbserver
    String ATTR_DEBUGGER_CUSTOM_GDBSERVER_BIN_PATH =  LAUNCH_ID + ".debugger_custom_gdbsever_bin_path";
    String ATTR_DEBUGGER_CUSTOM_GDBSERVER_COMMAND =  LAUNCH_ID + ".debugger_custom_gdbsever_command";

    public static final String ID_LAUNCH_C_APP = "com.arc.embeddedcdt.idleNative";

}
