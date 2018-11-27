/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu.scannerconfig;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector3;
import org.eclipse.cdt.make.core.scannerconfig.InfoContext;
import org.eclipse.cdt.make.core.scannerconfig.ScannerInfoTypes;
import org.eclipse.cdt.make.internal.core.scannerconfig.util.CygpathTranslator;
import org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector;
import org.eclipse.cdt.managedbuilder.scannerconfig.IManagedScannerInfoCollector;
import org.eclipse.core.resources.IProject;

public class ARCGnuWinScannerInfoCollector extends PerProjectSICollector
implements IScannerInfoCollector3, IManagedScannerInfoCollector
{
    private IProject m_oProject;

    @Override
    public void contributeToScannerConfig(Object oResource,
            @SuppressWarnings("rawtypes") Map oScannerInfo)
    {
        List<String> oIncludes = (List<String>) oScannerInfo.get(ScannerInfoTypes.INCLUDE_PATHS);

        List<String> oTranslatedIncludes = CygpathTranslator.translateIncludePaths(this.m_oProject,
                oIncludes);
        Iterator<String> oPathIter = oTranslatedIncludes.listIterator();
        while (oPathIter.hasNext()) {
            String sConvertedPath = oPathIter.next();

            if (sConvertedPath.startsWith("/")) {
                oPathIter.remove();
            }

        }

        oScannerInfo.put(ScannerInfoTypes.INCLUDE_PATHS, oTranslatedIncludes);

        super.contributeToScannerConfig(oResource, oScannerInfo);
    }

    @Override
    public void setProject(IProject oProject) {
        this.m_oProject = oProject;
        super.setProject(oProject);
    }

    @Override
    public void setInfoContext(InfoContext oContext) {
        this.m_oProject = oContext.getProject();
        super.setInfoContext(oContext);
    }
}
