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


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.arc.cdt.toolchain.AbstractOptionEnablementManager;
import com.arc.cdt.toolchain.IOptionEnablementManager;
import com.arc.cdt.toolchain.OptionEnablementManager;


/**
 * Handles ARCompact options. Determines which is enabled or disabled when things are set. Also ties similar compiler,
 * assembler, and linker options together.
 * @author davidp
 * @currentOwner <a href="mailto:davidp@arc.com">davidp</a>
 * @version $Revision$
 * @lastModified $Date$
 * @lastModifiedBy $Author$
 * @reviewed 0 $Revision:1$
 */

public class ArcOptionEnablementManager extends OptionEnablementManager {

       
   
    private static String ARCV2EM_DISABLED[] = {
    	         
            "org.eclipse.cdt.cross.arc.gnu.windows.option.target.ea",//yunlu add for ea   
            "org.eclipse.cdt.cross.arc.gnu.linux.option.target.ea",//yunlu add for ea  
    };
    
    
    /**
     * The names of options that are disabled for ARCv2EM
     */
   private static Set<String> DISABLED_FOR_ARCV2EM = new HashSet<String>(Arrays.asList(ARCV2EM_DISABLED));
   
    /**
     * The names of options that are disabled for ARC700
     */
    private static String ARC7_DISABLED[] = {
    	    "org.eclipse.cdt.cross.arc.gnu.windows.option.target.codedensity", //yunlu add for windows_codedensity
    	    "org.eclipse.cdt.cross.arc.gnu.linux.option.target.codedensity", //yunlu add for linux_codedensity
            "org.eclipse.cdt.cross.arc.gnu.windows.option.target.swap",//yunlu add for windows_swap
            "org.eclipse.cdt.cross.arc.gnu.linux.option.target.swap",//yunlu add for linux_swap
    };
    private static String ARC6_DISABLED[] = {
	    "org.eclipse.cdt.cross.arc.gnu.windows.option.target.swap",//yunlu add for windows_swap
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.swap",//yunlu add for linux_swap
    };
    private static String ARCV2HS_DISABLED[] = {
	    "org.eclipse.cdt.cross.arc.gnu.windows.option.target.spfp",//yunlu add for windows_swap
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.spfp",//yunlu add for linux_swap
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.ea",//yunlu add for ea   
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.ea",//yunlu add for ea  
    };
    
    private static Set<String> DISABLED_FOR_ARC7 = new HashSet<String>(Arrays.asList(ARC7_DISABLED));
    private static Set<String> DISABLED_FOR_ARC6 = new HashSet<String>(Arrays.asList(ARC6_DISABLED));
    private static Set<String> DISABLED_FOR_ARCV2HS = new HashSet<String>(Arrays.asList(ARCV2HS_DISABLED));
    private static Set<String> ALL_TARGET_DEPENDENT = new HashSet<String>();
    static {

        ALL_TARGET_DEPENDENT.addAll(DISABLED_FOR_ARC7);
        ALL_TARGET_DEPENDENT.addAll(DISABLED_FOR_ARC6);
        ALL_TARGET_DEPENDENT.addAll(DISABLED_FOR_ARCV2EM);
        ALL_TARGET_DEPENDENT.addAll(DISABLED_FOR_ARCV2HS);
    }
    
     
    
    public ArcOptionEnablementManager() {
        addObserver(new Observer());
        
        AbstractOptionEnablementManager generalOptionManager = com.arc.cdt.toolchain.ApplicabilityCalculator.getOptionEnablementManager();
        generalOptionManager.addObserver(new IObserver(){

            public void onOptionValueChanged (IOptionEnablementManager mgr, String optionId) {
            }

            public void onOptionEnablementChanged (IOptionEnablementManager mgr, String optionID) {
                // TODO Auto-generated method stub               
            }});
    }
  
    class Observer implements IOptionEnablementManager.IObserver {

        /**
         * Called when an option value changes. Enable or disable any options that are dependent on this one.
         * @param mgr
         * @param optionId
         */
        int processor_control =0;
        public void onOptionValueChanged (IOptionEnablementManager mgr, String optionId) {
        	
            Set<String> disabledSet = null;
                  
            if (optionId.endsWith(".option.target.processor")) { 
                String value = (String) mgr.getValue(optionId);
                System.out.println("com.arc.cdt.toolchain.arc.ArcOptionEnablementManager~~~~~~~~~~~~~~~~~~~~~"+value);
                 if (value.endsWith("option.mcpu.arc700")) {
                    disabledSet = DISABLED_FOR_ARC7;
                    for (int i=0;i<ARC7_DISABLED.length;i++) 	setEnabled(ARC7_DISABLED[i],false);
                    for (int i=0;i<ARC6_DISABLED.length;i++)    	setEnabled(ARC6_DISABLED[i],false);
                 }
               else if (value.endsWith("option.mcpu.arc600")) {
                    disabledSet = DISABLED_FOR_ARC6;
                    for (int i=0;i<ARC7_DISABLED.length;i++)     setEnabled(ARC7_DISABLED[i],false);
                    for (int i=0;i<ARCV2EM_DISABLED.length;i++)  setEnabled(ARCV2EM_DISABLED[i],false);
                    processor_control++;
      
                 }
                 else if (value.endsWith("option.mcpu.arcv2em")){
                    disabledSet = DISABLED_FOR_ARCV2EM;
                    for (int i=0;i<ARCV2EM_DISABLED.length;i++)  setEnabled(ARCV2EM_DISABLED[i],false);
                    for (int i=0;i<ARC6_DISABLED.length;i++)     setEnabled(ARC6_DISABLED[i],false);
                    processor_control++;

                }
                 else if (value.endsWith("option.mcpu.arcv2hs")){
                     disabledSet = DISABLED_FOR_ARCV2HS;
                     for (int i=0;i<ARCV2HS_DISABLED.length;i++)  setEnabled(ARCV2HS_DISABLED[i],false);
                     processor_control++;

                 }
            }
           
            // TN: this calls only when target changing 
			if (disabledSet != null) {
				// Turn on any option not in the set.
				for (String id : ALL_TARGET_DEPENDENT) {
					if (!disabledSet.contains(id)) {
						setEnabled(id, true);
					}
				}
			

				// Now disable all options in the set.

				for (String id : disabledSet) {
					setEnabled(id, false);
				}

				// TN: move checkMPY after disabledSet running
			
			}
        }
 
        private boolean handleHirachicalOptsSelection (String srcOpt, String [] destOpts, boolean allowSetEnable){
        	 Boolean v = (Boolean)getValue(srcOpt);
        	 if (v != null) {
                 for (String s: destOpts) {
                     if (v) 
                    	 setOptionValue(s,true);
                     if(allowSetEnable)
                         setEnabled(s,!v);
                 }
                 return v; // could be true or false - true mean srcOpt is selected
             }
        	
             return false;
        }
       public void onOptionEnablementChanged (IOptionEnablementManager mgr, String optionID) {
            // Nothing to do.

        }
    }
}
