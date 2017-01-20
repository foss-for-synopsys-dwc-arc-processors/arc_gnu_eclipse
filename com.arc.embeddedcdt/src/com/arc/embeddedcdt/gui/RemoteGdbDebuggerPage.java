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
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.TabFolder;
import com.arc.embeddedcdt.LaunchConfigurationConstants;
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

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        LaunchFileFormatVersionChecker.getInstance().check(configuration);
        super.initializeFrom(configuration);
        debuggerGroupContainer.initializeFrom(configuration);
        fGDBCommandText.setText(debuggerGroupContainer.getGdbPath());
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);
        ConfigurationReader configurationReader = new ConfigurationReader(configuration);
        final String programName = configurationReader.getProgramName();
        configuration.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME,
            programName.replace('\\', '/'));

        ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
        debuggerGroupContainer.performApply(configurationWriter, configuration);
        debuggerGroupContainer.setGdbPath(fGDBCommandText.getText());
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
                        || !debuggerGroupContainer.getWorkingDirectoryBlockNsim().isValid(
                            configuration)) {
                     return false;
                }
                break;
            case CUSTOM_GDBSERVER:
                if (DebuggerGroupContainer.groupComCustomGdb.isDisposed()) {
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
        debuggerGroupContainer.createGdbServerSettingsTab(tabFolder);
    }
    
    @Override
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        super.setLaunchConfigurationDialog(dialog);
        debuggerGroupContainer.getWorkingDirectoryBlockNsim().setLaunchConfigurationDialog(dialog);
    }

    @Override
    public String getErrorMessage() {
        String errorMessage = super.getErrorMessage();
        if (errorMessage == null && !DebuggerGroupContainer.groupNsim.isDisposed()) {
            return debuggerGroupContainer.getWorkingDirectoryBlockNsim().getErrorMessage();
        }
        return errorMessage;
    }
}
