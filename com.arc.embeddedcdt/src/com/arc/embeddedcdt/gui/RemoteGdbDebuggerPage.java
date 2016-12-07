/*******************************************************************************
 * Copyright (c) 2006, 2015 PalmSource, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Ewa Matejska (PalmSource)
 * 
 * Referenced GDBDebuggerPage code to write this.
 * Anna Dushistova (Mentor Graphics) - moved to org.eclipse.cdt.launch.remote.tabs
 * Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.GdbDebuggerPage;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.internal.launch.remote.Messages;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.common.LaunchFileFormatVersionChecker;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;
import com.arc.embeddedcdt.dsf.utils.ConfigurationWriter;
import com.arc.embeddedcdt.common.FtdiCore;
import com.arc.embeddedcdt.common.FtdiDevice;

/**
 * The dynamic debugger tab for remote launches using gdb server. The pageGui.gdbServer settings are used to
 * start a pageGui.gdbServer session on the remote and then to connect to it from the host. The DSDP-TM
 * project is used to accomplish this.
 */
@SuppressWarnings("restriction")
public class RemoteGdbDebuggerPage extends GdbDebuggerPage {
    private static final String DEFAULT_OOCD_BIN;
    private static final String DEFAULT_OOCD_CFG;
    static {
        if (isWindowsOs()) {
            DEFAULT_OOCD_BIN = getIdeRootDirPath() + "\\bin\\openocd.exe";
            DEFAULT_OOCD_CFG = getIdeRootDirPath()
                    + "\\share\\openocd\\scripts\\board\\snps_em_sk.cfg";
        } else {
            String predefinedPath = getIdeBinDir();
            // Checking for OpenOCD binary presence in default path
            if (new File(predefinedPath).isDirectory()) {
                DEFAULT_OOCD_BIN = predefinedPath + "openocd";
                DEFAULT_OOCD_CFG = getIdeRootDir() + "share/openocd/scripts/board/snps_em_sk.cfg";
            } else {
                DEFAULT_OOCD_BIN = LaunchConfigurationConstants.DEFAULT_OPENOCD_BIN_PATH_LINUX;
                DEFAULT_OOCD_CFG = LaunchConfigurationConstants.DEFAULT_OPENOCD_CFG_PATH_LINUX;
            }
        }
    }

    private static RemoteGdbDebuggerPageGui pageGui = new RemoteGdbDebuggerPageGui();
    protected Combo jtagFrequencyCombo;
    
    
    private FileFieldEditor customGdbBinaryPathEditor;
    private String openOcdBinaryPath;
    private String openOcdConfigurationPath;
    private Text customGdbCommandLineArgumentsText;
    private String customGdbPath;
    private String customGdbCommandLineArguments = null;
    private FileFieldEditor ashlingBinaryPathEditor;
    private FileFieldEditor nsimBinaryPathEditor;
    private FileFieldEditor nsimTcfPathEditor;
    private FileFieldEditor nsimPropertiesPathEditor;
    private FileFieldEditor ashlingXmlPathEditor;
    private FileFieldEditor ashlingTdescXmlPathEditor;
    private ARCWorkingDirectoryBlock workingDirectoryBlockNsim = new ARCWorkingDirectoryBlock();
    private String jtagFrequency = null;
    //private ArcGdbServer pageGui.gdbServer = ArcGdbServer.DEFAULT_GDB_SERVER;
    //private boolean createTabItemCom = false;
    private boolean createTabItemNsim = false;
    private boolean createTabItemGenericGdbServer = false;
    private String gdbPath = null;
    private boolean createTabItemComAshling = false;
    private boolean createTabItemCustomGdb = false;
    private String nsimPropertiesFilesLast = "";
    protected Button launchTcf;
    private boolean externalNsimPropertiesEnabled = true;
    private String nsimTcfFilesLast = "";
    private boolean externalNsimTcfToolsEnabled = true;
    private boolean externalNsimJitEnabled = true;
    private boolean externalNsimHostLinkToolsEnabled = true;
    private boolean externalNsimMemoryExceptionToolsEnabled = true;
    private boolean externalNsimEnableExceptionToolsEnabled = true;
    private boolean launchExternalNsimInvalidInstructionException = true;

    private String externalToolsAshlingPath = "";
    private String ashlingXmlPath = "";
    private String ashlingTdescPath = "";
    private String externalToolsNsimPath = "";
    private String hostName = "";
    private String portNumber = "";

    private String jitThread = "1";
    
    @Override
    public String getName() {
        return Messages.Remote_GDB_Debugger_Options;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
      super.setDefaults(configuration);
      ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
      configurationWriter.setGdbServerCommand(
          IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND_DEFAULT);
      configurationWriter.setGdbServerPort(
          IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT_DEFAULT);
      configurationWriter.setGdbServer(ArcGdbServer.DEFAULT_GDB_SERVER.toString());
      configurationWriter.setOpenOcdConfig(DEFAULT_OOCD_CFG);
      configurationWriter.setAshlingPath("");
      configurationWriter.setNsimPath("");
      configurationWriter.setDoLaunchTerminal(false);
      configurationWriter.setNsimDefaultPath(getNsimdrvDefaultPath());
      configurationWriter.setOpenOcdPath(DEFAULT_OOCD_BIN);
      configurationWriter.setAshlingJtagFrequency("");
      configurationWriter.setFtdiDevice(LaunchConfigurationConstants.DEFAULT_FTDI_DEVICE_NAME);
      configurationWriter.setFtdiCore(LaunchConfigurationConstants.DEFAULT_FTDI_CORE_NAME);
    }

    /**
     * Get default path to nSIM application nsimdrv.
     */
    private static String getNsimdrvDefaultPath() {
        String nsimHome = System.getenv("NSIM_HOME");
        if (nsimHome == null)
            return "";
        else {
            String path = nsimHome + java.io.File.separator + "bin" + java.io.File.separator
                    + "nsimdrv";
            if (isWindowsOs()) {
                return path + ".exe";
            } else {
                return path;
            }
        }
    }

