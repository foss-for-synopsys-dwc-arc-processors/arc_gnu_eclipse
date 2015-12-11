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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;

import com.arc.cdt.toolchain.IOptionEnablementManager;
import com.arc.cdt.toolchain.OptionEnablementManager;

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

    private boolean tcfValue;
    private boolean tcfLinkSelected;

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
                readTargetOptions();
            }
            if (optionId.contains("option.target.tcf")) {
                tcfValue = (Boolean) mgr.getValue(optionId);
                if (tcfValue) {
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
                    // either by default or when TCF was cancelled
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
            if (optionId.contains("option.target.maptcf")) {
                tcfLinkSelected = (Boolean) mgr.getValue(optionId);
                if (tcfLinkSelected && tcfValue) {
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
