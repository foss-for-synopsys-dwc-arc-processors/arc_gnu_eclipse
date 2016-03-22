/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.gui;

import java.io.File;

import org.eclipse.cdt.launch.internal.ui.LaunchMessages;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.launch.internal.ui.WorkingDirectoryBlock;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.common.InvalidDirectoryPathException;

/**
 * This class is used to add working directory block to GDB server setting page for nSIM. Other GDB
 * servers do not support passing working directory to them, so this block should be there for nSIM
 * only. Need to override some methods of WorkingDirectoryBlock class due to specifics of
 * RemoteGDBDebuggerPage: nSIM group might be disposed, so we might not be able to set (or read)
 * fields' values in IDE directly, as it is done in WorkingDirectoryBlock class. Instead we store
 * fields' values in class fields.
 * 
 * Also validity check for working directory value is improved comparing to WorkingDirectoryBlock.
 *
 */
@SuppressWarnings("restriction")
public class ARCWorkingDirectoryBlock extends WorkingDirectoryBlock {

    // Shows if 'Use default' is checked
    private boolean isDefaultWorkingDir = true;

    // Working directory path containing unresolved variables
    private String workingDir = null;

    /**
     * A listener to update for text changes and widget selection
     */
    private class WidgetListener extends SelectionAdapter implements ModifyListener {

        @Override
        public void modifyText(ModifyEvent e) {
            workingDir = getAttributeValueFrom(fWorkingDirText);
            updateLaunchConfigurationDialog();
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            if (source == fWorkspaceButton) {
                handleWorkspaceDirBrowseButtonSelected();
            } else if (source == fFileSystemButton) {
                handleWorkingDirBrowseButtonSelected();
            } else if (source == fUseDefaultWorkingDirButton) {
                isDefaultWorkingDir = fUseDefaultWorkingDirButton.getSelection();
                handleUseDefaultWorkingDirButtonSelected();
            }
        }
    }

    private WidgetListener fListener = new WidgetListener();

    /*
     * The way that WorkingDirectoryBlock.createControl() method creates working directory group is
     * not suitable for nSIM page, because created group is contained in just one of the three
     * columns that nSIM group has. So we specify that working directory group should have one
     * column.
     * 
     * The other thing that differs from WorkingDirectoryBlock.createControl() is that we set values
     * of the working directory path field and "Use default" checkbox here since we do not set these
     * values in initialize() method because they might be disposed when initialize() is called.
     */
    @Override
    public void createControl(Composite parent) {
        Font font = parent.getFont();

        Group group = SWTFactory.createGroup(parent,
                LaunchMessages.WorkingDirectoryBlock_Working_directory, 1, 15,
                GridData.FILL_HORIZONTAL);
        setControl(group);

        fWorkingDirText = new Text(group, SWT.SINGLE | SWT.BORDER);
        fWorkingDirText.getAccessible().addAccessibleListener(new AccessibleAdapter() {
            @Override
            public void getName(AccessibleEvent e) {
                e.result = LaunchMessages.WorkingDirectoryBlock_Working_directory;
            }
        });
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        fWorkingDirText.setLayoutData(gd);
        fWorkingDirText.setFont(font);
        fWorkingDirText.addModifyListener(fListener);

        fUseDefaultWorkingDirButton = new Button(group, SWT.CHECK);
        fUseDefaultWorkingDirButton.setText(LaunchMessages.WorkingDirectoryBlock_Use_default);
        gd = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
        fUseDefaultWorkingDirButton.setLayoutData(gd);
        fUseDefaultWorkingDirButton.setFont(font);
        fUseDefaultWorkingDirButton.addSelectionListener(fListener);

        Composite buttonComp = new Composite(group, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        buttonComp.setLayout(layout);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        buttonComp.setLayoutData(gd);
        buttonComp.setFont(font);
        fWorkspaceButton = createPushButton(buttonComp, LaunchMessages.WorkingDirectoryBlock_0,
                null);
        fWorkspaceButton.addSelectionListener(fListener);

        fFileSystemButton = createPushButton(buttonComp, LaunchMessages.WorkingDirectoryBlock_1,
                null);
        fFileSystemButton.addSelectionListener(fListener);

        fVariablesButton = createVariablesButton(buttonComp,
                LaunchMessages.WorkingDirectoryBlock_17, fWorkingDirText);

        if (workingDir != null) {
            fWorkingDirText.setText(workingDir);
        }
        fUseDefaultWorkingDirButton.setSelection(isDefaultWorkingDir);
        handleUseDefaultWorkingDirButtonSelected();
    }

    /*
     * Check that working directory path field is not empty, resolve variables and check that file
     * corresponding to obtained path exists.
     */
    @Override
    public boolean isValid(ILaunchConfiguration config) {
        setErrorMessage(null);
        setMessage(null);
        String workingDir = getAttributeValueFrom(fWorkingDirText);
        try {
            resolveDirectoryPath(workingDir);
        } catch (InvalidDirectoryPathException e) {
            setErrorMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public static String resolveDirectoryPath(String path) throws InvalidDirectoryPathException {
        if (path == null || path.isEmpty()) {
            throw new InvalidDirectoryPathException("Working directory can not be empty.");
        }

        IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
        String workingDirPath;
        try {
            workingDirPath = manager.performStringSubstitution(path, true);
        } catch (CoreException e) {
            throw new InvalidDirectoryPathException(e.getMessage());
        }
        if (!new File(workingDirPath).isDirectory()) {
            throw new InvalidDirectoryPathException(
                    "Directory \'" + workingDirPath + "\' does not exist.");
        }
        return workingDirPath;
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        setLaunchConfiguration(configuration);
        try {
            workingDir = configuration.getAttribute(
                    LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_WORKING_DIRECTORY, (String) null);
            isDefaultWorkingDir = configuration.getAttribute(
                    LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_USE_DEFAULT_DIRECTORY, true);
            if (workingDir == null) {
                isDefaultWorkingDir = true;
            }
        } catch (CoreException e) {
            setErrorMessage(
                    LaunchMessages.WorkingDirectoryBlock_Exception_occurred_reading_configuration_15
                            + e.getStatus().getMessage());
            LaunchUIPlugin.log(e);
        }
    }

    /*
     * Save working directory to configuration even if it is default one so that we could read it
     * from configuration when launching nSIM. This way we have to save value of 'Use default'
     * checkbox too.
     */
    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_WORKING_DIRECTORY, workingDir);
        configuration.setAttribute(
                LaunchConfigurationConstants.ATTR_DEBUGGER_NSIM_USE_DEFAULT_DIRECTORY,
                isDefaultWorkingDir);
    }

    /*
     * Need to differentiate empty string from null since if working directory path is null it is
     * considered that default working directory is chosen. So if you press 'Apply' button while
     * working directory path is empty and then close debug configuration and open it again, you
     * will see that default directory is chosen.
     * 
     * We do not allow working directory to be empty either, but I think that behavior described
     * above is counterintuitive and should be changed to more clear one.
     */
    @Override
    protected String getAttributeValueFrom(Text text) {
        return text.getText().trim();
    }

    @Override
    protected boolean isDefaultWorkingDirectory() {
        return isDefaultWorkingDir;
    }
}
