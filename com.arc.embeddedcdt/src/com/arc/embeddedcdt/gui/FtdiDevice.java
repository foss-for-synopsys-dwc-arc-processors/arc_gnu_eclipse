/*******************************************************************************
 * This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

public enum FtdiDevice {
    AXS101("AXS101", Arrays.asList(FtdiCore.ARC770D, FtdiCore.EM, FtdiCore.AS221_2,
            FtdiCore.AS221_1)),
    AXS102("AXS102", Arrays.asList(FtdiCore.HS36, FtdiCore.HS34)),
    AXS103("AXS103", Arrays.asList(FtdiCore.HS38_1, FtdiCore.HS38_0, FtdiCore.HS36)),
    EM_SK_v1x("EM Starter Kit v1.x"),
    EM_SK_v2x("EM Starter Kit v2.x"),
    CUSTOM("Custom configuration file");

    private final String text;
    private final List<FtdiCore> cores;

    private FtdiDevice(final String text) {
        this(text, Arrays.asList(FtdiCore.DEFAULT_CORE));
    }

    private FtdiDevice(final String text, final List<FtdiCore> cores) {
        if (cores.isEmpty())
            throw new IllegalArgumentException("Cores list must not be empty.");

        this.text = text;
        this.cores = cores;
    }

    @Override
    public String toString() {
        return text;
    }

    public static FtdiDevice fromString(final String string) {
        for (FtdiDevice device : FtdiDevice.values()) {
            if (device.toString().equals(string))
                return device;
        }
        throw new IllegalArgumentException("String does not correspond to any FTDI device.");
    }

    public List<FtdiCore> getCores() {
        return new LinkedList<FtdiCore>(cores);
    }
}
