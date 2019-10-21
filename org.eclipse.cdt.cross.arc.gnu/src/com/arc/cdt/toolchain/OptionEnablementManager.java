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
package com.arc.cdt.toolchain;

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
        "arc.gnu.elf.option.target.ea",
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
                setOptionValue("com.synopsys.arc.gnu.elf.tool.c.linker.arc.prof",mgr.getValue(optionId));
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
