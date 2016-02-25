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
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.cdt.toolchain.ArcCpu;
import com.arc.cdt.toolchain.IOptionEnablementManager;
import com.arc.cdt.toolchain.OptionEnablementManager;
import com.arc.cdt.toolchain.tcf.TcfContent;

public class ArcOptionEnablementManager extends OptionEnablementManager {

    private static final String TCF_OPTION_ID = "org.eclipse.cdt.cross.arc.gnu.base.option.target.tcf";
    private static final String TCF_FILE_OPTION_ID = "org.eclipse.cdt.cross.arc.gnu.base.option.target.filefortcf";
    private static final String ABI_SELECTION_ID = "org.eclipse.cdt.cross.arc.gnu.base.option.target.abiselection";
    private static final String TCF_MEMORY_MAP = "org.eclipse.cdt.cross.arc.gnu.base.option.target.maptcf";

    private static final String[] TCF_RELATED_OPTIONS = { TCF_FILE_OPTION_ID, TCF_MEMORY_MAP };
    private static final String[] NOT_ARCHITECTURE_OPTIONS = { TCF_FILE_OPTION_ID, TCF_MEMORY_MAP,
            TCF_OPTION_ID, ABI_SELECTION_ID };

    private static final String[] LINKER_SCRIPT_IDS = { "org.eclipse.cdt.cross.arc.gnu.c.link.option.scriptfile",
            "org.eclipse.cdt.cross.arc.gnu.cpp.link.option.scriptfile" };

    private List<String> targetOptions;
    private List<String> disabledForCpu = new ArrayList<>();

    private int settingOptionsLevel = 0;

    public ArcOptionEnablementManager() {
        addObserver(new Observer());
    }

    private boolean useTcf;
    private boolean tcfLinkSelected;
    private String tcfPath = "";
    private String mcpuFlag = null;

