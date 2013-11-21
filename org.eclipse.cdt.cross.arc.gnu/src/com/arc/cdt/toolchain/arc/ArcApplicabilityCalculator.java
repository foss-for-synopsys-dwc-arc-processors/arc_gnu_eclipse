/*******************************************************************************
 * Copyright (c) 2005-2012 Synopsys, Incorporated
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Synopsys, Inc - Initial implementation 
 * Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/
package com.arc.cdt.toolchain.arc;

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionApplicability;



public class ArcApplicabilityCalculator implements IOptionApplicability {

	public ArcApplicabilityCalculator(){}
	// There is one instance of this class per option. But we want
    // to share the same enablement manager. So make it static.
    private static final ArcOptionEnablementManager EMGR = new ArcOptionEnablementManager();
    private  static IBuildObject lastConfig;
    
    public boolean isOptionUsedInCommandLine (IBuildObject configuration, IHoldsOptions holder, IOption option) {
        return isOptionEnabled(configuration,holder,option);       
    }

    public boolean isOptionVisible (IBuildObject configuration, IHoldsOptions holder, IOption option) {
        return true;
    }

    public boolean isOptionEnabled (IBuildObject configuration, IHoldsOptions holder, IOption option) {
        // Since there are no listeners on option changes,
        // we must resort to reading the states of all options!!!
        if (configuration != lastConfig) {
            lastConfig = configuration;
            EMGR.initialize(configuration);
       }
        if (option.getBaseId().endsWith(".option.target.processor")) { 
        	   String value = (String) EMGR.getValue(option.getBaseId());
        	   System.out.println("toolchain.arc.ArcApplicabilitycalculator~~~~~~~~~~~~~~~~~~~~~~~~~~~"+value);
      
        }
       return EMGR.isEnabled(option.getBaseId());
    }
    
}
