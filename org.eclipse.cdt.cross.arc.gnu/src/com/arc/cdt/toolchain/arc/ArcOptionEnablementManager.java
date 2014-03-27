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

public class ArcOptionEnablementManager extends OptionEnablementManager {

       
   
    private static String ARCV2EM_DISABLED[] = {
    	         
            "org.eclipse.cdt.cross.arc.gnu.windows.option.target.ea",//Customized for ARC GNU ea   
            "org.eclipse.cdt.cross.arc.gnu.linux.option.target.ea",//Customized for ARC GNU ea  
            "org.eclipse.cdt.cross.arc.gnu.windows.option.target.ll64",//Customized for ARC GNU ea   
            "org.eclipse.cdt.cross.arc.gnu.linux.option.target.ll64",//Customized for ARC GNU ea  
            //"org.eclipse.cdt.cross.arc.gnu.linux.option.target.fpu",
    };
    
    private static String ARCV2EM_INVISIBLED[]= {
    	"org.eclipse.cdt.cross.arc.gnu.windows.option.target.fpuhs",
    	"org.eclipse.cdt.cross.arc.gnu.windows.option.target.mpyhs",
    	"org.eclipse.cdt.cross.arc.gnu.linux.option.target.mpyem",
    	"org.eclipse.cdt.cross.arc.gnu.linux.option.target.mpyhs",
    };
    private static String ARCV2HS_INVISIBLED[]= {
    	//"org.eclipse.cdt.cross.arc.gnu.windows.option.target.fpuem",
    	"org.eclipse.cdt.cross.arc.gnu.windows.option.target.mpyem",
    	//"org.eclipse.cdt.cross.arc.gnu.linux.option.target.fpuem",
    	"org.eclipse.cdt.cross.arc.gnu.linux.option.target.fpuhs",
    	
    };
    
    
    /**
     * The names of options that are disabled for ARCv2EM
     */
   private static Set<String> DISABLED_FOR_ARCV2EM = new HashSet<String>(Arrays.asList(ARCV2EM_DISABLED));
   
    /**
     * The names of options that are disabled for ARC700
     */
    private static String ARC7_DISABLED[] = {
    	    "org.eclipse.cdt.cross.arc.gnu.windows.option.target.codedensity", //Customized for ARC GNU windows_codedensity
    	    "org.eclipse.cdt.cross.arc.gnu.linux.option.target.codedensity", //Customized for ARC GNU linux_codedensity
            "org.eclipse.cdt.cross.arc.gnu.windows.option.target.swap",//Customized for ARC GNU windows_swap
            "org.eclipse.cdt.cross.arc.gnu.linux.option.target.swap",//Customized for ARC GNU linux_swap
           
    };
    private static String ARC6_DISABLED[] = {
	    "org.eclipse.cdt.cross.arc.gnu.windows.option.target.swap",//Customized for ARC GNU windows_swap
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.swap",//Customized for ARC GNU linux_swap
    };
    private static String ARCV2HS_DISABLED[] = {

        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.ea",//Customized for ARC GNU ea   
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.ea",//Customized for ARC GNU ea  
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.codedensity", //Customized for ARC GNU windows_codedensity
	    "org.eclipse.cdt.cross.arc.gnu.linux.option.target.codedensity", //Customized for ARC GNU linux_codedensity
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.swap",//Customized for ARC GNU windows_swap
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.swap",//Customized for ARC GNU linux_swap
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.barrelshifter",//
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.barrelshifter",//

        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.shiftassist",//
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.shiftassist",//
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.normalize",//
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.normalize",
        
	    "org.eclipse.cdt.cross.arc.gnu.windows.option.target.fpi",//
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.fpi",//
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.mno-dpfp-lrsr",//
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.mno-dpfp-lrsr",//
     
        
        
    };
    
    // default value on ARCV2HS
    private static String[]  ARCV2HS_DEFAULT = {

        // HS default value to be true
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.codedensity", //Customized for ARC GNU windows_codedensity
	    "org.eclipse.cdt.cross.arc.gnu.linux.option.target.codedensity", //Customized for ARC GNU linux_codedensity
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.swap",//Customized for ARC GNU windows_swap
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.swap",//Customized for ARC GNU linux_swap
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.barrelshifter",//
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.barrelshifter",//
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.shiftassist",//
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.shiftassist",//
        "org.eclipse.cdt.cross.arc.gnu.windows.option.target.normalize",//
        "org.eclipse.cdt.cross.arc.gnu.linux.option.target.normalize",
    };
    
    private static Set<String> DISABLED_FOR_ARC7 = new HashSet<String>(Arrays.asList(ARC7_DISABLED));
    private static Set<String> DISABLED_FOR_ARC6 = new HashSet<String>(Arrays.asList(ARC6_DISABLED));
    private static Set<String> DISABLED_FOR_ARCV2HS = new HashSet<String>(Arrays.asList(ARCV2HS_DISABLED));
    private static Set<String> DEFAULT_FOR_ARCV2HS = new HashSet<String>(Arrays.asList(ARCV2HS_DEFAULT));
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
            public void onOptionVisiblementChanged (IOptionEnablementManager mgr, String optionId) {
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
            Set<String> defaultSet = null; 
            // `contains()` because sometimes this options has numeric suffix in the end.
            if (optionId.contains(".option.target.processor")) { 
                String value = (String) mgr.getValue(optionId);
                //System.out.println("com.arc.cdt.toolchain.arc.ArcOptionEnablementManager~~~~~~~~~~~~~~~~~~~~~"+value);
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
                    for (int i=0;i<ARCV2EM_DISABLED.length;i++) 
                    	setEnabled(ARCV2EM_DISABLED[i],false);
                    for (int i=0;i<ARCV2HS_DEFAULT.length;i++) {
                   	    setOptionValue(ARCV2HS_DEFAULT[i],false);
                    }
                    
                    for (int i=0;i<ARCV2EM_INVISIBLED.length;i++) 
                    	setEnabled(ARCV2EM_INVISIBLED[i],false);
                    for (int i=0;i<ARCV2HS_INVISIBLED.length;i++) 
                    	setEnabled(ARCV2HS_INVISIBLED[i],true);
                    
                    
                    
                    processor_control++;

                }
                 else if (value.endsWith("option.mcpu.arcv2hs")){
                     disabledSet = DISABLED_FOR_ARCV2HS;
                     defaultSet =  DEFAULT_FOR_ARCV2HS;
                     for (int i=0;i<ARCV2HS_DISABLED.length;i++) {
                    	 setEnabled(ARCV2HS_DISABLED[i],false);
                    	
                     }
                     for (int i=0;i<ARCV2HS_DEFAULT.length;i++) {
                    	 setOptionValue(ARCV2HS_DEFAULT[i],true);
                     }
                     for (int i=0;i<ARCV2EM_INVISIBLED.length;i++) 
                     	setEnabled(ARCV2EM_INVISIBLED[i],true);
                     for (int i=0;i<ARCV2HS_INVISIBLED.length;i++) 
                     	setEnabled(ARCV2HS_INVISIBLED[i],false);
                     
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
 
        public void onOptionEnablementChanged (IOptionEnablementManager mgr, String optionID) {
            // Nothing to do.

        }
    }
}
