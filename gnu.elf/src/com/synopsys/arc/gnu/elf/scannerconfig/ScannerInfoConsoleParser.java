// SPDX-License-Identifier: EPL-2.0

package com.synopsys.arc.gnu.elf.scannerconfig;

import org.eclipse.cdt.managedbuilder.internal.scannerconfig.ManagedGCCScannerInfoConsoleParser;

/**
 * The only purpose of this plugin is to suppress warnings about usage of internal
 * {@link ManagedGCCScannerInfoConsoleParser} - using it in the plugin.xml would cause a warning we
 * can't suppress individually.
 */
@SuppressWarnings("restriction")
public final class ScannerInfoConsoleParser extends ManagedGCCScannerInfoConsoleParser
{
}
