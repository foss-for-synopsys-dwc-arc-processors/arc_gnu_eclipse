package com.synopsys.arc.gnu.elf;

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;

import com.synopsys.arc.gnu.elf.utility.BuildUtils;

public final class TargetValueHandler extends ManagedOptionValueHandler
    implements IManagedOptionValueHandler
{
    @Override
    public boolean handleValue(
        IBuildObject configuration,
        IHoldsOptions holder,
        IOption option,
        String extraArgument,
        int event)
    {
        // If this is an ARC target option then ensure that ELF will be rebuilt.
        if (event == EVENT_APPLY || event == EVENT_SETDEFAULT) {
            if (BuildUtils.isArcTargetOption(option)
                && holder instanceof IToolChain
                && option.getApplicabilityCalculator()
                    .isOptionEnabled(configuration, holder, option)) {
                ((IToolChain) holder).getParent().setRebuildState(true);
            }
        }
        return super.handleValue(configuration, holder, option, extraArgument, event);
    }

}
