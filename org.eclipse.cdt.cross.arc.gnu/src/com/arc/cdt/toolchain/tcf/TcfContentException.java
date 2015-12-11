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

public class TcfContentException extends Exception {

    private static final long serialVersionUID = -8130268668081957392L;

    public TcfContentException(String message) {
        super(message);
    }

    public TcfContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
