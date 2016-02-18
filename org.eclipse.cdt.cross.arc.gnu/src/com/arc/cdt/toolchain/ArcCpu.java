/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.cdt.toolchain;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public enum ArcCpu {
    ARCEM("arcem"),
    EM("em"),
    EM4("em4"),
    EM4_DMIPS("em4_dmips"),
    EM4_FPUS("em4_fpus"),
    EM4_FPUDA("em4_fpuda"),
    ARCHS("archs"),
    HS("hs"),
    HS34("hs34"),
    HS38("hs38"),
    HS38_LINUX("hs38_linux"),
    ARC600("arc600"),
    ARC700("arc700");

    private String string;
    private static Map<ArcCpu, Properties> SET_OPTIONS = new HashMap<>();
    static {
        for (ArcCpu cpu: ArcCpu.values()) {
            SET_OPTIONS.put(cpu, new Properties());
        }
        try {
            SET_OPTIONS.get(ARCEM).load(new StringReader(
                    "-mcpu=arcem\n -mcode-density\n -mmpy-option=2\n -mbarrel-shifter\n"));
            SET_OPTIONS.get(EM).load(new StringReader("-mcpu=em\n -mmpy-option=0\n"));
            SET_OPTIONS.get(EM4)
                    .load(new StringReader("-mcpu=em4\n -mcode-density\n -mmpy-option=0\n"));
            SET_OPTIONS.get(EM4_DMIPS).load(new StringReader(
                    "-mcpu=em4_dmips\n -mcode-density\n -mmpy-option=2\n -mbarrel-shifter\n -mdiv-rem\n -mnorm\n"));
            SET_OPTIONS.get(EM4_FPUS).load(new StringReader(
                    "-mcpu=em4_fpus\n -mcode-density\n -mmpy-option=2\n -mbarrel-shifter\n -mdiv-rem\n -mnorm\n -mfpu=fpus\n"));
            SET_OPTIONS.get(EM4_FPUDA).load(new StringReader(
                    "-mcpu=em4_fpuda\n -mcode-density\n -mmpy-option=2\n -mbarrel-shifter\n -mdiv-rem\n -mnorm\n -mfpu=fpuda\n"));
            SET_OPTIONS.get(ARCHS).load(new StringReader(
                    "-mcpu=archs\n -mdiv-rem\n -mmpy-option=2\n -mll64\n -matomic\n"));
            SET_OPTIONS.get(HS).load(new StringReader("-mcpu=hs\n -mmpy-option=0\n"));
            SET_OPTIONS.get(HS34)
                    .load(new StringReader("-mcpu=hs34\n -mmpy-option=2\n -matomic\n"));
            SET_OPTIONS.get(HS38).load(new StringReader(
                    "-mcpu=hs38\n -mdiv-rem\n -mmpy-option=9\n -mll64\n -matomic\n"));
            SET_OPTIONS.get(HS38_LINUX).load(new StringReader(
                    "-mcpu=hs38_linux\n -mdiv-rem\n -mmpy-option=9\n -mll64\n -matomic\n -mfpu=fpud_all\n"));
        } catch (IOException e) {
        }
    }

    private ArcCpu(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public static ArcCpu fromCommand(final String command) {
        for (ArcCpu cpu : ArcCpu.values()) {
            if (command.endsWith(cpu.toString()))
                return cpu;
        }
        throw new IllegalArgumentException("String does not correspond to any ARC CPU.");
    }

    public Properties getOptionsToSet() {
        return SET_OPTIONS.get(this);
    }

}
