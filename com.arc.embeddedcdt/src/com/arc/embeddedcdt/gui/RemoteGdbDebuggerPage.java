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
import java.util.Observable;
import java.util.Observer;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.GdbDebuggerPage;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.internal.launch.remote.Messages;
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
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.ArcGdbServer;
import com.arc.embeddedcdt.common.LaunchFileFormatVersionChecker;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;
import com.arc.embeddedcdt.dsf.utils.ConfigurationWriter;
import com.arc.embeddedcdt.common.FtdiDevice;

/**
 * The dynamic debugger tab for remote launches using gdb server. The gdbserver settings are used to
 * start a gdbserver session on the remote and then to connect to it from the host. The DSDP-TM
 * project is used to accomplish this.
 */
@SuppressWarnings("restriction")
public class RemoteGdbDebuggerPage extends GdbDebuggerPage {
    protected Combo externalToolsCombo;
    private ARCWorkingDirectoryBlock workingDirectoryBlockNsim = new ARCWorkingDirectoryBlock();

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
        super.initializeFrom(configuration);
        ConfigurationReader configurationReader = new ConfigurationReader(configuration);
        debuggerGroupContainer.initializeFrom(configurationReader);
        debuggerGroupContainer.setGdbPath(configurationReader.getOrDefault(getDefaultGdbPath(), "",
            configurationReader.getGdbPath()));
        fGDBCommandText.setText(debuggerGroupContainer.getGdbPath());
        debuggerGroupContainer.setGdbServer(configurationReader.getGdbServer());

        workingDirectoryBlockNsim.initializeFrom(configuration);

        externalToolsCombo.setText(debuggerGroupContainer.getGdbServer().toString());

        if (DebuggerGroupContainer.groupGenericGdbServer != null && !DebuggerGroupContainer.groupGenericGdbServer.isDisposed())
            debuggerGroupContainer.setTextForGdbServerIpAddressText(
                debuggerGroupContainer.getHostName());

