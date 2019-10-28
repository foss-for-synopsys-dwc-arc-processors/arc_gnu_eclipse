/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.cdt.toolchain.tcf;

import java.io.File;

import org.eclipse.cdt.cross.arc.gnu.ARCManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.ui.statushandlers.StatusManager;

import com.synopsys.arc.gnu.elf.ArcGnuElfPlugin;

public class TcfValueHandler implements IManagedOptionValueHandler {

    @Override
    public boolean handleValue(IBuildObject configuration, IHoldsOptions holder, IOption option,
            String extraArgument, int event) {
        if (event == IManagedOptionValueHandler.EVENT_APPLY &&
                option.getApplicabilityCalculator().isOptionEnabled(configuration, holder, option)) {
            IOption[] options = holder.getOptions();
            String cpu = null;
            for (IOption o : options) {
                if (o.getBaseId().contains(ARCManagedCommandLineGenerator.CPU_OPTION)) {
                    try {
                        cpu = o.getEnumCommand(o.getSelectedEnum());
                    } catch (BuildException e) {
                        e.printStackTrace();
                    }
                }
            }
            File tcf = new File(ArcGnuElfPlugin.safeVariableExpansion((String) option.getValue()));
            TcfContent.readFile(tcf, cpu, StatusManager.BLOCK);
            return true;
        }
        return false;
    }

    @Override
    public boolean isDefaultValue(IBuildObject configuration, IHoldsOptions holder, IOption option,
            String extraArgument) {
        return false;
    }

    @Override
    public boolean isEnumValueAppropriate(IBuildObject configuration, IHoldsOptions holder,
            IOption option, String extraArgument, String enumValue) {
        return false;
    }

}
