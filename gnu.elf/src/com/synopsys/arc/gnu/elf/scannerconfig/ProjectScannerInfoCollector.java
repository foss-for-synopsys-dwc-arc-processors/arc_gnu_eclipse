// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.scannerconfig;

import org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector;

/**
 * The only purpose of this plugin is to suppress warnings about usage of internal
 * {@link PerProjectSICollector} - using it in the plugin.xml would cause a warning we can't
 * suppress individually.
 */
@SuppressWarnings("restriction")
public final class ProjectScannerInfoCollector extends PerProjectSICollector
{
}