        int previous = externalToolsCombo.indexOf(debuggerGroupContainer.getGdbServer().toString());
        if (previous > -1)
            externalToolsCombo.remove(previous);
        /*
         * Reading gdbServer again from configuration because field gdbServer might have been
         * changed by event handler called by extTools.remove(previous)
         */
        debuggerGroupContainer.setGdbServer(configurationReader.getGdbServer());
        externalToolsCombo.add(debuggerGroupContainer.getGdbServer().toString(), 0);
        externalToolsCombo.select(0);

    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);
        ConfigurationReader configurationReader = new ConfigurationReader(configuration);
        final String programName = configurationReader.getProgramName();
        configuration.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME,
            programName.replace('\\', '/'));
        if (!DebuggerGroupContainer.groupNsim.isDisposed()) {
            workingDirectoryBlockNsim.performApply(configuration);
        }

        ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
        debuggerGroupContainer.performApply(configurationWriter);
        String nsimDefaultPath = DebuggerGroupContainer.getNsimdrvDefaultPath();
        configurationWriter.setNsimDefaultPath(nsimDefaultPath);
        debuggerGroupContainer.setGdbPath(fGDBCommandText.getText());

        configurationWriter.setFileFormatVersion(
            LaunchConfigurationConstants.CURRENT_FILE_FORMAT_VERSION);
        /* Because there is no setAttribute(String, long) method. */
        configurationWriter.setTimeStamp(String.format("%d", System.currentTimeMillis()));

        if (DebuggerGroupContainer.groupGenericGdbServer != null && !DebuggerGroupContainer.groupGenericGdbServer.isDisposed()) {
            debuggerGroupContainer.setHostName(
                debuggerGroupContainer.getTextFromGdbServerIpAddressText());
            configurationWriter.setHostAddress(DebuggerGroupContainer.getAttributeValueFromString(
                debuggerGroupContainer.getHostName()));
        }
    }

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
                    debuggerGroupContainer.setGdbServer(ArcGdbServer.fromString(combo.getText()));
                } catch (IllegalArgumentException e) {
                    debuggerGroupContainer.setGdbServer(ArcGdbServer.DEFAULT_GDB_SERVER);
                }

                if (debuggerGroupContainer.getGdbServer() == ArcGdbServer.JTAG_OPENOCD) {
                    debuggerGroupContainer.setPortNumberText(
                        LaunchConfigurationConstants.DEFAULT_OPENOCD_PORT);

                    DebuggerGroupContainer.groupNsim.dispose();
                    if (DebuggerGroupContainer.groupGenericGdbServer != null) {
                        DebuggerGroupContainer.groupGenericGdbServer.dispose();
                    }
                    DebuggerGroupContainer.groupComAshling.dispose();
                    groupComCustomGdb.dispose();

                    if (!debuggerGroupContainer.getCreateTabItemCom()) {
                        if (!DebuggerGroupContainer.groupCom.isDisposed())
                            DebuggerGroupContainer.groupCom.dispose();

                        createTabItemCom(subComp);
                    }
                    DebuggerGroupContainer.groupCom.setText(debuggerGroupContainer.getGdbServer().toString());
                    debuggerGroupContainer.setCreateTabItemNsim(false);
                    debuggerGroupContainer.setCreateTabItemComAshling(false);
                    DebuggerGroupContainer.groupCom.setVisible(true);
                    debuggerGroupContainer.setCreateTabItemGenericGdbServer(false);
                    debuggerGroupContainer.setCreateTabItemCustomGdb(false);
                } else if (debuggerGroupContainer.getGdbServer() == ArcGdbServer.JTAG_ASHLING) {
                    debuggerGroupContainer.setPortNumberText(
                        LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

                    DebuggerGroupContainer.groupNsim.dispose();
                    if (DebuggerGroupContainer.groupGenericGdbServer != null) {
                        DebuggerGroupContainer.groupGenericGdbServer.dispose();
                    }
                    DebuggerGroupContainer.groupCom.dispose();
                    groupComCustomGdb.dispose();
                    debuggerGroupContainer.setCreateTabItemNsim(false);
                    debuggerGroupContainer.setCreateTabItemGenericGdbServer(false);
                    debuggerGroupContainer.setCreateTabItemCom(false);
                    debuggerGroupContainer.setCreateTabItemCustomGdb(false);

                    if (!debuggerGroupContainer.getCreateTabItemComAshling()) {
                        if (!DebuggerGroupContainer.groupComAshling.isDisposed())
                            DebuggerGroupContainer.groupComAshling.dispose();

                        createTabItemComAshling(subComp);
                    }

                    DebuggerGroupContainer.groupComAshling.setText(debuggerGroupContainer.getGdbServer().toString());
                    DebuggerGroupContainer.groupComAshling.setVisible(true);
                } else if (debuggerGroupContainer.getGdbServer() == ArcGdbServer.NSIM) {
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

                    DebuggerGroupContainer.groupCom.dispose();
                    DebuggerGroupContainer.groupComAshling.dispose();
                    groupComCustomGdb.dispose();
                    if (DebuggerGroupContainer.groupGenericGdbServer != null) {
                        DebuggerGroupContainer.groupGenericGdbServer.dispose();
                    }
                    if (!debuggerGroupContainer.getCreateTabItemNsim()) {
                        if (!DebuggerGroupContainer.groupNsim.isDisposed())
                            DebuggerGroupContainer.groupNsim.dispose();
                        createTabItemNsim(subComp);

                        debuggerGroupContainer.getLaunchTcf().setSelection(
                            debuggerGroupContainer.getExternalNsimPropertiesEnabled());
                        debuggerGroupContainer.getLaunchTcfPropertiesButton().setSelection(
                            debuggerGroupContainer.getExternalNsimTcfToolsEnabled());
                        debuggerGroupContainer.getLaunchNsimJitProperties().setSelection(
                            debuggerGroupContainer.getExternalNsimJitEnabled());
                        debuggerGroupContainer.getLaunchHostLinkProperties().setSelection(
                            debuggerGroupContainer.getExternalNsimHostLinkToolsEnabled());
                        debuggerGroupContainer.getLaunchMemoryExceptionProperties().setSelection(
                            debuggerGroupContainer.getExternalNsimMemoryExceptionToolsEnabled());
                        debuggerGroupContainer.setSelectionForLaunchEnableExceptionPropertiesButton();

                    }
                    DebuggerGroupContainer.groupNsim.setText(debuggerGroupContainer.getGdbServer().toString());
                    debuggerGroupContainer.setCreateTabItemCom(false);
                    debuggerGroupContainer.setCreateTabItemComAshling(false);
                    DebuggerGroupContainer.groupNsim.setVisible(true);
                    debuggerGroupContainer.setCreateTabItemGenericGdbServer(false);
                    debuggerGroupContainer.setCreateTabItemCustomGdb(false);

                } else if (debuggerGroupContainer.getGdbServer() == ArcGdbServer.GENERIC_GDBSERVER) {
                    DebuggerGroupContainer.groupCom.dispose();
                    DebuggerGroupContainer.groupComAshling.dispose();
                    DebuggerGroupContainer.groupNsim.dispose();
                    groupComCustomGdb.dispose();
                    if (!debuggerGroupContainer.getCreateTabItemGenericGdbServer()) {
                        if (DebuggerGroupContainer.groupGenericGdbServer != null && !DebuggerGroupContainer.groupGenericGdbServer.isDisposed())
                            DebuggerGroupContainer.groupGenericGdbServer.dispose();

                        createTabItemHostAddress(subComp);
                    }
                    debuggerGroupContainer.setCreateTabItemCom(false);
                    debuggerGroupContainer.setCreateTabItemComAshling(false);
                    debuggerGroupContainer.setCreateTabItemNsim(false);
                    debuggerGroupContainer.setCreateTabItemCustomGdb(false);
                    DebuggerGroupContainer.groupGenericGdbServer.setText(debuggerGroupContainer.getGdbServer().toString());
                    DebuggerGroupContainer.groupGenericGdbServer.setVisible(true);

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

                    if (!DebuggerGroupContainer.groupCom.isDisposed())
                        DebuggerGroupContainer.groupCom.setVisible(false);
                    if (!DebuggerGroupContainer.groupNsim.isDisposed())
                        DebuggerGroupContainer.groupNsim.setVisible(false);
                    if (!DebuggerGroupContainer.groupComAshling.isDisposed())
                        DebuggerGroupContainer.groupComAshling.setVisible(false);
                    if (!groupComCustomGdb.isDisposed()) {
                        groupComCustomGdb.setVisible(false);
                    }

                } else if (debuggerGroupContainer.getGdbServer() == ArcGdbServer.CUSTOM_GDBSERVER) {
                    debuggerGroupContainer.setPortNumberText(
                        LaunchConfigurationConstants.DEFAULT_OPELLAXD_PORT);

                    DebuggerGroupContainer.groupNsim.dispose();
                    DebuggerGroupContainer.groupCom.dispose();
                    DebuggerGroupContainer.groupComAshling.dispose();
                    DebuggerGroupContainer.groupGenericGdbServer.dispose();
                    debuggerGroupContainer.setCreateTabItemNsim(false);
                    debuggerGroupContainer.setCreateTabItemCom(false);
                    debuggerGroupContainer.setCreateTabItemComAshling(false);
                    debuggerGroupContainer.setCreateTabItemGenericGdbServer(false);
                    if (!debuggerGroupContainer.getCreateTabItemCustomGdb()) {
                        if (!groupComCustomGdb.isDisposed())
                            groupComCustomGdb.dispose();

                        createTabCustomGdb(subComp);
                    }

                    groupComCustomGdb.setText(debuggerGroupContainer.getGdbServer().toString());
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

        if (!debuggerGroupContainer.getCreateTabItemNsim())
            createTabItemNsim(subComp);
        if (!debuggerGroupContainer.getCreateTabItemCom())
            createTabItemCom(subComp);
        if (!debuggerGroupContainer.getCreateTabItemComAshling())
            createTabItemComAshling(subComp);
        if (!debuggerGroupContainer.getCreateTabItemGenericGdbServer())
            createTabItemHostAddress(subComp);
        if (!debuggerGroupContainer.getCreateTabItemCustomGdb())
            createTabCustomGdb(subComp);
    }


    private void createTabCustomGdb(Composite subComp) {
        debuggerGroupContainer.setCreateTabItemCustomGdb(true);

        groupComCustomGdb = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3,
                5, GridData.FILL_HORIZONTAL);
        final Composite compositeCustomGdb = SWTFactory.createComposite(groupComCustomGdb, 3, 5,
                GridData.FILL_BOTH);

        debuggerGroupContainer.createTabCustomGdb(compositeCustomGdb);

    }


    private void createTabItemHostAddress(Composite subComp) {
        final int screenPpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
        final int minTextWidth = 2 * screenPpi;
        debuggerGroupContainer.setCreateTabItemGenericGdbServer(true);
        DebuggerGroupContainer.groupGenericGdbServer = SWTFactory.createGroup(subComp,
                ArcGdbServer.GENERIC_GDBSERVER.toString(), 3, 5, GridData.FILL_HORIZONTAL);
        final Composite compCOM = SWTFactory.createComposite(DebuggerGroupContainer.groupGenericGdbServer, 3, 5,
                GridData.FILL_BOTH);

        Label label = new Label(compCOM, SWT.LEFT);
        label.setText("Host Address:");

        debuggerGroupContainer.createGdbServerIpAddressText(compCOM, minTextWidth);
    }

    private void createTabItemComAshling(Composite subComp) {
        debuggerGroupContainer.setCreateTabItemComAshling(true);

        DebuggerGroupContainer.groupComAshling = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
                GridData.FILL_HORIZONTAL);
        final Composite compositeCom = SWTFactory.createComposite(DebuggerGroupContainer.groupComAshling, 3, 5,
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

        if (debuggerGroupContainer.getGdbServer() != null) {

            switch (debuggerGroupContainer.getGdbServer()) {
            case JTAG_OPENOCD:
                if (DebuggerGroupContainer.groupCom.isDisposed()) {
                    return true;
                }
                if (!isValidFileFieldEditor(debuggerGroupContainer.getOpenOcdBinaryPathEditor())) {
                    return false;
                }
                if (debuggerGroupContainer.getFtdiDevice() == FtdiDevice.CUSTOM) {
                    if (!isValidFileFieldEditor(
                        debuggerGroupContainer.getOpenOcdConfigurationPathEditor())) {
                        return false;
                    }
                } else {
                    File configurationFile = new File(
                        debuggerGroupContainer.accessOpenOcdConfigurationPath());
                    if (!configurationFile.exists()) {
                        setErrorMessage(
                                "Default OpenOCD configuration file for this development system \'"
                                        + debuggerGroupContainer.getOpenOcdConfigurationPathEditor()
                                        + "\' must exist");
                        return false;
                    }
                }
                break;
            case JTAG_ASHLING:
                if (DebuggerGroupContainer.groupComAshling.isDisposed()){
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
                if (DebuggerGroupContainer.groupNsim.isDisposed()) {
                    return true;
                }
                if (!isValidFileFieldEditor(debuggerGroupContainer.getNsimBinaryPathEditor())
                        || (debuggerGroupContainer.getLaunchTcf().getSelection()
                                && !isValidFileFieldEditor(
                                    debuggerGroupContainer.getNsimTcfPathEditor()))
                        || (debuggerGroupContainer.getLaunchTcfPropertiesButton().getSelection()
                                && !isValidFileFieldEditor(
                                    debuggerGroupContainer.getNsimPropertiesPathEditor()))
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

    private void createTabItemCom(Composite subComp) {
        DebuggerGroupContainer.groupCom = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
                GridData.FILL_HORIZONTAL);
        final Composite compositeCom =
            SWTFactory.createComposite(DebuggerGroupContainer.groupCom, 3, 5, GridData.FILL_BOTH);
        debuggerGroupContainer.createTabItemCom(compositeCom);
    }

    private void createTabItemNsim(Composite subComp) {
        debuggerGroupContainer.setCreateTabItemNsim(true);

        DebuggerGroupContainer.groupNsim = SWTFactory.createGroup(subComp, externalToolsCombo.getItem(0), 3, 5,
                GridData.FILL_HORIZONTAL);
        final Composite compositeNsim = SWTFactory.createComposite(DebuggerGroupContainer.groupNsim, 3, 5, GridData.FILL_BOTH);

        GridData gridData = new GridData();
        GridData gridData2 = new GridData();

        debuggerGroupContainer.createNsimBinaryPathEditor(compositeNsim);
        debuggerGroupContainer.createNsimTcfPathEditor(compositeNsim);
        debuggerGroupContainer.getNsimTcfPathEditor().setEnabled(
            debuggerGroupContainer.getExternalNsimTcfToolsEnabled(), compositeNsim);
        debuggerGroupContainer.createNsimPropertiesPathEditor(compositeNsim);
        debuggerGroupContainer.getNsimBinaryPathEditor().setEnabled(
            debuggerGroupContainer.getExternalNsimPropertiesEnabled(), compositeNsim);
        gridData = new GridData(SWT.BEGINNING);
        gridData.horizontalSpan = 3;

        debuggerGroupContainer.createLaunchTcf(compositeNsim, gridData,
            debuggerGroupContainer.getLaunchTcfPropertiesButton());
        debuggerGroupContainer.createLaunchTcfPropertiesButton(compositeNsim, gridData);
        // JIT

        gridData = new GridData(SWT.BEGINNING);
        gridData.horizontalSpan = 3;

        gridData2 = new GridData(SWT.BEGINNING);
        gridData2.horizontalSpan = 2;

        debuggerGroupContainer.createJitThreadSpinner(compositeNsim, gridData, gridData2);

        GridData gridDataNsim = new GridData(SWT.BEGINNING);
        gridDataNsim.horizontalSpan = 2;

        debuggerGroupContainer.createLaunchHostLinkProperties(compositeNsim, gridDataNsim);

        debuggerGroupContainer.createLaunchMemoryExceptionProperties(compositeNsim, gridDataNsim);

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
        if (errorMessage == null && !DebuggerGroupContainer.groupNsim.isDisposed()) {
            return workingDirectoryBlockNsim.getErrorMessage();
        }
        return errorMessage;
    }
}