    private static String getIdeRootDirPath() {
        String s = System.getProperty("eclipse.home.location");
        s = s.substring("file:/".length()).replace("/", "\\");
        String path = s + "\\..";
        try {
            return Paths.get(path).toRealPath().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void updateLaunchConfigurationDialogPublic(){
      updateLaunchConfigurationDialog();
    }
    
    public static String getDefaultGdbPath() {
        String gdbPath = "arc-elf32-gdb";
        String predefinedPath = getIdeBinDir();
        File predefinedPathFile = new File(predefinedPath);

        if (predefinedPathFile.isDirectory()) {
            File gdbFile = new File(predefinedPath + "arc-elf32-gdb");
            if (gdbFile.canExecute()) {
                gdbPath = gdbFile.getAbsolutePath();
            }
        }
        return gdbPath;
    }

    static String getIdeRootDir() {
        String eclipsehome = Platform.getInstallLocation().getURL().getPath();
        File predefinedPathToDirectory = new File(eclipsehome).getParentFile();
        return predefinedPathToDirectory + File.separator;
    }

    static String getIdeBinDir() {
        return getIdeRootDir() + "bin" + File.separator;
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        LaunchFileFormatVersionChecker.getInstance().check(configuration);
        createTabItemComAshling = false;
        createTabItemNsim = false;
        createTabItemGenericGdbServer = false;
        createTabItemCustomGdb = false;
        super.initializeFrom(configuration);
        ConfigurationReader configurationReader = new ConfigurationReader(configuration);
        gdbPath = configurationReader.getOrDefault(getDefaultGdbPath(), "",
            configurationReader.getGdbPath());
        fGDBCommandText.setText(gdbPath);
        openOcdBinaryPath = configurationReader.getOrDefault(DEFAULT_OOCD_BIN, "",
            configurationReader.getOpenOcdPath());
        jtagFrequency = configurationReader.getAshlingJtagFrequency();
        pageGui.ftdiDevice = configurationReader.getFtdiDevice();
        pageGui.ftdiCore = configurationReader.getFtdiCore();
        pageGui.gdbServer = configurationReader.getGdbServer();
        openOcdConfigurationPath = configurationReader.getOrDefault(DEFAULT_OOCD_CFG, "",
            configurationReader.getOpenOcdConfig());
        String defaultAshlingPath =
            isWindowsOs() ? LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_WINDOWS
                : LaunchConfigurationConstants.ASHLING_DEFAULT_PATH_LINUX;
        externalToolsAshlingPath =
            configurationReader.getOrDefault(defaultAshlingPath, "",
            configurationReader.getAshlingPath());
        String ashlingXmlFile = new File(defaultAshlingPath).getParentFile().getPath()
            + java.io.File.separator + "arc-cpu-em.xml";
        ashlingXmlPath = configurationReader.getOrDefault(ashlingXmlFile, "",
            configurationReader.getAshlingXmlPath());
        String defaultTDescPath = new File(defaultAshlingPath).getParentFile().getPath()
            + java.io.File.separator + "opella-arcem-tdesc.xml";
        ashlingTdescPath = configurationReader.getOrDefault(defaultTDescPath, "",
            configurationReader.getAshlingTDescPath());
        externalToolsNsimPath = configurationReader.getOrDefault(getNsimdrvDefaultPath(), "",
            configurationReader.getNsimPath());
        customGdbPath = configurationReader.getCustomGdbServerPath();
        customGdbCommandLineArguments = configurationReader.getCustomGdbServerArgs();

        workingDirectoryBlockNsim.initializeFrom(configuration);
        externalNsimJitEnabled = configurationReader.getNsimUseJit();
        externalNsimHostLinkToolsEnabled = configurationReader.getNsimUseNsimHostlink();
        externalNsimEnableExceptionToolsEnabled = configurationReader.getNsimSimulateExceptions();
        launchExternalNsimInvalidInstructionException =
            configurationReader.getNsimSimulateInvalidInstructionExceptions();
        externalNsimMemoryExceptionToolsEnabled =
            configurationReader.getNsimSimulateMemoryExceptions();
        externalNsimPropertiesEnabled = configurationReader.getNsimUseProps();
        externalNsimTcfToolsEnabled = configurationReader.getNsimUseTcf();
        nsimPropertiesFilesLast = configurationReader.getNsimPropsPath();
        nsimTcfFilesLast = configurationReader.getNsimTcfPath();
        jitThread = configurationReader.getNsimJitThreads();

        pageGui.externalToolsCombo.setText(pageGui.gdbServer.toString());

        if (!jtagFrequencyCombo.isDisposed()) {
            if (configurationReader.getAshlingJtagFrequency().isEmpty()) {
                jtagFrequencyCombo.setText(jtagFrequencyCombo.getItem(0));
            } else
                jtagFrequencyCombo.setText(jtagFrequency);
        }
        if (!pageGui.ftdiDeviceCombo.isDisposed())
            pageGui.ftdiDeviceCombo.setText(pageGui.ftdiDevice.toString());

        if (!pageGui.ftdiCoreCombo.isDisposed())
            pageGui.ftdiCoreCombo.setText(pageGui.ftdiCore.toString());
        // Set host and IP.
        portNumber = configurationReader.getGdbServerPort();
        pageGui.gdbServerPortNumberText.setText(portNumber);
        hostName = configurationReader.getHostAddress();
        if (groupGenericGdbServerContainer != null && !groupGenericGdbServerContainer.getGroup().isDisposed())
            pageGui.gdbServerIpAddressText.setText(hostName);

        int previous = pageGui.externalToolsCombo.indexOf(pageGui.gdbServer.toString());
        if (previous > -1)
            pageGui.externalToolsCombo.remove(previous);
        /*
         * Reading pageGui.gdbServer again from configuration because field pageGui.gdbServer might have been
         * changed by event handler called by extTools.remove(previous)
         */
        pageGui.gdbServer = configurationReader.getGdbServer();
        pageGui.externalToolsCombo.add(pageGui.gdbServer.toString(), 0);
        pageGui.externalToolsCombo.select(0);

        if (!jtagFrequencyCombo.isDisposed()) {
            if (!jtagFrequency.isEmpty()) {
                previous = jtagFrequencyCombo.indexOf(jtagFrequency);
                if (previous > -1)
                    jtagFrequencyCombo.remove(previous);
                jtagFrequencyCombo.add(jtagFrequency, 0);
                jtagFrequencyCombo.select(0);
            }
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);
        ConfigurationReader configurationReader = new ConfigurationReader(configuration);
        final String programName = configurationReader.getProgramName();
        configuration.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME,
            programName.replace('\\', '/'));
        if (!groupNsimContainer.getGroup().isDisposed()) {
            workingDirectoryBlockNsim.performApply(configuration);
        }
        String str = pageGui.gdbServerPortNumberText.getText();
        str = str.trim();

        ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
        configurationWriter.setGdbServerPort(str);
        String nsimDefaultPath = getNsimdrvDefaultPath();
        configurationWriter.setNsimDefaultPath(nsimDefaultPath);
        gdbPath = fGDBCommandText.getText();
        if (jtagFrequency != null)
            configurationWriter.setAshlingJtagFrequency(getAttributeValueFromString(jtagFrequency));

        configurationWriter.setFileFormatVersion(
            LaunchConfigurationConstants.CURRENT_FILE_FORMAT_VERSION);
        /* Because there is no setAttribute(String, long) method. */
        configurationWriter.setTimeStamp(String.format("%d", System.currentTimeMillis()));
        configurationWriter.setFtdiDevice(getAttributeValueFromString(pageGui.ftdiDevice.name()));
        configurationWriter.setFtdiCore(getAttributeValueFromString(pageGui.ftdiCore.name()));
        configurationWriter.setGdbPath(gdbPath);
        configurationWriter.setGdbServer(getAttributeValueFromString(pageGui.gdbServer.toString()));
        configurationWriter.setOpenOcdConfig(openOcdConfigurationPath);
        configurationWriter.setOpenOcdPath(openOcdBinaryPath);
        configurationWriter.setAshlingPath(externalToolsAshlingPath);
        configurationWriter.setAshlingXmlPath(ashlingXmlPath);
        configurationWriter.setAshlingTDescPath(ashlingTdescPath);
        configurationWriter.setNsimPath(externalToolsNsimPath);
        configurationWriter.setCustomGdbServerPath(customGdbPath);
        if (customGdbCommandLineArguments != null) {
            configurationWriter.setCustomGdbServerArgs(customGdbCommandLineArguments);
        }

        configurationWriter.setNsimUseTcf(externalNsimTcfToolsEnabled);
        configurationWriter.setNsimUseJit(externalNsimJitEnabled);
        configurationWriter.setNsimUseNsimHostLink(externalNsimHostLinkToolsEnabled);
        configurationWriter.setNsimSimulateMemoryExceptions(
            externalNsimMemoryExceptionToolsEnabled);
        configurationWriter.setNsimSimulateExceptions(externalNsimEnableExceptionToolsEnabled);
        configurationWriter.setNsimSimulateInvalidInstructionExceptions(
            launchExternalNsimInvalidInstructionException);
        configurationWriter.setNsimUseProps(externalNsimPropertiesEnabled);
        configurationWriter.setNsimJitThreads(jitThread);
        configurationWriter.setNsimPropsPath(nsimPropertiesFilesLast);
        configurationWriter.setNsimTcfPath(nsimTcfFilesLast);
        if (groupGenericGdbServerContainer != null && !groupGenericGdbServerContainer.getGroup().isDisposed()) {
            hostName = pageGui.gdbServerIpAddressText.getText();
            configurationWriter.setHostAddress(getAttributeValueFromString(hostName));
        }
    }

    private static boolean isWindowsOs() {
        boolean isWindowsOs = false;
        String osName = System.getProperty("os.name");
        /* This code may have local issues without "Locale.ENGLISH" specified, e.g. in Turkey,
         * "I" becomes lower case undotted "i" ("ı"), and "i" becomes upper case dotted "i" ("İ").
         * So "WINDOWS".toLowerCase().indexOf("win") will return -1 in Turkey.
         */
        if (osName != null && osName.toLowerCase(Locale.ENGLISH).indexOf("windows") > -1) {
            isWindowsOs = true;
        }
        return isWindowsOs;
    }

    static ComGroupContainer groupComContainer = new ComGroupContainer(pageGui);
    static ComAshlingGroupContainer groupComAshlingContainer = new ComAshlingGroupContainer(pageGui);
    static NsimGroupContainer groupNsimContainer  = new NsimGroupContainer(pageGui);
    static GenericGdbServerGroupContainer groupGenericGdbServerContainer =
        new GenericGdbServerGroupContainer(pageGui);
    static ComCustomGdbGroupContainer groupComCustomGdbContainer = new ComCustomGdbGroupContainer(pageGui);
    
    private static DebuggerGroupManager debuggerGroupManager = new DebuggerGroupManager(
        Arrays.asList(groupComAshlingContainer, groupComContainer, groupComCustomGdbContainer,
            groupGenericGdbServerContainer,groupNsimContainer));
    
    protected void createGdbServerSettingsTab(TabFolder tabFolder) {
        // Lets set minimal width of text field to 2 inches. If more required text fields will
        // stretch.
        final int screenPpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
        final int minTextWidth = 2 * screenPpi;

        TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText(Messages.Gdbserver_Settings_Tab_Name);

        Composite comp = new Composite(tabFolder, SWT.NULL);
        comp.setLayout(new GridLayout(1, true));
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridLayout) comp.getLayout()).makeColumnsEqualWidth = false;
        comp.setFont(tabFolder.getFont());
        tabItem.setControl(comp);

