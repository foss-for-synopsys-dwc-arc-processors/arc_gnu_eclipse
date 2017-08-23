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

package com.arc.embeddedcdt.gui;

import org.eclipse.cdt.launch.ui.CLaunchConfigurationTab;
import org.eclipse.cdt.launch.ui.ICDTLaunchHelpContextIds;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.arc.embeddedcdt.LaunchImages;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;
import com.arc.embeddedcdt.dsf.utils.ConfigurationWriter;

public class CommandTab extends CLaunchConfigurationTab {

    protected Label fPrgmArgumentsLabelInit;
    protected Label fPrgmArgumentsLabelRun; // this variable is for showing run command
    protected Text fPrgmArgumentsTextRun; // this variable is for getting user's input run command
    static String initcom = "";// this variable is for saving user's input initial command
    static String runcom = "";// this variable is for saving user's input run command
    protected static Text fPrgmArgumentsTextInit;// this variable for showing which target is be
                                                 // selected
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
     */

    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        setControl(comp);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
                ICDTLaunchHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_ARGUMNETS_TAB);

        GridLayout topLayout = new GridLayout();
        comp.setLayout(topLayout);

        createVerticalSpacer(comp, 1);
        createCommandsComponent(comp, 1);
    }

    protected void createCommandsComponent(Composite comp, int i) {
        Composite argsComp = new Composite(comp, SWT.NONE);
        GridLayout projLayout = new GridLayout();
        projLayout.numColumns = 1;
        projLayout.marginHeight = 0;
        projLayout.marginWidth = 0;
        argsComp.setLayout(projLayout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = i;
        argsComp.setLayoutData(gd);

        fPrgmArgumentsLabelInit = new Label(argsComp, SWT.NONE);
        fPrgmArgumentsLabelInit.setText("'Initialize' commands"); //$NON-NLS-1$
        fPrgmArgumentsTextInit = new Text(argsComp,
                SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 100;
        fPrgmArgumentsTextInit.setLayoutData(gd);
        fPrgmArgumentsTextInit.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent evt) {
                initcom = fPrgmArgumentsTextInit.getText();
                updateLaunchConfigurationDialog();
            }
        });

        // yunlu change for debug session preset value end

        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.heightHint = 25;
        fPrgmArgumentsLabelRun = new Label(argsComp, SWT.NONE);// 3-1 and 3-2 and 3-3
        fPrgmArgumentsLabelRun.setText("'Run' commands"); //$NON-NLS-1$
        fPrgmArgumentsLabelRun.setLayoutData(gd);
        fPrgmArgumentsTextRun = new Text(argsComp,
                SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);// 4-1 and 4-2
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 100;
        fPrgmArgumentsTextInit.setLayoutData(gd);
        gd.horizontalSpan = 3;
        fPrgmArgumentsTextRun.setLayoutData(gd);
        fPrgmArgumentsTextRun.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent evt) {
                runcom = fPrgmArgumentsTextRun.getText();
                updateLaunchConfigurationDialog();
            }
        });

    }

    protected void handleBinarylaunchButtonSelected() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.
     * ILaunchConfigurationWorkingCopy)
     */
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        ConfigurationWriter cfgWriter = new ConfigurationWriter(configuration);
        cfgWriter.setUserInitCommands(null);
        cfgWriter.setUserRunCommands(null);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.
     * ILaunchConfiguration)
     */
    public void initializeFrom(ILaunchConfiguration configuration) {
        ConfigurationReader cfgReader = new ConfigurationReader(configuration);
        fPrgmArgumentsTextInit.setText(cfgReader.getUserInitCommands());
        fPrgmArgumentsTextRun.setText(cfgReader.getUserRunCommands());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.
     * ILaunchConfigurationWorkingCopy)
     */
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        ConfigurationWriter cfgWriter = new ConfigurationWriter(configuration);
        cfgWriter.setUserInitCommands(initcom);
        cfgWriter.setUserRunCommands(runcom);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
     */
    public String getName() {
        return "Commands";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
     */
    public Image getImage() {
        return LaunchImages.get(LaunchImages.IMG_VIEW_COMMANDS_TAB);
    }

    /**
     * Retuns the string in the text widget, or <code>null</code> if empty.
     * 
     * @return text or <code>null</code>
     */
    protected String getAttributeValueFrom(Text text) {
        String content = text.getText().trim();
        if (content.length() > 0) {
            return content;
        }
        return null;
    }

    public static String getAttributeValueFromString(String string) {
        String content = string;
        if (content.length() > 0) {
            return content;
        }
        return null;
    }

    protected String getAttributeValueFromCombo(Combo combo) {

        String content = combo.getText().trim();
        if (content.length() > 0) {
            return content;
        }

        return null;
    }

    @Override
    public String getId() {
        return "com.arc.embeddedcdt.debuggertab.dsf";
    }
}
