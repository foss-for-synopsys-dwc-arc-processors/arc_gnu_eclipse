/*******************************************************************************
 * Copyright (c) 2005, 2014 Synopsys, Incorporated
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Synopsys, Inc. - Initial implementation
 * Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/
package com.arc.cdt.toolchain.arc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.cdt.toolchain.IOptionEnablementManager;
import com.arc.cdt.toolchain.OptionEnablementManager;
import com.arc.cdt.toolchain.tcf.TcfContent;

public class ArcOptionEnablementManager extends OptionEnablementManager {

    private static final String TCF_OPTION_ID = "org.eclipse.cdt.cross.arc.gnu.base.option.target.tcf";
    private static final String TCF_FILE_OPTION_ID = "org.eclipse.cdt.cross.arc.gnu.base.option.target.filefortcf";
    private static final String ABI_SELECTION_ID = "org.eclipse.cdt.cross.arc.gnu.base.option.target.abiselection";
    private static final String TCF_MEMORY_MAP = "org.eclipse.cdt.cross.arc.gnu.base.option.target.maptcf";

    private static final String[] DISABLE_WHEN_NO_TCF = { TCF_FILE_OPTION_ID, TCF_MEMORY_MAP };
    private static final String[] ENABLE_WHEN_TCF = { TCF_FILE_OPTION_ID, TCF_MEMORY_MAP, TCF_OPTION_ID,
            ABI_SELECTION_ID };

    private static final String[] LINKER_SCRIPT_IDS = { "org.eclipse.cdt.cross.arc.gnu.c.link.option.scriptfile",
            "org.eclipse.cdt.cross.arc.gnu.cpp.link.option.scriptfile" };

    private List<String> targetOptions;

    public ArcOptionEnablementManager() {
        addObserver(new Observer());
    }

    private boolean useTcf;
    private boolean tcfLinkSelected;
    private String tcfPath = "";
    private String processor = null;

    private void readTargetOptions() {
        targetOptions = new ArrayList<String>();
        for (IOption option : getToolChain().getOptions()) {
            if (option.getCategory().getBaseId().contains("category.target")) {
                targetOptions.add(option.getBaseId());
            }
        }
    }

    private List<String> getToolChainSpecificOption(String optionId) {
        List<String> list = new ArrayList<String>();
        for (IOption option : getToolChain().getOptions()) {
            IOption tmp = option;
            while (tmp != null) {
                if (tmp.getBaseId().equals(optionId)) {
                    list.add(option.getBaseId());
                }
                tmp = tmp.getSuperClass();
            }
        }
        for (ITool tool : getToolChain().getTools()) {
            for (IOption option : tool.getOptions()) {
                IOption tmp = option;
                while (tmp != null) {
                    if (tmp.getBaseId().equals(optionId)) {
                        list.add(option.getBaseId());
                    }
                    tmp = tmp.getSuperClass();
                }
            }
        }
        return list;
    }

    /**
     * Sets values for options from Target Processor tab, if they are also present in TCF. If cannot
     * set values, shows or logs the error or does nothing depending on <code>showStyle</code>.
     * 
     * @param showStyle
     *            style indicating what should be done if exception has occurred while importing.
     *            Applicable values are <code>StatusManager.NONE</code>,
     *            <code>StatusManager.LOG</code>, <code>StatusManager.SHOW</code> and
     *            <code>StatusManager.BLOCK</code>.
     */
    private void importOptionsFromTcf(int showStyle) {
        TcfContent tcfContent = null;
        tcfContent = TcfContent.readFile(new File(tcfPath), processor, showStyle);
        if (tcfContent != null) {
            Properties gccOptions = tcfContent.getGccOptions();
            for (String targetOptionId : targetOptions) {
                String command = getCommand(targetOptionId);
                if (command != null && !command.isEmpty() && !command.contains("-mabi")) {
                    IOption targetOption = (IOption) getOption(targetOptionId)[1];
                    command = command.split("=")[0];
                    String value = gccOptions.getProperty(command);
                    Object valueToSet = null;

                    if (value != null) {
                        if (!value.isEmpty()) {
                            command += "=" + value;
                        }
                        valueToSet = optionValueFromCommand(command);
                    } else {
                        valueToSet = getDefaultValue(targetOption);
                    }
                    if (valueToSet != null) {
                        if (valueToSet instanceof Boolean) {
                            /* Need to use ManagedBuildManager here instead of just option.setValue()
                             * because option.setValue() method rewrites default value of this option
                             * with what is set. But ManagedBuildManager does nothing with the default
                             * value and just changes build configuration for the project.*/
                            ManagedBuildManager.setOption(getConfig(), getToolChain(), targetOption,
                                    ((Boolean) valueToSet).booleanValue());
                        } else if (valueToSet instanceof String) {
                            ManagedBuildManager.setOption(getConfig(), getToolChain(), targetOption,
                                    (String) valueToSet);
                        } else {
                            throw new IllegalArgumentException("Invalid value to set option "
                                    + targetOptionId + ": " + valueToSet);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns default value for the option. If default value is not specified, it is considered
     * <code>false</code> for options with <code>IOption.BOOLEAN</code> value type and empty string
     * for options with <code>IOption.STRING</code> value type.
     * 
     * @param option
     *            option to get value for
     * @return default value for the option
     */
    private Object getDefaultValue(IOption option) {
        Object valueToSet = option.getDefaultValue();
        if (valueToSet != null) {
            return valueToSet;
        }
        int valueType = -1;
        try {
            valueType = option.getBasicValueType();
        } catch (BuildException e) {
            throw new IllegalArgumentException("Can not get value type for " + option.getName());
        }
        switch (valueType) {
        case IOption.BOOLEAN:
            return false;
        case IOption.STRING:
            return "";
        default:
            throw new IllegalArgumentException("Can not get default value for " + option.getName());
        }
    }

    class Observer implements IOptionEnablementManager.IObserver {

        /**
         * Called when an option value changes. Enable or disable any options that are dependent on
         * this one.
         * 
         * @param mgr
         * @param optionId
         */
        public void onOptionValueChanged(IOptionEnablementManager mgr, String optionId) {
            // `contains()` because sometimes this options has numeric suffix in the end.
            if (optionId.contains("option.target.processor")) {
                processor = getCommand(optionId);
                readTargetOptions();
            }
            if (optionId.contains("option.target.tcf")) {
                useTcf = (Boolean) mgr.getValue(optionId);
                if (useTcf) {
                    setEnabled(targetOptions, false);
                    for (String option : ENABLE_WHEN_TCF) {
                        setEnabled(getToolChainSpecificOption(option), true);
                    }
                    if (tcfLinkSelected) {
                        for (String option : LINKER_SCRIPT_IDS) {
                            setEnabled(getToolChainSpecificOption(option), false);
                        }
                    }
                    // Else do nothing because if TCF is not selected, these options were enabled
                    // either by default or when TCF was cancelled.

                    if (!tcfPath.isEmpty()) {
                        importOptionsFromTcf(StatusManager.SHOW);
                    }
                } else {
                    setEnabled(targetOptions, true);
                    for (String option : DISABLE_WHEN_NO_TCF) {
                        setEnabled(getToolChainSpecificOption(option), false);
                    }
                    if (tcfLinkSelected) {
                        for (String option : LINKER_SCRIPT_IDS) {
                            setEnabled(getToolChainSpecificOption(option), true);
                        }
                    }
                }
            }
            if (optionId.contains("option.target.filefortcf")) {
                tcfPath = (String)mgr.getValue(optionId);
                if (useTcf) {
                    // Don't show anything here because TcfValueHandler shows errors when
                    // TCF path value is changed
                    importOptionsFromTcf(StatusManager.NONE);
                }
            }
            if (optionId.contains("option.target.maptcf")) {
                tcfLinkSelected = (Boolean) mgr.getValue(optionId);
                if (tcfLinkSelected && useTcf) {
                    for (String option : LINKER_SCRIPT_IDS) {
                        setEnabled(getToolChainSpecificOption(option), false);
                    }
                } else
                    for (String option : LINKER_SCRIPT_IDS) {
                        setEnabled(getToolChainSpecificOption(option), true);
                    }
            }
        }

        public void onOptionEnablementChanged(IOptionEnablementManager mgr, String optionID) {
            // Nothing to do.

        }
    }
}