        final Composite subComp = new Composite(comp, SWT.NULL);
        subComp.setLayout(new GridLayout(5, true));
        subComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridLayout) subComp.getLayout()).makeColumnsEqualWidth = false;
        subComp.setFont(tabFolder.getFont());

        Label label = new Label(subComp, SWT.LEFT);
        label.setText("ARC GDB Server:");
        GridData gd = new GridData();
        label.setLayoutData(gd);

        GridData serverTypeComboGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
        serverTypeComboGridData.horizontalSpan = 4;
        serverTypeComboGridData.minimumWidth = minTextWidth;
        pageGui.externalToolsCombo = new Combo(subComp, SWT.None | SWT.READ_ONLY);// 1-2 and 1-3
        pageGui.externalToolsCombo.setLayoutData(serverTypeComboGridData);
        for (ArcGdbServer server: ArcGdbServer.values()) {
            pageGui.externalToolsCombo.add(server.toString());
        }



        pageGui.externalToolsCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                Combo combo = (Combo) event.widget;
                pageGui.gdbServerPortNumberText.getText();
                try {
                    pageGui.gdbServer = ArcGdbServer.fromString(combo.getText());
                } catch (IllegalArgumentException e) {
                    pageGui.gdbServer = ArcGdbServer.DEFAULT_GDB_SERVER;
                }
                if (pageGui.gdbServer == ArcGdbServer.JTAG_OPENOCD) {
                    groupComContainer.chosenInGui(subComp, RemoteGdbDebuggerPage.this);
                } else if (pageGui.gdbServer == ArcGdbServer.JTAG_ASHLING) {
                    if (!portNumber.isEmpty())
                        pageGui.gdbServerPortNumberText.setText(portNumber);
                    else
                        pageGui.gdbServerPortNumberText
                                .setText(LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

                    groupNsimContainer.getGroup().dispose();
                    if (groupGenericGdbServerContainer != null) {
                        groupGenericGdbServerContainer.getGroup().dispose();
                    }
                    groupComContainer.guiGroup.dispose();
                    groupComCustomGdbContainer.getGroup().dispose();
                    createTabItemNsim = false;
                    createTabItemGenericGdbServer = false;
                    createTabItemCustomGdb = false;

                    if (createTabItemComAshling == false) {
                        if (!groupComAshlingContainer.getGroup().isDisposed())
                            groupComAshlingContainer.getGroup().dispose();

                        createTabItemComAshling(subComp);
                    }

                    groupComAshlingContainer.getGroup().setText(pageGui.gdbServer.toString());
                    groupComAshlingContainer.getGroup().setVisible(true);
                } else if (pageGui.gdbServer == ArcGdbServer.NSIM) {
                    if (!portNumber.isEmpty())
                        pageGui.gdbServerPortNumberText.setText(portNumber);
                    else
                        pageGui.gdbServerPortNumberText
                                .setText(LaunchConfigurationConstants.DEFAULT_NSIM_PORT);

                    if (!CommandTab.initcom.isEmpty())
                        CommandTab.initcom = "";

                    IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow()
                            .getActivePage();

                    String viewId = "org.eclipse.tm.terminal.view.ui.TerminalsView";

                    if (page != null) {
                        IViewReference[] viewReferences = page.getViewReferences();
                        for (IViewReference ivr : viewReferences) {
                            if (ivr.getId().equalsIgnoreCase(viewId)
                                    || ivr.getId()
                                            .equalsIgnoreCase(
                                                    "more view id if you want to close more than one at a time")) {
                                page.hideView(ivr);
                            }
                        }
                    }

                    groupComContainer.guiGroup.dispose();
                    groupComAshlingContainer.getGroup().dispose();
                    groupComCustomGdbContainer.getGroup().dispose();
                    if (groupGenericGdbServerContainer != null) {
                        groupGenericGdbServerContainer.getGroup().dispose();
                    }
                    if (createTabItemNsim == false) {
                        if (!groupNsimContainer.getGroup().isDisposed())
                            groupNsimContainer.getGroup().dispose();
                        createTabItemNsim(subComp);

                        launchTcf.setSelection(externalNsimPropertiesEnabled);
                        pageGui.launchTcfPropertiesButton.setSelection(externalNsimTcfToolsEnabled);
                        pageGui.launchNsimJitProperties.setSelection(externalNsimJitEnabled);
                        pageGui.launchHostLinkProperties.setSelection(externalNsimHostLinkToolsEnabled);
                        pageGui.launchMemoryExceptionProperties.setSelection(externalNsimMemoryExceptionToolsEnabled);
                        pageGui.launchEnableExceptionProperties.setSelection(externalNsimEnableExceptionToolsEnabled);

                        pageGui.launchInvalidInstructionExceptionProperties.setSelection(launchExternalNsimInvalidInstructionException);
                    }
                    groupNsimContainer.getGroup().setText(pageGui.gdbServer.toString());
                    createTabItemComAshling = false;
                    groupNsimContainer.getGroup().setVisible(true);
                    createTabItemGenericGdbServer = false;
                    createTabItemCustomGdb = false;

                } else if (pageGui.gdbServer == ArcGdbServer.GENERIC_GDBSERVER) {
                    groupComContainer.guiGroup.dispose();
                    groupComAshlingContainer.getGroup().dispose();
                    groupNsimContainer.getGroup().dispose();
                    groupComCustomGdbContainer.getGroup().dispose();
                    if (createTabItemGenericGdbServer == false) {
                        if (groupGenericGdbServerContainer != null && !groupGenericGdbServerContainer.getGroup().isDisposed())
                            groupGenericGdbServerContainer.getGroup().dispose();

                        createTabItemHostAddress(subComp);
                    }
                    createTabItemComAshling = false;
                    createTabItemNsim = false;
                    createTabItemCustomGdb = false;
                    groupGenericGdbServerContainer.getGroup().setText(pageGui.gdbServer.toString());
                    groupGenericGdbServerContainer.getGroup().setVisible(true);

                    IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow()
                            .getActivePage();

                    String viewId = "org.eclipse.tm.terminal.view.ui.TerminalsView";

                    if (page != null) {
                        IViewReference[] viewReferences = page.getViewReferences();
                        for (IViewReference ivr : viewReferences) {
                            if (ivr.getId().equalsIgnoreCase(viewId)
                                    || ivr.getId()
                                            .equalsIgnoreCase(
                                                    "more view id if you want to close more than one at a time")) {
                                page.hideView(ivr);
                            }
                        }
                    }

                    if (!groupComContainer.guiGroup.isDisposed())
                        groupComContainer.guiGroup.setVisible(false);
                    if (!groupNsimContainer.getGroup().isDisposed())
                        groupNsimContainer.getGroup().setVisible(false);
                    if (!groupComAshlingContainer.getGroup().isDisposed())
                        groupComAshlingContainer.getGroup().setVisible(false);
                    if (!groupComCustomGdbContainer.getGroup().isDisposed()) {
                        groupComCustomGdbContainer.getGroup().setVisible(false);
                    }

                } else if (pageGui.gdbServer == ArcGdbServer.CUSTOM_GDBSERVER) {
                    if (!portNumber.equals(""))
                        pageGui.gdbServerPortNumberText.setText(portNumber);
                    else
                        pageGui.gdbServerPortNumberText
                                .setText(LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

                    groupNsimContainer.getGroup().dispose();
                    groupComContainer.guiGroup.dispose();
                    groupComAshlingContainer.getGroup().dispose();
                    groupGenericGdbServerContainer.getGroup().dispose();
                    createTabItemNsim = false;
                    createTabItemComAshling = false;
                    createTabItemGenericGdbServer = false;
                    if (createTabItemCustomGdb == false) {
                        if (!groupComCustomGdbContainer.getGroup().isDisposed())
                            groupComCustomGdbContainer.getGroup().dispose();

                        createTabCustomGdb(subComp);
                    }

                    groupComCustomGdbContainer.getGroup().setText(pageGui.gdbServer.toString());
                    if (!groupComCustomGdbContainer.getGroup().isVisible())
                        groupComCustomGdbContainer.getGroup().setVisible(true);
                }

                updateLaunchConfigurationDialog();

            }
        });

        // GDB port label
        label = new Label(subComp, SWT.LEFT);
        label.setText(Messages.Port_number_textfield_label);
        GridData gdbPortLabelGridData = new GridData();
        gdbPortLabelGridData.horizontalSpan = 1;
        label.setLayoutData(gdbPortLabelGridData);

        // GDB port text field
        pageGui.gdbServerPortNumberText = new Text(subComp, SWT.SINGLE | SWT.BORDER | SWT.BEGINNING);
        GridData gdbPortTextGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
        gdbPortTextGridData.horizontalSpan = 4;
        gdbPortTextGridData.minimumWidth = minTextWidth;
        pageGui.gdbServerPortNumberText.setLayoutData(gdbPortTextGridData);
        pageGui.gdbServerPortNumberText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                updateLaunchConfigurationDialog();
            }
        });

        if (createTabItemNsim == false)
          createTabItemNsim(subComp);
        if (createTabItemComAshling == false)
          createTabItemComAshling(subComp);
        if (createTabItemGenericGdbServer == false)
          createTabItemHostAddress(subComp);
        if (createTabItemCustomGdb == false)
          createTabCustomGdb(subComp);
        debuggerGroupManager.createTabItemsIfNotCreated(subComp, RemoteGdbDebuggerPage.this);
    }


    private void createTabCustomGdb(Composite subComp) {
        createTabItemCustomGdb = true;

        groupComCustomGdbContainer.guiGroup = SWTFactory.createGroup(subComp, pageGui.externalToolsCombo.getItem(0), 3,
                5, GridData.FILL_HORIZONTAL);
        DebuggerGroupManager.guiGroupByGdbServer.put(ArcGdbServer.CUSTOM_GDBSERVER, groupComCustomGdbContainer.guiGroup);
        final Composite compositeCustomGdb = SWTFactory.createComposite(groupComCustomGdbContainer.guiGroup, 3, 5,
                GridData.FILL_BOTH);
        
        // GDB server executable path
        customGdbBinaryPathEditor = new FileFieldEditor("GDB server executable path", "GDB server executable path",
                compositeCustomGdb);
        customGdbBinaryPathEditor.setStringValue(customGdbPath);
        customGdbBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    customGdbPath = (String) event.getNewValue();
                    updateLaunchConfigurationDialog();
                }
            }
        });

        // GDB server command line arguments
        Label label = new Label(compositeCustomGdb, SWT.LEFT);
        label.setText("GDB server command line arguments:");
        customGdbCommandLineArgumentsText = new Text(compositeCustomGdb, SWT.SINGLE | SWT.BORDER | SWT.BEGINNING);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.widthHint = 220;
        layoutData.horizontalSpan = 2;
        customGdbCommandLineArgumentsText.setLayoutData(layoutData);
        if (customGdbCommandLineArguments != null)
            customGdbCommandLineArgumentsText.setText(customGdbCommandLineArguments);

        customGdbCommandLineArgumentsText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                customGdbCommandLineArguments = customGdbCommandLineArgumentsText.getText();
                updateLaunchConfigurationDialog();
            }
        });

    }


    private void createTabItemHostAddress(Composite subComp) {
        final int screenPpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
        final int minTextWidth = 2 * screenPpi;
        createTabItemGenericGdbServer = true;
        groupGenericGdbServerContainer.guiGroup = SWTFactory.createGroup(subComp,
                ArcGdbServer.GENERIC_GDBSERVER.toString(), 3, 5, GridData.FILL_HORIZONTAL);
        DebuggerGroupManager.guiGroupByGdbServer.put(ArcGdbServer.GENERIC_GDBSERVER, groupGenericGdbServerContainer.guiGroup);
        final Composite compCOM = SWTFactory.createComposite(groupGenericGdbServerContainer.guiGroup, 3, 5,
                GridData.FILL_BOTH);
        Label label = new Label(compCOM, SWT.LEFT);
        label.setText("Host Address:");

        // GDB host text field
        pageGui.gdbServerIpAddressText = new Text(compCOM, SWT.SINGLE | SWT.BORDER | SWT.BEGINNING);
        GridData gdbHostFieldGridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
        gdbHostFieldGridData.minimumWidth = minTextWidth;
        pageGui.gdbServerIpAddressText.setLayoutData(gdbHostFieldGridData);
        if (hostName.isEmpty())
            pageGui.gdbServerIpAddressText.setText(LaunchConfigurationConstants.DEFAULT_GDB_HOST);
        else
            pageGui.gdbServerIpAddressText.setText(hostName);
        pageGui.gdbServerIpAddressText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                updateLaunchConfigurationDialog();
            }
        });
    }

    private void createTabItemComAshling(Composite subComp) {
        createTabItemComAshling = true;

        groupComAshlingContainer.guiGroup = SWTFactory.createGroup(subComp, pageGui.externalToolsCombo.getItem(0), 3, 5,
                GridData.FILL_HORIZONTAL);
        debuggerGroupManager.guiGroupByGdbServer.put(ArcGdbServer.JTAG_ASHLING, groupComAshlingContainer.guiGroup);
        final Composite compositeCom = SWTFactory.createComposite(groupComAshlingContainer.guiGroup, 3, 5,
                GridData.FILL_BOTH);
        // Path to Ashling binary
        ashlingBinaryPathEditor = new FileFieldEditor("ashlingBinaryPath", "Ashling binary path", false,
                StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
        ashlingBinaryPathEditor.setStringValue(externalToolsAshlingPath);

        ashlingBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    externalToolsAshlingPath = (String) event.getNewValue();
                    updateLaunchConfigurationDialog();
                }
            }
        });

        // Path to Ashling XMl file
        ashlingXmlPathEditor = new FileFieldEditor("ashlingXmlPathEditor", "Ashling XML File", false,
                StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
        ashlingXmlPathEditor.setStringValue(ashlingXmlPath);

        ashlingXmlPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    ashlingXmlPath = (String) event.getNewValue();
                    updateLaunchConfigurationDialog();
                }
            }
        });


        // Path to ashling target description file
        ashlingTdescXmlPathEditor = new FileFieldEditor("ashlingTdescXmlPath",
                "Target description XML file", false,
                StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
        ashlingTdescXmlPathEditor.setStringValue(ashlingTdescPath);

        ashlingTdescXmlPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    ashlingTdescPath = (String) event.getNewValue();
                    updateLaunchConfigurationDialog();
                }
            }
        });


        createJtagFrequencyCombo(compositeCom);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public boolean isValid(ILaunchConfiguration configuration) {
        setErrorMessage(null);
        setMessage(null);

        if (pageGui.gdbServer != null) {

            switch (pageGui.gdbServer) {
            case JTAG_OPENOCD:
                if (groupComContainer.guiGroup.isDisposed()) {
                    return true;
                }
                if (!isValidFileFieldEditor(pageGui.openOcdBinPathEditor)) {
                    return false;
                }
                if (pageGui.ftdiDevice == pageGui.ftdiDevice.CUSTOM) {
                    if (!isValidFileFieldEditor(pageGui.openOcdConfigurationPathEditor)) {
                        return false;
                    }
                } else {
                    File configurationFile = new File(openOcdConfigurationPath);
                    if (!configurationFile.exists()) {
                        setErrorMessage(
                                "Default OpenOCD configuration file for this development system \'"
                                        + pageGui.openOcdConfigurationPathEditor + "\' must exist");
                        return false;
                    }
                }
                break;
            case JTAG_ASHLING:
                if (groupComAshlingContainer.getGroup().isDisposed()){
                    return true;
                }
                if (!isValidFileFieldEditor(ashlingBinaryPathEditor)
                        || !isValidFileFieldEditor(ashlingXmlPathEditor)
                        || !isValidFileFieldEditor(ashlingTdescXmlPathEditor)) {
                     return false;
                }
                break;
            case NSIM:
                if (groupNsimContainer.getGroup().isDisposed()) {
                    return true;
                }
                if (!isValidFileFieldEditor(nsimBinaryPathEditor)
                        || (launchTcf.getSelection()
                                && !isValidFileFieldEditor(nsimTcfPathEditor))
                        || (pageGui.launchTcfPropertiesButton.getSelection()
                                && !isValidFileFieldEditor(nsimPropertiesPathEditor))
                        || !workingDirectoryBlockNsim.isValid(configuration)) {
                     return false;
                }
                break;
            case CUSTOM_GDBSERVER:
                if (groupComCustomGdbContainer.getGroup().isDisposed()) {
                    return true;
                }
                if (!isValidFileFieldEditor(customGdbBinaryPathEditor)) {
                    return false;
                }
                break;
            case GENERIC_GDBSERVER:
                break;
            default:
                throw new IllegalArgumentException("Unknown enum value has been used");
            }
        }

        return true;
    }

    private boolean isValidFileFieldEditor(FileFieldEditor editor) {
        if (editor != null) {
            String label = editor.getLabelText();
            if (editor.getStringValue().isEmpty()) {
                setErrorMessage(label + "'s value cannot be empty");
                return false;
            }
            if (!editor.isValid()) {
                setErrorMessage(label + "'s value must be an existing file");
                return false;
            }
        }
        return true;
    }

    private void createJtagFrequencyCombo(Composite composite) {
        Label label = new Label(composite, SWT.LEFT);
        label.setText("JTAG frequency:");
        jtagFrequencyCombo = new Combo(composite, SWT.None);// 1-2 and 1-3

        GridData gridDataJtag = new GridData(GridData.BEGINNING);
        gridDataJtag.widthHint = 100;
        jtagFrequencyCombo.setLayoutData(gridDataJtag);

        jtagFrequencyCombo.add("100MHz");
        jtagFrequencyCombo.add("90MHz");
        jtagFrequencyCombo.add("80MHz");
        jtagFrequencyCombo.add("70MHz");
        jtagFrequencyCombo.add("60MHz");
        jtagFrequencyCombo.add("50MHz");
        jtagFrequencyCombo.add("40MHz");
        jtagFrequencyCombo.add("30MHz");
        jtagFrequencyCombo.add("25MHz");
        jtagFrequencyCombo.add("20MHz");
        jtagFrequencyCombo.add("18MHz");
        jtagFrequencyCombo.add("15MHz");
        jtagFrequencyCombo.add("12MHz");
        jtagFrequencyCombo.add("10MHz");
        jtagFrequencyCombo.add("9MHz");
        jtagFrequencyCombo.add("8MHz");
        jtagFrequencyCombo.add("7MHz");
        jtagFrequencyCombo.add("6MHz");
        jtagFrequencyCombo.add("5MHz");
        jtagFrequencyCombo.add("4MHz");
        jtagFrequencyCombo.add("3MHz");
        jtagFrequencyCombo.add("2500KHz");
        jtagFrequencyCombo.add("2000KHz");
        jtagFrequencyCombo.add("1800KHz");
        jtagFrequencyCombo.add("1500KHz");
        jtagFrequencyCombo.add("1200KHz");
        jtagFrequencyCombo.add("1000KHz");

        jtagFrequencyCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                Combo combo = (Combo) event.widget;
                jtagFrequency = combo.getText();
                updateLaunchConfigurationDialog();

            }
        });
        //Setting text after adding listener to make sure jtagFreq field value is updated
        if (jtagFrequency != null) {
            if (jtagFrequencyCombo.getText().isEmpty() && jtagFrequency.isEmpty())
                jtagFrequencyCombo.setText("10MHz");
            else if (jtagFrequencyCombo.getText().isEmpty() && !jtagFrequency.isEmpty())
                jtagFrequencyCombo.setText(jtagFrequency);
        } else {
            jtagFrequencyCombo.setText("10MHz");
        }
    }

    private void createTabItemNsim(Composite subComp) {
        createTabItemNsim = true;

        groupNsimContainer.guiGroup = SWTFactory.createGroup(subComp, pageGui.externalToolsCombo.getItem(0), 3, 5,
                GridData.FILL_HORIZONTAL);
        DebuggerGroupManager.guiGroupByGdbServer.put(ArcGdbServer.NSIM, groupNsimContainer.guiGroup);
        final Composite compositeNsim = SWTFactory.createComposite(groupNsimContainer.getGroup(), 3, 5, GridData.FILL_BOTH);
        
        GridData gridData = new GridData();

        nsimBinaryPathEditor = new FileFieldEditor("nsimBinPath", "nSIM executable", false,
                StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeNsim);

        nsimBinaryPathEditor.setStringValue(externalToolsNsimPath);
        nsimBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    externalToolsNsimPath = (String) event.getNewValue();
                    updateLaunchConfigurationDialog();
                }
            }
        });

        pageGui.launchTcfPropertiesButton = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        pageGui.launchTcfPropertiesButton.setToolTipText("Pass specified TCF file to nSIM for parsing of nSIM properties (-tcf=path)" );
        pageGui.launchTcfPropertiesButton.setSelection(externalNsimTcfToolsEnabled);
        gridData = new GridData(SWT.BEGINNING);
        gridData.horizontalSpan = 3;
        pageGui.launchTcfPropertiesButton.setLayoutData(gridData);
        pageGui.launchTcfPropertiesButton.setText("Use TCF?");

        nsimTcfPathEditor = new FileFieldEditor("nsimTcfPath", "nSIM TCF path", false,
                StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeNsim);
        nsimTcfPathEditor.setStringValue(nsimPropertiesFilesLast);
        nsimTcfPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    nsimPropertiesFilesLast = (String) event.getNewValue();
                    updateLaunchConfigurationDialog();
                }
            }
        });
        nsimTcfPathEditor.setEnabled((externalNsimTcfToolsEnabled), compositeNsim);

        launchTcf = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        launchTcf.setToolTipText("-propsfile=path");
        launchTcf.setSelection(externalNsimPropertiesEnabled);
        gridData = new GridData(SWT.BEGINNING);
        gridData.horizontalSpan = 3;
        launchTcf.setLayoutData(gridData);
        launchTcf.setText("Use nSIM properties file?");
        nsimPropertiesPathEditor = new FileFieldEditor("nsimPropertiesPath", "nSIM properties file", false,
                StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeNsim);
        nsimPropertiesPathEditor.setStringValue(nsimPropertiesFilesLast);
        nsimPropertiesPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    nsimPropertiesFilesLast = (String) event.getNewValue();
                    updateLaunchConfigurationDialog();
                }
            }
        });

        nsimPropertiesPathEditor.setEnabled((externalNsimPropertiesEnabled), compositeNsim);

        launchTcf.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (launchTcf.getSelection() == true) {
                    externalNsimTcfToolsEnabled = true;
                    nsimTcfPathEditor.setEnabled(true, compositeNsim);

                } else {
                    externalNsimTcfToolsEnabled = false;
                    pageGui.launchTcfPropertiesButton.setSelection(false);
                    nsimTcfPathEditor.setEnabled(false, compositeNsim);
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });
        pageGui.launchTcfPropertiesButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (pageGui.launchTcfPropertiesButton.getSelection() == true) {
                    externalNsimPropertiesEnabled = true;
                    nsimPropertiesPathEditor.setEnabled(true, compositeNsim);
                } else {
                    externalNsimPropertiesEnabled = false;
                    nsimPropertiesPathEditor.setEnabled(false, compositeNsim);
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });

        // JIT

        gridData = new GridData(SWT.BEGINNING);
        gridData.horizontalSpan = 3;

        pageGui.launchNsimJitProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        pageGui.launchNsimJitProperties.setSelection(externalNsimJitEnabled);
        pageGui.launchNsimJitProperties.setText("JIT");
        pageGui.launchNsimJitProperties.setToolTipText("Enable (1) or disable (0) JIT simulation mode (-p nsim_fast={0,1})");
        pageGui.jitThreadSpinner = new Spinner(compositeNsim, SWT.NONE | SWT.BORDER);
        pageGui.jitThreadSpinner.setToolTipText("Specify number of threads to use in JIT simulation mode (-p nsim_fast-num-threads=N)");
        final Label jitLabel = new Label(compositeNsim, SWT.BEGINNING);
        jitLabel.setText("JIT threads");
        pageGui.jitThreadSpinner.setValues(1, 1, 100, 10, 1, 0);

        pageGui.launchNsimJitProperties.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (pageGui.launchNsimJitProperties.getSelection() == true) {
                    externalNsimJitEnabled = true;
                    jitLabel.setEnabled(true);
                    pageGui.jitThreadSpinner.setEnabled(true);

                } else {
                    externalNsimJitEnabled = false;
                    jitLabel.setEnabled(false);
                    pageGui.jitThreadSpinner.setEnabled(false);
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });

        pageGui.launchNsimJitProperties.setLayoutData(gridData);

        if (externalNsimJitEnabled == true) {
            jitLabel.setEnabled(true);
            pageGui.jitThreadSpinner.setEnabled(true);
        } else if (externalNsimJitEnabled == false) {
            jitLabel.setEnabled(false);
            pageGui.jitThreadSpinner.setEnabled(false);
        }

        if (!jitThread.equals("1"))
            pageGui.jitThreadSpinner.setSelection(Integer.parseInt(jitThread));
        else
            pageGui.jitThreadSpinner.setSelection(1);

        pageGui.jitThreadSpinner.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                jitThread = pageGui.jitThreadSpinner.getText();
                updateLaunchConfigurationDialog();
            }
        });
        gridData = new GridData(SWT.BEGINNING);
        gridData.horizontalSpan = 2;
        jitLabel.setLayoutData(gridData);

        GridData gridDataNsim = new GridData(SWT.BEGINNING);
        gridDataNsim.horizontalSpan = 2;

        pageGui.launchHostLinkProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        pageGui.launchHostLinkProperties.setToolTipText("Enable or disable nSIM GNU host I/O support (-p nsim_emt={0,1}). The nsim_emt property works only if the application that is being simulated is compiled with the ARC GCC compiler.");
        pageGui.launchHostLinkProperties.setSelection(externalNsimHostLinkToolsEnabled);
        pageGui.launchHostLinkProperties.setText("GNU host I/O support");
        pageGui.launchHostLinkProperties.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (pageGui.launchHostLinkProperties.getSelection() == true) {
                    externalNsimHostLinkToolsEnabled = true;

                } else {
                    externalNsimHostLinkToolsEnabled = false;
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });

        pageGui.launchHostLinkProperties.setLayoutData(gridDataNsim);

        pageGui.launchMemoryExceptionProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        pageGui.launchMemoryExceptionProperties.setToolTipText("Simulate (1) or break (0) on memory exception (-p memory_exception_interrupt={0,1})");
        pageGui.launchMemoryExceptionProperties.setSelection(externalNsimMemoryExceptionToolsEnabled);
        pageGui.launchMemoryExceptionProperties.setText("Memory Exception");
        pageGui.launchMemoryExceptionProperties.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (pageGui.launchMemoryExceptionProperties.getSelection() == true) {
                    externalNsimMemoryExceptionToolsEnabled = true;

                } else {
                    externalNsimMemoryExceptionToolsEnabled = false;
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });

        pageGui.launchMemoryExceptionProperties.setLayoutData(gridDataNsim);

        pageGui.launchEnableExceptionProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        pageGui.launchEnableExceptionProperties.setSelection(externalNsimEnableExceptionToolsEnabled);
        pageGui.launchEnableExceptionProperties.setText("Enable Exception");
        pageGui.launchEnableExceptionProperties.setToolTipText("Simulate (1) or break (0) on any exception (-p enable_exceptions={0,1})");
        pageGui.launchEnableExceptionProperties.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (pageGui.launchEnableExceptionProperties.getSelection() == true) {
                    externalNsimEnableExceptionToolsEnabled = true;

                } else {
                    externalNsimEnableExceptionToolsEnabled = false;
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });

        pageGui.launchEnableExceptionProperties.setLayoutData(gridDataNsim);

        pageGui.launchInvalidInstructionExceptionProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        pageGui.launchInvalidInstructionExceptionProperties.setToolTipText("Simulate (1) or break (0) on invalid instruction exception (-p invalid_instruction_interrupt={0,1})");
        pageGui.launchInvalidInstructionExceptionProperties.setSelection(launchExternalNsimInvalidInstructionException);
        pageGui.launchInvalidInstructionExceptionProperties.setText("Invalid Instruction  Exception");
        pageGui.launchInvalidInstructionExceptionProperties.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (pageGui.launchInvalidInstructionExceptionProperties.getSelection() == true) {
                    launchExternalNsimInvalidInstructionException = true;

                } else {
                    launchExternalNsimInvalidInstructionException = false;
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });
        pageGui.launchInvalidInstructionExceptionProperties.setLayoutData(gridDataNsim);

        workingDirectoryBlockNsim.createControl(compositeNsim);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.debug.mi.internal.ui.GDBDebuggerPage#createTabs(org.eclipse.swt.widgets.TabFolder
     * )
     */
    @Override
    public void createTabs(TabFolder tabFolder) {
        super.createTabs(tabFolder);
        createGdbServerSettingsTab(tabFolder);
    }

    public static String getAttributeValueFromString(String string) {
        String content = string;
        if (content.length() > 0) {
          return content;
        }
        return null;
    }
    
    @Override
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        super.setLaunchConfigurationDialog(dialog);
        workingDirectoryBlockNsim.setLaunchConfigurationDialog(dialog);
    }

    @Override
    public String getErrorMessage() {
        String errorMessage = super.getErrorMessage();
        if (errorMessage == null && !groupNsimContainer.getGroup().isDisposed()) {
            return workingDirectoryBlockNsim.getErrorMessage();
        }
        return errorMessage;
    }
}
