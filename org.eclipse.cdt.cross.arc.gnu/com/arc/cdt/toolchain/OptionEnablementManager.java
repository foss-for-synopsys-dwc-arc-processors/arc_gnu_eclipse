/*******************************************************************************
 * Copyright (c) 2005-2012 Synopsys, Incorporated
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Synopsys, Inc - Initial implementation 
 *******************************************************************************/
package com.arc.cdt.toolchain;

import java.util.ArrayList;



public class OptionEnablementManager extends AbstractOptionEnablementManager {

    private static final String MAP_OPTION_ID = "com.arc.cdt.toolchain.linker.option.map";
    private static final String PREFIX = "com.arc.cdt.toolchain.linker.option";
    private static final String[] MAPFILE_OPTION_IDS = {
        PREFIX + ".globals",
        PREFIX + ".crossref",
        PREFIX + ".sections",
        PREFIX + ".unmangle",
        PREFIX + ".tables",
        PREFIX + ".symbols",
        PREFIX + ".functions",
        PREFIX + ".crossfunc",
        "com.arc.cdt.toolchain.crossref", // misnamed      
    };
    
    private static final String ANSI_MODE = "arc.compiler.options.ansi";
    private static final String[] NONANSI_OPTIONS = {
    	"arc.compiler.options.pcc",
    	"arc.compiler.options.ptrscompat",
    	"arc.compiler.options.ptrint",
    	"arc.compiler.options.char_is_rep"   	
    };
    
    public OptionEnablementManager(){
        addObserver(new Observer());
    }
    
    class Observer implements IObserver {
        Observer(){
            doMapOptions(OptionEnablementManager.this,MAP_OPTION_ID);
        }

        public void onOptionValueChanged (IOptionEnablementManager mgr, String optionId) {
            // If linker map requested or not requested, then enable/disable
            // related options.
            if (optionId.equals(MAP_OPTION_ID)){
                doMapOptions(mgr, optionId);     
            }
            else
            // If -pg, then turn corresponding linker option
            if (optionId.endsWith(".call_graph")){
                setOptionValue("arc.link.options.profiling",mgr.getValue(optionId));
            }
            else if (optionId.endsWith(".keepasm")){
                // -keepasm enabled -Hanno
                boolean v = mgr.getValue(optionId).equals(Boolean.TRUE);
                setEnabled("arc.compiler.options.anno",v) ;            
            }
            else if (optionId.equals(ANSI_MODE)){
            	boolean v = mgr.getValue(optionId).equals(Boolean.TRUE);
                for (String o: NONANSI_OPTIONS){
            		setEnabled(o,!v);
            	}
            }
            else {
            	boolean ansiPermitted = true;
            	for (String o: NONANSI_OPTIONS){
            		if (Boolean.TRUE.equals(mgr.getValue(o))){
            			ansiPermitted = false;
            			break;
            		}
            	}
            	setEnabled(ANSI_MODE,ansiPermitted);           	
            }
            
            // Get suffix of option and make sure all with same suffix
            // are set to same value.
            // For example "arc.asm.options.arc5core" and
            // "arc.compiler.options.arc5core" must match.
            // HACK: except for ".level". We don't want optimization level to be mistaken for
            // debug level!
            Object v = mgr.getValue(optionId);
            if (v instanceof String || v instanceof Boolean) {
                String suffix = getSuffixOf(optionId);
                //Make copy to avoid occasional ConcurrentModificationException
                for (String id : new ArrayList<String>(getOptionIds())) {
                    if (suffix.equals(getSuffixOf(id)) && !id.equals(optionId) && !suffix.equals("level")) {
                        setOptionValue(id, v);
                    }
                }
            }
        }



        /**
         * Enable/disable the linker map options appropriately.
         * @param mgr the enablement manager.
         * @param optionId the name of the linker map option.
         */
        private void doMapOptions (IOptionEnablementManager mgr, String optionId) {
            boolean v = Boolean.TRUE.equals(mgr.getValue(optionId));
            for (String id: MAPFILE_OPTION_IDS){
               setEnabled(id,v);
            }
        }

        public void onOptionEnablementChanged (IOptionEnablementManager mgr, String optionID) {
            // @todo Auto-generated method stub          
        }  
    }
    
    protected static String getSuffixOf (String id) {
        int lastDot = id.lastIndexOf('.');
        if (lastDot >= 0) {
            return id.substring(lastDot + 1);
        }
        return id;
    }
    
}
