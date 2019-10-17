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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.cdt.cross.arc.gnu.ARCManagedCommandLineGenerator;
import org.eclipse.cdt.cross.arc.gnu.ARCPlugin;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.cdt.toolchain.ArcCpu;
import com.arc.cdt.toolchain.IOptionEnablementManager;
import com.arc.cdt.toolchain.OptionEnablementManager;
import com.arc.cdt.toolchain.tcf.TcfContent;

public class ArcOptionEnablementManager extends OptionEnablementManager {

    public static final String TCF_OPTION_ID = "use_tcf";
    public static final String TCF_FILE_OPTION_ID = "tcf_path";
    public static final String TCF_MEMORY_MAP = "tcf_map";
    public static final String TCF_INCLUDE_C_DEFINES = "tcf_cinclude";

    private static final String[] TCF_RELATED_OPTIONS = { getTCF(TCF_FILE_OPTION_ID), 
            getTCF(TCF_MEMORY_MAP), getTCF(TCF_INCLUDE_C_DEFINES) };
    private static final String[] NOT_ARCHITECTURE_OPTIONS = { getTCF(TCF_FILE_OPTION_ID),  getTCF(TCF_MEMORY_MAP),
            getTCF(TCF_OPTION_ID),  getTCF(TCF_INCLUDE_C_DEFINES) };

    private static final String[] LINKER_SCRIPT_IDS = { "org.eclipse.cdt.cross.arc.gnu.c.link.option.scriptfile",
            "org.eclipse.cdt.cross.arc.gnu.base.option.linker.memoryx"  };

    private List<String> targetOptions;
    private List<String> disabledForCpu = new ArrayList<>();

    private Map<String, List<String>> inapplicableEnumValues = new HashMap<>();
    private Map<String, String> cpuSpecificEnumValues = new HashMap<>();

    private int settingOptionsLevel = 0;
    private Observer observer;


    public ArcOptionEnablementManager() {
        observer = new Observer();
        addObserver(observer);
    }

    private static String getTCF(String string) {
        String target = "com.synopsys.arc.gnu.elf.toolchain.base.target.";
        return target + string;
    }

    private boolean useTcf;
    private boolean tcfLinkSelected;
    private String tcfPath = "";
    private String mcpuFlag = null;

    @Override
    public void initialize(IBuildObject config) {
        super.initialize(config);
        List<String> tcfOption = getToolChainSpecificOption(getTCF(TCF_FILE_OPTION_ID));
        if (tcfOption != null && tcfOption.size() > 0) {
            observer.onOptionValueChanged(this, tcfOption.get(0));
        }
    }

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
     * setOptionValue() from AbstractOptionEnablementManager class calls option.setValue() method,
     * which rewrites default value of the option. This method just sets option value in GUI without
     * rewriting defaults.
     */
    private void setOptionValue(IOption option, Object value) {
        // Do not rewrite configuration when initializing. Otherwise some values from configuration
        // we initialize from might be lost.
        if (initializing) {
            return;
        }
        if (value instanceof Boolean) {
            /* Need to use ManagedBuildManager here instead of just option.setValue()
             * because option.setValue() method rewrites default value of this option
             * with what is set. But ManagedBuildManager does nothing with the default
             * value and just changes build configuration for the project.*/
            ManagedBuildManager.setOption(getConfig(), getToolChain(), option,
                    ((Boolean) value).booleanValue());
        } else if (value instanceof String) {
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
        disabledForCpu.clear();
        inapplicableEnumValues.clear();
        cpuSpecificEnumValues.clear();

        for (String targetOptionId : targetOptions) {
            IOption targetOption = (IOption) getOption(targetOptionId)[1];
            Object defaultValue = getDefaultValue(targetOption);
            set(targetOptionId, defaultValue);
        }
    }

    private void computeInapplicableAndDisabledOptionsForCpu(List<String> options) {
        /*
         * For each -mcpu value there is a standard library that uses some options. We
         * set these options in IDE, but if user unchecks one of the checkboxes, he
         * might think that options he unselected are not used, but it would not be true
         * because they would still be used by standard library. As for dropdown lists,
         * we should prevent user from choosing weaker options than are used in standard
         * library for the same reason, but he can choose stronger ones, so we don't
         * disable dropdown lists.
         */
        for (String setOptionId : options) {
            if (setOptionId.contains(ARCManagedCommandLineGenerator.CPU_OPTION)) {
                continue;
            }
            IOption setOption = (IOption)(getOption(setOptionId)[1]);
            try {
                if (setOption.getValueType() == IOption.BOOLEAN) {
                    disabledForCpu.add(setOptionId);
                } else if (setOption.getValueType() == IOption.ENUMERATED) {
                    inapplicableEnumValues.put(setOptionId, new ArrayList<String>());
                    String selectedValueId = getValue(setOptionId).toString();
                    cpuSpecificEnumValues.put(setOptionId, selectedValueId);
                    for (String value : setOption.getApplicableValues()) {
                        String enumId = setOption.getEnumeratedId(value);
                        if (enumId.equals(selectedValueId)) {
                            break;
                        }
                        inapplicableEnumValues.get(setOptionId).add(enumId);
                    }
                }
            } catch (BuildException e) {
                e.printStackTrace();
            }
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
                setOptions.add(((IOption)toSet[0]).getBaseId());
                set(((IOption)toSet[0]).getBaseId(), toSet[1]);
            }
        }
        settingOptionsLevel--;
        if (settingOptionsLevel == 0) {
            for (String key : targetOptions) {
                setOptionValue((IOption) getOption(key)[1], getValue(key));
            }
        }
        return setOptions;
    }

