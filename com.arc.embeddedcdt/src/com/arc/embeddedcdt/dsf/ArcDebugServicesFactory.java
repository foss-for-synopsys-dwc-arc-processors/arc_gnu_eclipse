/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf;

import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ArcDebugServicesFactory extends GdbDebugServicesFactory {

    public ArcDebugServicesFactory(String version) {
        super(version);
    }

    public <V> V createService(Class<V> clazz, DsfSession session,
            Object... optionalArguments) {
    if (GdbServerBackend.class.isAssignableFrom(clazz)) {
            for (Object arg : optionalArguments) {
                    if (arg instanceof ILaunchConfiguration) {
                            return (V) createGdbServerBackendService(session,
                                            (ILaunchConfiguration) arg);
                    }
            }
    }
    return super.createService(clazz, session, optionalArguments);
}

    protected GdbServerBackend createGdbServerBackendService(DsfSession session, ILaunchConfiguration arg) {
        return new GdbServerBackend(session, arg);
    }
}
