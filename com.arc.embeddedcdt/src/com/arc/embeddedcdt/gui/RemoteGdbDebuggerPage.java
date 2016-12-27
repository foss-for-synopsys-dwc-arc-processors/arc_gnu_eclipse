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
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

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
 * The dynamic debugger tab for remote launches using gdb server. The gdbserver settings are used to
 * start a gdbserver session on the remote and then to connect to it from the host. The DSDP-TM
 * project is used to accomplish this.
 */
@SuppressWarnings("restriction")
public class RemoteGdbDebuggerPage extends GdbDebuggerPage {
    protected Combo externalToolsCombo;
    protected Combo ftdiDeviceCombo;
    protected Combo ftdiCoreCombo;
    private FileFieldEditor openOcdBinaryPathEditor;
    private FileFieldEditor openOcdConfigurationPathEditor;
    private FileFieldEditor nsimBinaryPathEditor;
    private FileFieldEditor nsimTcfPathEditor;
    private FileFieldEditor nsimPropertiesPathEditor;
    private ARCWorkingDirectoryBlock workingDirectoryBlockNsim = new ARCWorkingDirectoryBlock();
    private ArcGdbServer gdbServer = ArcGdbServer.DEFAULT_GDB_SERVER;
    private boolean createTabItemCom = false;
    private boolean createTabItemNsim = false;
    private boolean createTabItemGenericGdbServer = false;
    private String gdbPath = null;
    private boolean createTabItemComAshling = false;
    private boolean createTabItemCustomGdb = false;
    protected Button nsimPropertiesBrowseButton;
    private String nsimPropertiesFilesLast = "";
    protected Button launchTcf;
    private boolean externalNsimPropertiesEnabled = true;
    protected Button launchTcfPropertiesButton;
    protected Button launchNsimJitProperties;
    protected Button launchHostLinkProperties;
    protected Button launchMemoryExceptionProperties;

    protected Spinner jitThreadSpinner;
    private DebuggerGroupContainer debuggerGroupContainer = new DebuggerGroupContainer();

    public RemoteGdbDebuggerPage() {
      debuggerGroupContainer.addObserver(new Observer() {

        @Override
        public void update(Observable o, Object arg) {
          updateLaunchConfigurationDialog();
        }
      });
    }

    @Override
    public String getName() {
        return Messages.Remote_GDB_Debugger_Options;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
      super.setDefaults(configuration);
      debuggerGroupContainer.setDefaults(configuration);
    }