    /**
     * Check if option's value is inapplicable due to current CPU value. If value is incorrect, show
     * error message or change incorrect value to the one that corresponds to CPU depending on
     * <code>rewrite</code> and <code>showStyle</code> values.
     * 
     * @param optionId
     *            id of option to check
     * @param rewrite
     *            if true, change inapplicable option's value to the one that corresponds to CPU
     * @param showStyle
     *            style indicating how error message should be shown if value is incorrect.
     *            Possible values are <code>StatusManager.NONE</code>,
     *            <code>StatusManager.LOG</code>, <code>StatusManager.SHOW</code> and
     *            <code>StatusManager.BLOCK</code>.
     */
    private void checkOptionIsCorrect(String optionId, boolean rewrite, int showStyle) {
        // Do not show errors while initializing: TCF might be selected
        if (initializing) return;
        Object[] optLookup = getOption(optionId);
        if (optLookup == null) {
            return;
        }
        IOption option = (IOption)optLookup[1];
        String postfix = rewrite ? " Setting option's value corresponding to the CPU"
                : " It is recommended that you change value of either " + option.getName()
                        + " or CPU.";
        boolean isCorrect = true;
        String optionValueName = "";
        String newValueName = "";
        Object newValue = null;
        try {
            if (disabledForCpu.contains(optionId)) {
                Boolean value = (Boolean)getValue(optionId);
                if (!value) {
                    optionValueName = value.toString();
                    newValue = true;
                    newValueName = "true";
                    isCorrect = false;
                }
            }
            if (inapplicableEnumValues.containsKey(optionId)) {
                String value = getValue(optionId).toString();
                if (inapplicableEnumValues.get(optionId).contains(value)) {
                    optionValueName = option.getEnumName(value);
                    newValue = cpuSpecificEnumValues.get(optionId);
                    newValueName = option.getEnumName(newValue.toString());
                    isCorrect = false;
                }
            }
        } catch (BuildException e) {
            e.printStackTrace();
        }
        if (!isCorrect) {
            if (rewrite) {
                postfix += ": \"" + newValueName + "\".";
                setOptionValue(option, newValue);
            }
            String errorMessage = "Combination of " + option.getName() + "'s value \""
                    + optionValueName + "\" and CPU value \"" + ArcCpu.fromCommand(mcpuFlag)
                    + "\" is not valid." + postfix;
            StatusManager.getManager().handle(
                    new Status(IStatus.ERROR, ARCPlugin.PLUGIN_ID, errorMessage),
                    showStyle);
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
            // Ensure that target options is not null.
            if (targetOptions == null) {
                readTargetOptions();
            }

            // `contains()` because sometimes this options has numeric suffix in the end.
            if (optionId.contains(ARCManagedCommandLineGenerator.CPU_OPTION)) {
                mcpuFlag = null;
                try {
                    Object[] optLookup = getOption(optionId);
                    if (optLookup != null) {
                        IOption option = (IOption)optLookup[1];
                        mcpuFlag = option.getEnumCommand(getValue(optionId).toString());
                    }
                } catch (BuildException e) {
                    e.printStackTrace();
                }
                if (mcpuFlag != null) {
                    readTargetOptions();
                    setEnabled(disabledForCpu, true);

                    List<String> setOptions = setOptionsFromProperties(
                            ArcCpu.fromCommand(mcpuFlag).getOptionsToSet());

                    computeInapplicableAndDisabledOptionsForCpu(setOptions);
                    setEnabled(disabledForCpu, false);
                }
            }
            /*
             * If some of option's values are inapplicable due to current -mcpu value, show error
             * message.
             */
            if (inapplicableEnumValues.containsKey(optionId) || disabledForCpu.contains(optionId)) {
                int showStyle = useTcf ? StatusManager.LOG : StatusManager.BLOCK;
                checkOptionIsCorrect(optionId, !useTcf, showStyle);
            }
            if (optionId.contains(ARCManagedCommandLineGenerator.USE_TCF_OPTION)) {
                useTcf = (Boolean) mgr.getValue(optionId);
                if (useTcf) {
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

                    tcfPath = (String)mgr.getValue(
                            getToolChainSpecificOption(getTCF(TCF_FILE_OPTION_ID)).get(0));
                    if (tcfPath != null && !tcfPath.isEmpty()) {
                        File tcf = new File(ARCPlugin.safeVariableExpansion(tcfPath));
                        TcfContent tcfContent = TcfContent.readFile(tcf, mcpuFlag, StatusManager.SHOW);
                        if (tcfContent != null) {
                            Properties gccOptions = tcfContent.getGccOptions();
                            setOptionsFromProperties(gccOptions);
                        }
                    }

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
                    for (String id : disabledForCpu) {
                        checkOptionIsCorrect(id, true, StatusManager.SHOW);
                    }
                    for (String id : inapplicableEnumValues.keySet()) {
                        checkOptionIsCorrect(id, true, StatusManager.SHOW);
                    }
                }
            }
            if (optionId.contains(ARCManagedCommandLineGenerator.TCF_PATH_OPTION)) {
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
            if (optionId.contains(ARCManagedCommandLineGenerator.TCF_MAP_OPTION)) {
                tcfLinkSelected = (Boolean) mgr.getValue(optionId);
                if (tcfLinkSelected && useTcf) {
                    for (String option : LINKER_SCRIPT_IDS) {
                        setEnabled(getToolChainSpecificOption(option), false);
                    }
                } else
                {
                    for (String option : LINKER_SCRIPT_IDS) {
                        setEnabled(getToolChainSpecificOption(option), true);
                    }
                }
            }
        }

        public void onOptionEnablementChanged(IOptionEnablementManager mgr, String optionID) {
            // Nothing to do.

        }
    }
}
