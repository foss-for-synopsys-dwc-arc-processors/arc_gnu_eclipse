/*******************************************************************************
 * This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.common;

public enum FtdiCore {
    ARC770D("ARC770D"),
    EM6("EM6"),
    AS221_1("AS221 #1"),
    AS221_2("AS221 #2"),
    HS34("HS34"),
    HS36("HS36"),
    HS38_0("HS38 #0"),
    HS38_1("HS38 #1"),
    DEFAULT_CORE("Default core");

    private final String text;

    private FtdiCore(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static FtdiCore fromString(final String string) {
        for (FtdiCore core : FtdiCore.values()) {
            if (core.toString().equals(string))
                return core;
        }
        throw new IllegalArgumentException("String does not correspond to any FTDI device core.");
    }
}