    private void readTargetOptions() {
        targetOptions = new ArrayList<String>();
        for (IOption option : getToolChain().getOptions()) {
            if (option.getCategory().getBaseId().contains("category.target")) {
                targetOptions.add(option.getBaseId());
            }
        }
        for (String baseOptionId : NOT_ARCHITECTURE_OPTIONS) {
            targetOptions.removeAll(getToolChainSpecificOption(baseOptionId));
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

    /*
     * Need one more method which set value to an option because set() does not affect GUI and
     * setOptionValue() calls option.setValue() method, which rewrites default value of the option.
     * This method just sets option value in GUI without rewriting defaults.
     */
    private void setValueWithoutRewritingDefaults(IOption option, Object value) {
        if (value instanceof Boolean) {
            /* Need to use ManagedBuildManager here instead of just option.setValue()
             * because option.setValue() method rewrites default value of this option
             * with what is set. But ManagedBuildManager does nothing with the default
             * value and just changes build configuration for the project.*/
            ManagedBuildManager.setOption(getConfig(), getToolChain(), option,
                    ((Boolean) value).booleanValue());
        } else if (value instanceof String) {
            /*
             * If option has enumerated type it is necessary to use enumeration id as a
             * value so that option.getSelectedEnum() would return id like it does when
             * value is selected by user in GUI.
             */
            try {
                if (option.getValueType() == IOption.ENUMERATED) {
                    value = option.getEnumeratedId((String)value);
                }
            } catch (BuildException e) {
                e.printStackTrace();
            }
            ManagedBuildManager.setOption(getConfig(), getToolChain(), option,
                    (String) value);
        } else {
            throw new IllegalArgumentException("Invalid value to set option "
                    + option.getName() + ": " + value);
        }
    }

    /**
     * Set all target options values to default. It is needed to set options values automatically
     * from TCF or from -mcpu value.
     */
    private void setOptionsToDefaults() {
        for (String targetOptionId : targetOptions) {
            IOption targetOption = (IOption) getOption(targetOptionId)[1];
            Object defaultValue = getDefaultValue(targetOption);
            setValueWithoutRewritingDefaults(targetOption, defaultValue);
        }
    }

    /**
     * For all architecture (-m) options from Target Processor tab set values from properties. If
     * there isn't a value for the option in properties, set the default one.
     * 
     * @param properties
     *            from which to get option values
     * 
     * @return list of options that were set from properties
     */
    private List<String> setOptionsFromProperties(Properties properties) {
        settingOptionsLevel++;
        if (settingOptionsLevel == 1) {
            setOptionsToDefaults();
        }

        List<String> setOptions = new ArrayList<>();
        for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
            String key = e.nextElement().toString();
            String keyValue = properties.getProperty(key);
            String command = key;
            command += (keyValue.isEmpty()) ? "" : "=" + keyValue;
            Object[] toSet = getOptionAndValueFromCommand(command);
            if (toSet != null) {
                setValueWithoutRewritingDefaults((IOption)toSet[0], toSet[1]);
                setOptions.add(((IOption)toSet[0]).getBaseId());
            }
        }
        settingOptionsLevel--;
        return setOptions;
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
                mcpuFlag = null;
                try {
                    IOption option = (IOption)(getOption(optionId)[1]);
                    mcpuFlag = option.getEnumCommand(option.getSelectedEnum());
                } catch (BuildException e) {
                    e.printStackTrace();
                }
                if (mcpuFlag != null) {
                    readTargetOptions();
                    setEnabled(disabledForCpu, true);
                    disabledForCpu = new ArrayList<>();

                    List<String> setOptions = setOptionsFromProperties(
                            ArcCpu.fromCommand(mcpuFlag).getOptionsToSet());

                    /*
                     * For each -mcpu value there is a standard library that uses some options. We
                     * set these options in IDE, but if user unchecks one of the checkboxes, he
                     * might think that options he unselected are not used, but it would not be true
                     * because they would still be used by standard library. As for dropdown lists,
                     * we should prevent user from choosing weaker options than are used in standard
                     * library for the same reason, but he can choose stronger ones, so we don't
                     * disable dropdown lists.
                     */
                    for (String setOptionId : setOptions) {
                        if (setOptionId.equals(optionId)) {
                            continue;
                        }
                        IOption setOption = (IOption)(getOption(setOptionId)[1]);
                        try {
                            if (setOption.getValueType() == IOption.BOOLEAN) {
                                disabledForCpu.add(setOptionId);
                            }
                        } catch (BuildException e) {
                            e.printStackTrace();
                        }
                    }
                    setEnabled(disabledForCpu, false);
                }
            }
            if (optionId.contains("option.target.tcf")) {
                useTcf = (Boolean) mgr.getValue(optionId);
                if (useTcf) {
                    // First set option values, then enable/disable them because changing -mcpu value
                    // also enables/disables some options.
                    if (!tcfPath.isEmpty()) {
                        TcfContent tcfContent = null;
                        tcfContent = TcfContent.readFile(new File(tcfPath), mcpuFlag, StatusManager.SHOW);
                        if (tcfContent != null) {
                            Properties gccOptions = tcfContent.getGccOptions();
                            setOptionsFromProperties(gccOptions);
                        }
                    }

                    setEnabled(targetOptions, false);
                    for (String option : TCF_RELATED_OPTIONS) {
                        setEnabled(getToolChainSpecificOption(option), true);
                    }
                    if (tcfLinkSelected) {
                        for (String option : LINKER_SCRIPT_IDS) {
                            setEnabled(getToolChainSpecificOption(option), false);
                        }
                    }
                    // Else do nothing because if TCF is not selected, these options were enabled
                    // either by default or when TCF was cancelled.

                } else {
                    setEnabled(targetOptions, true);
                    setEnabled(disabledForCpu, false);
                    for (String option : TCF_RELATED_OPTIONS) {
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
                    TcfContent tcfContent = null;
                    // Don't show anything here because TcfValueHandler shows errors when
                    // TCF path value is changed
                    tcfContent = TcfContent.readFile(new File(tcfPath), mcpuFlag, StatusManager.NONE);
                    if (tcfContent != null) {
                        Properties gccOptions = tcfContent.getGccOptions();
                        setOptionsFromProperties(gccOptions);
                    }
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
