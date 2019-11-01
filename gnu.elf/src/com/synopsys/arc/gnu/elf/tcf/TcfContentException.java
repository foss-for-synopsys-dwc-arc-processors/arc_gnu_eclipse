// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.tcf;

public class TcfContentException extends Exception
{
    private static final long serialVersionUID = -8130268668081957392L;

    public TcfContentException(String message)
    {
        super(message);
    }

    public TcfContentException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
