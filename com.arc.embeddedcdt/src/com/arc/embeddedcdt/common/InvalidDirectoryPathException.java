/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.common;

public class InvalidDirectoryPathException extends Exception {

    public InvalidDirectoryPathException(String message) {
        super(message);
    }

    public InvalidDirectoryPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