    public static String getDefaultGdbPath() {
        String gdbPath = "arc-elf32-gdb";
        String predefinedPath = DebuggerGroupContainer.getIdeBinDir();
        File predefinedPathFile = new File(predefinedPath);

        if (predefinedPathFile.isDirectory()) {
            File gdbFile = new File(predefinedPath + "arc-elf32-gdb");
            if (gdbFile.canExecute()) {
                gdbPath = gdbFile.getAbsolutePath();
            }
        }
        return gdbPath;
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        LaunchFileFormatVersionChecker.getInstance().check(configuration);
        createTabItemCom = false;
        createTabItemComAshling = false;
        createTabItemNsim = false;
        createTabItemGenericGdbServer = false;
        createTabItemCustomGdb = false;
        super.initializeFrom(configuration);
        ConfigurationReader configurationReader = new ConfigurationReader(configuration);
        debuggerGroupContainer.initializeFrom(configurationReader);
        gdbPath = configurationReader.getOrDefault(getDefaultGdbPath(), "",
            configurationReader.getGdbPath());
        fGDBCommandText.setText(gdbPath);
        gdbServer = configurationReader.getGdbServer();

        workingDirectoryBlockNsim.initializeFrom(configuration);
        externalNsimPropertiesEnabled = configurationReader.getNsimUseProps();
        nsimPropertiesFilesLast = configurationReader.getNsimPropsPath();

        externalToolsCombo.setText(gdbServer.toString());

        if (!ftdiDeviceCombo.isDisposed())
            ftdiDeviceCombo.setText(debuggerGroupContainer.getFtdiDevice().toString());

        if (!ftdiCoreCombo.isDisposed())
            ftdiCoreCombo.setText(debuggerGroupContainer.getFtdiCore().toString());
        if (groupGenericGdbServer != null && !groupGenericGdbServer.isDisposed())
            debuggerGroupContainer.setTextForGdbServerIpAddressText(
                debuggerGroupContainer.getHostName());

        int previous = externalToolsCombo.indexOf(gdbServer.toString());
        if (previous > -1)
            externalToolsCombo.remove(previous);
        /*
         * Reading gdbServer again from configuration because field gdbServer might have been
         * changed by event handler called by extTools.remove(previous)
         */
        gdbServer = configurationReader.getGdbServer();
        externalToolsCombo.add(gdbServer.toString(), 0);
        externalToolsCombo.select(0);

    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);
        ConfigurationReader configurationReader = new ConfigurationReader(configuration);
        final String programName = configurationReader.getProgramName();
        configuration.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME,
            programName.replace('\\', '/'));
        if (!groupNsim.isDisposed()) {
            workingDirectoryBlockNsim.performApply(configuration);
        }

        ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
        debuggerGroupContainer.performApply(configurationWriter);
        String nsimDefaultPath = DebuggerGroupContainer.getNsimdrvDefaultPath();
        configurationWriter.setNsimDefaultPath(nsimDefaultPath);
        gdbPath = fGDBCommandText.getText();

        configurationWriter.setFileFormatVersion(
            LaunchConfigurationConstants.CURRENT_FILE_FORMAT_VERSION);
        /* Because there is no setAttribute(String, long) method. */
        configurationWriter.setTimeStamp(String.format("%d", System.currentTimeMillis()));
        configurationWriter.setFtdiDevice(
            DebuggerGroupContainer.getAttributeValueFromString(
                debuggerGroupContainer.getFtdiDevice().name()));
        configurationWriter.setFtdiCore(
            DebuggerGroupContainer.getAttributeValueFromString(
                debuggerGroupContainer.getFtdiCore().name()));
        configurationWriter.setGdbPath(gdbPath);
        configurationWriter.setGdbServer(
            DebuggerGroupContainer.getAttributeValueFromString(gdbServer.toString()));

        configurationWriter.setNsimUseProps(externalNsimPropertiesEnabled);
        configurationWriter.setNsimPropsPath(nsimPropertiesFilesLast);
        if (groupGenericGdbServer != null && !groupGenericGdbServer.isDisposed()) {
            debuggerGroupContainer.setHostName(
                debuggerGroupContainer.getTextFromGdbServerIpAddressText());
            configurationWriter.setHostAddress(DebuggerGroupContainer.getAttributeValueFromString(
                debuggerGroupContainer.getHostName()));
        }
    }

    static Group groupCom;
    static Group groupComAshling;
    static Group groupNsim;
    static Group groupGenericGdbServer;
    static Group groupComCustomGdb;

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
        externalToolsCombo = new Combo(subComp, SWT.None | SWT.READ_ONLY);// 1-2 and 1-3
        externalToolsCombo.setLayoutData(serverTypeComboGridData);
        for (ArcGdbServer server: ArcGdbServer.values()) {
            externalToolsCombo.add(server.toString());
        }



        externalToolsCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                Combo combo = (Combo) event.widget;
                debuggerGroupContainer.getTextFromGdbServerPortNumberText();
                try {
                    gdbServer = ArcGdbServer.fromString(combo.getText());
                } catch (IllegalArgumentException e) {
                    gdbServer = ArcGdbServer.DEFAULT_GDB_SERVER;
                }

                if (gdbServer == ArcGdbServer.JTAG_OPENOCD) {
                    debuggerGroupContainer.setPortNumberText(
                        LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT);

                    groupNsim.dispose();
                    if (groupGenericGdbServer != null) {
                        groupGenericGdbServer.dispose();
                    }
                    groupComAshling.dispose();
                    groupComCustomGdb.dispose();

                    if (createTabItemCom == false) {
                        if (!groupCom.isDisposed())
                            groupCom.dispose();

                        createTabItemCom(subComp);
                    }
                    groupCom.setText(gdbServer.toString());
                    createTabItemNsim = false;
                    createTabItemComAshling = false;
                    groupCom.setVisible(true);
                    createTabItemGenericGdbServer = false;
                    createTabItemCustomGdb = false;

                } else if (gdbServer == ArcGdbServer.JTAG_ASHLING) {
                    debuggerGroupContainer.setPortNumberText(
                        LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

                    groupNsim.dispose();
                    if (groupGenericGdbServer != null) {
                        groupGenericGdbServer.dispose();
                    }
                    groupCom.dispose();
                    groupComCustomGdb.dispose();
                    createTabItemNsim = false;
                    createTabItemGenericGdbServer = false;
                    createTabItemCom = false;
                    createTabItemCustomGdb = false;

                    if (createTabItemComAshling == false) {
                        if (!groupComAshling.isDisposed())
                            groupComAshling.dispose();

                        createTabItemComAshling(subComp);
                    }

                    groupComAshling.setText(gdbServer.toString());
                    groupComAshling.setVisible(true);
                } else if (gdbServer == ArcGdbServer.NSIM) {
                    debuggerGroupContainer.setPortNumberText(
                        LaunchConfigurationConstants.DEFAULT_NSIM_PORT);

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

                    groupCom.dispose();
                    groupComAshling.dispose();
                    groupComCustomGdb.dispose();
                    if (groupGenericGdbServer != null) {
                        groupGenericGdbServer.dispose();
                    }
                    if (createTabItemNsim == false) {
                        if (!groupNsim.isDisposed())
                            groupNsim.dispose();
                        createTabItemNsim(subComp);

                        launchTcf.setSelection(externalNsimPropertiesEnabled);
                        launchTcfPropertiesButton.setSelection(
                            debuggerGroupContainer.getExternalNsimTcfToolsEnabled());
                        launchNsimJitProperties.setSelection(
                            debuggerGroupContainer.getExternalNsimJitEnabled());
                        launchHostLinkProperties.setSelection(
                            debuggerGroupContainer.getExternalNsimHostLinkToolsEnabled());
                        launchMemoryExceptionProperties.setSelection(
                            debuggerGroupContainer.getExternalNsimMemoryExceptionToolsEnabled());
                        debuggerGroupContainer.setSelectionForLaunchEnableExceptionPropertiesButton();

                    }
                    groupNsim.setText(gdbServer.toString());
                    createTabItemCom = false;
                    createTabItemComAshling = false;
                    groupNsim.setVisible(true);
                    createTabItemGenericGdbServer = false;
                    createTabItemCustomGdb = false;

                } else if (gdbServer == ArcGdbServer.GENERIC_GDBSERVER) {
                    groupCom.dispose();
                    groupComAshling.dispose();
                    groupNsim.dispose();
                    groupComCustomGdb.dispose();
                    if (createTabItemGenericGdbServer == false) {
                        if (groupGenericGdbServer != null && !groupGenericGdbServer.isDisposed())
                            groupGenericGdbServer.dispose();

                        createTabItemHostAddress(subComp);
                    }
                    createTabItemCom = false;
                    createTabItemComAshling = false;
                    createTabItemNsim = false;
                    createTabItemCustomGdb = false;
                    groupGenericGdbServer.setText(gdbServer.toString());
                    groupGenericGdbServer.setVisible(true);

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

                    if (!groupCom.isDisposed())
                        groupCom.setVisible(false);
                    if (!groupNsim.isDisposed())
                        groupNsim.setVisible(false);
                    if (!groupComAshling.isDisposed())
                        groupComAshling.setVisible(false);
                    if (!groupComCustomGdb.isDisposed()) {
                        groupComCustomGdb.setVisible(false);
                    }

                } else if (gdbServer == ArcGdbServer.CUSTOM_GDBSERVER) {
                    debuggerGroupContainer.setPortNumberText(
                        LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

                    groupNsim.dispose();
                    groupCom.dispose();
                    groupComAshling.dispose();
                    groupGenericGdbServer.dispose();
                    createTabItemNsim = false;
                    createTabItemCom = false;
                    createTabItemComAshling = false;
                    createTabItemGenericGdbServer = false;
                    if (createTabItemCustomGdb == false) {
                        if (!groupComCustomGdb.isDisposed())
                            groupComCustomGdb.dispose();

                        createTabCustomGdb(subComp);
                    }

                    groupComCustomGdb.setText(gdbServer.toString());
                    if (!groupComCustomGdb.isVisible())
                        groupComCustomGdb.setVisible(true);
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

        debuggerGroupContainer.createGdbServerPortNumberText(subComp, minTextWidth);

        if (createTabItemNsim == false)
            createTabItemNsim(subComp);
        if (createTabItemCom == false)
            createTabItemCom(subComp);
        if (createTabItemComAshling == false)
            createTabItemComAshling(subComp);
        if (createTabItemGenericGdbServer == false)
            createTabItemHostAddress(subComp);
        if (createTabItemCustomGdb == false)
            createTabCustomGdb(subComp);
    }


    private void createTabCustomGdb(Composite subComp) {
        createTabItemCustomGdb = true;

        groupComCustomGdb = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3,
                5, GridData.FILL_HORIZONTAL);
        final Composite compositeCustomGdb = SWTFactory.createComposite(groupComCustomGdb, 3, 5,
                GridData.FILL_BOTH);

        debuggerGroupContainer.createTabCustomGdb(compositeCustomGdb);

    }


    private void createTabItemHostAddress(Composite subComp) {
        final int screenPpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
        final int minTextWidth = 2 * screenPpi;
        createTabItemGenericGdbServer = true;
        groupGenericGdbServer = SWTFactory.createGroup(subComp,
                ArcGdbServer.GENERIC_GDBSERVER.toString(), 3, 5, GridData.FILL_HORIZONTAL);
        final Composite compCOM = SWTFactory.createComposite(groupGenericGdbServer, 3, 5,
                GridData.FILL_BOTH);

        Label label = new Label(compCOM, SWT.LEFT);
        label.setText("Host Address:");

        debuggerGroupContainer.createGdbServerIpAddressText(compCOM, minTextWidth);
    }

    private void createTabItemComAshling(Composite subComp) {
        createTabItemComAshling = true;

        groupComAshling = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
                GridData.FILL_HORIZONTAL);
        final Composite compositeCom = SWTFactory.createComposite(groupComAshling, 3, 5,
                GridData.FILL_BOTH);

        debuggerGroupContainer.createAshlingBinaryPathEditor(compositeCom);

        debuggerGroupContainer.createTabItemComAshling(compositeCom);

        debuggerGroupContainer.createAshlingTdescXmlPathEditor(compositeCom);

        debuggerGroupContainer.createJtagFrequencyCombo(compositeCom);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
     */
    public boolean isValid(ILaunchConfiguration configuration) {
        setErrorMessage(null);
        setMessage(null);

        if (gdbServer != null) {

            switch (gdbServer) {
            case JTAG_OPENOCD:
                if (groupCom.isDisposed()) {
                    return true;
                }
                if (!isValidFileFieldEditor(openOcdBinaryPathEditor)) {
                    return false;
                }
                if (debuggerGroupContainer.getFtdiDevice() == FtdiDevice.CUSTOM) {
                    if (!isValidFileFieldEditor(openOcdConfigurationPathEditor)) {
                        return false;
                    }
                } else {
                    File configurationFile = new File(
                        debuggerGroupContainer.getOpenOcdConfigurationPath());
                    if (!configurationFile.exists()) {
                        setErrorMessage(
                                "Default OpenOCD configuration file for this development system \'"
                                        + openOcdConfigurationPathEditor + "\' must exist");
                        return false;
                    }
                }
                break;
            case JTAG_ASHLING:
                if (groupComAshling.isDisposed()){
                    return true;
                }
                if (!isValidFileFieldEditor(debuggerGroupContainer.getAshlingBinaryPathEditor())
                        || !isValidFileFieldEditor(debuggerGroupContainer.getAshlingXmlPathEditor())
                        || !isValidFileFieldEditor(
                            debuggerGroupContainer.getAshlingTdescXmlPathEditor())) {
                     return false;
                }
                break;
            case NSIM:
                if (groupNsim.isDisposed()) {
                    return true;
                }
                if (!isValidFileFieldEditor(nsimBinaryPathEditor)
                        || (launchTcf.getSelection()
                                && !isValidFileFieldEditor(nsimTcfPathEditor))
                        || (launchTcfPropertiesButton.getSelection()
                                && !isValidFileFieldEditor(nsimPropertiesPathEditor))
                        || !workingDirectoryBlockNsim.isValid(configuration)) {
                     return false;
                }
                break;
            case CUSTOM_GDBSERVER:
                if (groupComCustomGdb.isDisposed()) {
                    return true;
                }
                if (!isValidFileFieldEditor(debuggerGroupContainer.getCustomGdbBinaryPathEditor())) {
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

    private String getOpenOcdConfigurationPath() {
        final File rootDirectory = new File(debuggerGroupContainer.getOpenOcdBinaryPath())
            .getParentFile().getParentFile();
        final File scriptsDirectory = new File(rootDirectory,
                "share" + File.separator + "openocd" + File.separator + "scripts");
        String openOcdConfiguration = scriptsDirectory + File.separator + "board" + File.separator;

        switch (debuggerGroupContainer.getFtdiDevice()) {
        case EM_SK_v1x:
            openOcdConfiguration += "snps_em_sk_v1.cfg";
            break;
        case EM_SK_v21:
            openOcdConfiguration += "snps_em_sk_v2.1.cfg";
            break;
        case EM_SK_v22:
            openOcdConfiguration += "snps_em_sk_v2.2.cfg";
            break;
        case AXS101:
            openOcdConfiguration += "snps_axs101.cfg";
            break;
        case AXS102:
            openOcdConfiguration += "snps_axs102.cfg";
            break;
        case AXS103:
            if (debuggerGroupContainer.getFtdiCore() == FtdiCore.HS36) {
                openOcdConfiguration += "snps_axs103_hs36.cfg";
            } else {
                openOcdConfiguration += "snps_axs103_hs38.cfg";
            }
            break;
        case CUSTOM:
            break;
        default:
            throw new IllegalArgumentException("Unknown enum value has been used");
        }
        return openOcdConfiguration;
    }

    private void createTabItemCom(Composite subComp) {
        createTabItemCom = true;
        groupCom = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
                GridData.FILL_HORIZONTAL);
        final Composite compositeCom = SWTFactory.createComposite(groupCom, 3, 5, GridData.FILL_BOTH);

        // Path to OpenOCD binary
        openOcdBinaryPathEditor = new FileFieldEditor("openocdBinaryPathEditor", "OpenOCD executable",
            false, StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
        openOcdBinaryPathEditor.setStringValue(debuggerGroupContainer.getOpenOcdBinaryPath());
        openOcdBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    debuggerGroupContainer.setOpenOcdBinaryPath((String) event.getNewValue());
                    if (debuggerGroupContainer.getFtdiDevice() != FtdiDevice.CUSTOM) {
                        debuggerGroupContainer.setOpenOcdConfigurationPath(
                            getOpenOcdConfigurationPath());
                        openOcdConfigurationPathEditor.setStringValue(
                            debuggerGroupContainer.getOpenOcdConfigurationPath());
                    }
                    updateLaunchConfigurationDialog();
                }
            }
        });
        Label label = new Label(compositeCom, SWT.LEFT);
        label.setText("Development system:");
        ftdiDeviceCombo = new Combo(compositeCom, SWT.None | SWT.READ_ONLY);// 1-2 and 1-3

        GridData gridDataJtag = new GridData(GridData.BEGINNING);
        gridDataJtag.widthHint = 220;
        gridDataJtag.horizontalSpan = 2;
        ftdiDeviceCombo.setLayoutData(gridDataJtag);

        for (FtdiDevice i : FtdiDevice.values())
            ftdiDeviceCombo.add(i.toString());
        ftdiDeviceCombo.setText(debuggerGroupContainer.getFtdiDevice().toString());

        ftdiDeviceCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                Combo combo = (Combo) event.widget;
                debuggerGroupContainer.setFtdiDevice(FtdiDevice.fromString(combo.getText()));

                if (debuggerGroupContainer.getFtdiDevice() == FtdiDevice.CUSTOM)
                    openOcdConfigurationPathEditor.setEnabled(true, compositeCom);
                else
                    openOcdConfigurationPathEditor.setEnabled(false, compositeCom);

                if (debuggerGroupContainer.getFtdiDevice().getCores().size() <= 1)
                    ftdiCoreCombo.setEnabled(false);
                else
                    ftdiCoreCombo.setEnabled(true);

                updateFtdiCoreCombo();
                updateLaunchConfigurationDialog();
            }
        });

        Label coreLabel = new Label(compositeCom, SWT.LEFT);
        coreLabel.setText("Target Core");
        ftdiCoreCombo = new Combo(compositeCom, SWT.None | SWT.READ_ONLY);// 1-2 and 1-3
        ftdiCoreCombo.setLayoutData(gridDataJtag);

        if (debuggerGroupContainer.getFtdiDevice().getCores().size() <= 1)
            ftdiCoreCombo.setEnabled(false);
        else
            ftdiCoreCombo.setEnabled(true);

        updateFtdiCoreCombo();

        ftdiCoreCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                Combo combo = (Combo) event.widget;
                if (!combo.getText().isEmpty()) {
                    debuggerGroupContainer.setFtdiCore(FtdiCore.fromString(combo.getText()));
                    if (debuggerGroupContainer.getFtdiDevice() != FtdiDevice.CUSTOM) {
                        debuggerGroupContainer.setOpenOcdConfigurationPath(
                            getOpenOcdConfigurationPath());
                        openOcdConfigurationPathEditor.setStringValue(
                            debuggerGroupContainer.getOpenOcdConfigurationPath());
                    }
                }
                updateLaunchConfigurationDialog();
            }
        });

        openOcdConfigurationPathEditor = new FileFieldEditor("openocdConfigurationPathEditor",
            "OpenOCD configuration file",
                false, StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeCom);
        openOcdConfigurationPathEditor.setEnabled(false, compositeCom);
        openOcdConfigurationPathEditor.setStringValue(
            debuggerGroupContainer.getOpenOcdConfigurationPath());
        openOcdConfigurationPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    debuggerGroupContainer.setOpenOcdConfigurationPath(
                        event.getNewValue().toString());
                    updateLaunchConfigurationDialog();
                }
            }
        });

        if (openOcdConfigurationPathEditor != null) {
            if (!ftdiDeviceCombo.getText().equalsIgnoreCase(
                    FtdiDevice.CUSTOM.toString())) {
                openOcdConfigurationPathEditor.setEnabled(false, compositeCom);
            } else {
                openOcdConfigurationPathEditor.setEnabled(true, compositeCom);
            }
        }
    }

    private void updateFtdiCoreCombo() {
        ftdiCoreCombo.removeAll();
        java.util.List<FtdiCore> cores = debuggerGroupContainer.getFtdiDevice().getCores();
        String text = cores.get(0).toString();
        for (FtdiCore core : cores) {
            ftdiCoreCombo.add(core.toString());
            if (debuggerGroupContainer.getFtdiCore() == core) {
                /*
                 * Should select current ftdiCore if it is present in cores list in order to be able
                 * to initialize from configuration. Otherwise ftdiCore field will be rewritten to
                 * the selected core when we initialize FTDI_DeviceCombo
                 */
                text = core.toString();
            }
        }
        ftdiCoreCombo.setText(text);
    }

    private void createTabItemNsim(Composite subComp) {
        createTabItemNsim = true;

        groupNsim = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
                GridData.FILL_HORIZONTAL);
        final Composite compositeNsim = SWTFactory.createComposite(groupNsim, 3, 5, GridData.FILL_BOTH);

        GridData gridData = new GridData();

        nsimBinaryPathEditor = new FileFieldEditor("nsimBinPath", "nSIM executable", false,
                StringButtonFieldEditor.VALIDATE_ON_KEY_STROKE, compositeNsim);

        nsimBinaryPathEditor.setStringValue(debuggerGroupContainer.getExternalToolsNsimPath());
        nsimBinaryPathEditor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty() == "field_editor_value") {
                    debuggerGroupContainer.setExternalToolsNsimPath((String) event.getNewValue());
                    updateLaunchConfigurationDialog();
                }
            }
        });

        launchTcfPropertiesButton = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        launchTcfPropertiesButton.setToolTipText("Pass specified TCF file to nSIM for parsing of nSIM properties (-tcf=path)" );
        launchTcfPropertiesButton.setSelection(
            debuggerGroupContainer.getExternalNsimTcfToolsEnabled());
        gridData = new GridData(SWT.BEGINNING);
        gridData.horizontalSpan = 3;
        launchTcfPropertiesButton.setLayoutData(gridData);
        launchTcfPropertiesButton.setText("Use TCF?");

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
        nsimTcfPathEditor.setEnabled((debuggerGroupContainer.getExternalNsimTcfToolsEnabled()),
            compositeNsim);

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
                    debuggerGroupContainer.setExternalNsimTcfToolsEnabled(true);
                    nsimTcfPathEditor.setEnabled(true, compositeNsim);

                } else {
                    debuggerGroupContainer.setExternalNsimTcfToolsEnabled(false);
                    launchTcfPropertiesButton.setSelection(false);
                    nsimTcfPathEditor.setEnabled(false, compositeNsim);
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });
        launchTcfPropertiesButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
                if (launchTcfPropertiesButton.getSelection() == true) {
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

        launchNsimJitProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        launchNsimJitProperties.setSelection(debuggerGroupContainer.getExternalNsimJitEnabled());
        launchNsimJitProperties.setText("JIT");
        launchNsimJitProperties.setToolTipText("Enable (1) or disable (0) JIT simulation mode (-p nsim_fast={0,1})");
        jitThreadSpinner = new Spinner(compositeNsim, SWT.NONE | SWT.BORDER);
        jitThreadSpinner.setToolTipText("Specify number of threads to use in JIT simulation mode (-p nsim_fast-num-threads=N)");
        final Label jitLabel = new Label(compositeNsim, SWT.BEGINNING);
        jitLabel.setText("JIT threads");
        jitThreadSpinner.setValues(1, 1, 100, 10, 1, 0);

        launchNsimJitProperties.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (launchNsimJitProperties.getSelection() == true) {
                    debuggerGroupContainer.setExternalNsimJitEnabled(true);
                    jitLabel.setEnabled(true);
                    jitThreadSpinner.setEnabled(true);

                } else {
                    debuggerGroupContainer.setExternalNsimJitEnabled(false);
                    jitLabel.setEnabled(false);
                    jitThreadSpinner.setEnabled(false);
                }
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });

        launchNsimJitProperties.setLayoutData(gridData);

        if (debuggerGroupContainer.getExternalNsimJitEnabled()) {
            jitLabel.setEnabled(true);
            jitThreadSpinner.setEnabled(true);
        }
        else {
            jitLabel.setEnabled(false);
            jitThreadSpinner.setEnabled(false);
        }

        if (!debuggerGroupContainer.getJitThread().equals("1"))
            jitThreadSpinner.setSelection(Integer.parseInt(debuggerGroupContainer.getJitThread()));
        else
            jitThreadSpinner.setSelection(1);

        jitThreadSpinner.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                debuggerGroupContainer.setJitThread(jitThreadSpinner.getText());
                updateLaunchConfigurationDialog();
            }
        });
        gridData = new GridData(SWT.BEGINNING);
        gridData.horizontalSpan = 2;
        jitLabel.setLayoutData(gridData);

        GridData gridDataNsim = new GridData(SWT.BEGINNING);
        gridDataNsim.horizontalSpan = 2;

        launchHostLinkProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        launchHostLinkProperties.setToolTipText("Enable or disable nSIM GNU host I/O support (-p nsim_emt={0,1}). The nsim_emt property works only if the application that is being simulated is compiled with the ARC GCC compiler.");
        launchHostLinkProperties.setSelection(
            debuggerGroupContainer.getExternalNsimHostLinkToolsEnabled());
        launchHostLinkProperties.setText("GNU host I/O support");
        launchHostLinkProperties.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                debuggerGroupContainer.setExternalNsimHostLinkToolsEnabled(
                    launchHostLinkProperties.getSelection());
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });

        launchHostLinkProperties.setLayoutData(gridDataNsim);

        launchMemoryExceptionProperties = new Button(compositeNsim, SWT.CHECK); //$NON-NLS-1$ //6-3
        launchMemoryExceptionProperties.setToolTipText("Simulate (1) or break (0) on memory exception (-p memory_exception_interrupt={0,1})");
        launchMemoryExceptionProperties.setSelection(
            debuggerGroupContainer.getExternalNsimMemoryExceptionToolsEnabled());
        launchMemoryExceptionProperties.setText("Memory Exception");
        launchMemoryExceptionProperties.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                debuggerGroupContainer.setExternalNsimMemoryExceptionToolsEnabled(
                    launchMemoryExceptionProperties.getSelection());
                updateLaunchConfigurationDialog();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }

        });

        launchMemoryExceptionProperties.setLayoutData(gridDataNsim);

        debuggerGroupContainer.createLaunchEnableExceptionPropertiesButton(compositeNsim,
            gridDataNsim);

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
    
    @Override
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        super.setLaunchConfigurationDialog(dialog);
        workingDirectoryBlockNsim.setLaunchConfigurationDialog(dialog);
    }

    @Override
    public String getErrorMessage() {
        String errorMessage = super.getErrorMessage();
        if (errorMessage == null && !groupNsim.isDisposed()) {
            return workingDirectoryBlockNsim.getErrorMessage();
        }
        return errorMessage;
    }
}
