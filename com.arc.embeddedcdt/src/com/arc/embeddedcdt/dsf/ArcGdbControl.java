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

import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_7;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

// This class is needed for replacing default final launch sequence with ours
public class ArcGdbControl extends GDBControl_7_7 {

    public ArcGdbControl(DsfSession session, ILaunchConfiguration config, CommandFactory factory) {
        super(session, config, factory);
    }

    @Override
    protected Sequence getCompleteInitializationSequence(
                    Map<String, Object> attributes, RequestMonitorWithProgress rm) {
            return new ArcFinalLaunchSequence(getSession(), attributes, rm);
    }

}
