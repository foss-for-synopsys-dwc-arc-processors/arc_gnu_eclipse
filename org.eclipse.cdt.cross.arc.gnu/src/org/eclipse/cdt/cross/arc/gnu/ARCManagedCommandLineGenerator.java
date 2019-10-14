/*******************************************************************************
* This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*     Synopsys, Inc. - ARC GNU Toolchain support
*******************************************************************************/

package org.eclipse.cdt.cross.arc.gnu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.ui.statushandlers.StatusManager;

import com.arc.cdt.toolchain.ArcCpu;
import com.arc.cdt.toolchain.ArcCpuFamily;
import com.arc.cdt.toolchain.tcf.TcfContent;

public class ARCManagedCommandLineGenerator extends ManagedCommandLineGenerator {
    public static final String USE_TCF_OPTION = ".target.use_tcf";
    public static final String TCF_PATH_OPTION = ".target.tcf_path";
    public static final String TCF_MAP_OPTION = ".target.tcf_map";
    public static final String TCF_CINCLUDE_OPTION = ".target.tcf_cinclude";
    public static final String CPU_OPTION = ".target.cpu";

    private static final String FPUEM_OPTION = ".target.fpuem";
    private static final String FPUHS_OPTION = ".target.fpuhs";
    private static final String FPX_OPTION = ".target.fpx";
    private static final String MPYEM_OPTION = ".target.mpyem";
    private static final String MPYHS_OPTION = ".target.mpyhs";
    private static final String MPY600_OPTION = ".target.mpy600";
    private static final String DIVREM_OPTION = ".target.divrem";
    private static final String CODEDENSITY_OPTION = ".target.codedensity";
    private static final String BARRELSHIFTER_OPTION = ".target.barrelshifter";
    private static final String NORMALIZE_OPTION = ".target.norm";
    private static final String SWAP_OPTION = ".target.swap";
    private static final String LL64_OPTION = ".target.ll64";
    private static final String ATOMIC_OPTION = ".target.atomic";
    private static final String EA_OPTION = ".target.ea";
    private static final String XY_OPTION = ".target.xy";
    private static final String NO_DPFP_LRSR_OPTION = ".target.dpfplrsr";

    private static ITool lastTool;
    private static String lastProject;

    private static final String MMPY_OPTION_EM = "com.synopsys.arc.gnu.elf.toolchain.base.target.mpyem";
    private static final String MMPY_OPTION_HS = "com.synopsys.arc.gnu.elf.toolchain.base.target.mpyhs";
    private static final String MMPY_OPTION_FOR_REPLACEMENT = "-mmpy-option=2";

    public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName,
            String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName,
            String[] asInputResources, String sCommandLinePattern) {
        return generateCommandLineInfo(oTool, sCommandName, asFlags, sOutputFlag, sOutputPrefix,
                sOutputName, asInputResources, sCommandLinePattern, false);
    }

     public IManagedCommandLineInfo generateCommandLineInfo(ITool oTool, String sCommandName, String[] asFlags, String sOutputFlag, String sOutputPrefix, String sOutputName, String[] asInputResources, String sCommandLinePattern, boolean bFlag)
     {
        ArrayList<String> tcf_properties = new ArrayList<String>();
        ArrayList<String> oList = new ArrayList<String>();

        ArrayList<String> oList_gcc_options = new ArrayList<String>();
        Object oParent = oTool.getParent();
        while ((oParent != null) && (!(oParent instanceof IToolChain)))
        {
           Object oSuper = oTool.getSuperClass();
           if ((oSuper != null) && ((oSuper instanceof ITool)))
                oParent = ((ITool)oSuper).getParent();
           else {
                oParent = null;
           }
        }
   
       if ((oParent != null) && ((oParent instanceof IToolChain))) {
       IToolChain oToolChain = (IToolChain)oParent;
       IBuildObject configuration = oToolChain.getParent();
   
       IOption[] aoOptions = oToolChain.getOptions();
   
       String sProcessor = null;
                
       String sCore700 = null;        //Customized for ARC GNU core 700
       String sBarrelshifter = null;  //Customized for ARC GNU Barrelshifter
       String sCodedensity = null;    //Customized for ARC GNU codedensity
       String sDivide = null;         //Customized for ARC GNU divide
       String sNormalize = null;      //Customized for ARC GNU normalize
       String sSwap = null;           //Customized for ARC GNU swap
       String sEa = null;           //Customized for ARC GNU ea
   
       String sSyntaxonly = null;
   
       String sFPUEM = null;
       String sFPUHS = null;
       String smpyhs= null;
       String smpyem= null;
       String smpy600 = null;
   
       String sDebugLevel = null;
   
       String sDebugFormat = null;
   
       String sDebugOther = null;
   
       String sDebugProf = null;
   
       String sDebugGProf = null;
       String satomic = null;
       String sll64 = null;
       String smfpi= null;
       String smno_dpfp_lrsr= null;
       String smxy= null;

       String sTCF= null;
       Boolean tcf_selected= (Boolean) null;
       Boolean tcf_map_selected= (Boolean) null;
       Boolean tcf_include_c_defines= (Boolean) null;

       String projectBuildPath = getProjectBuildPath(oToolChain);
       String tcfMapPath = projectBuildPath + File.separator + "memory.x";

       for (int i = 0; i < aoOptions.length; i++)
       {
         IOption oOption = aoOptions[i];
         String sID = oOption.getId();

         // Should be checking if option is used in command line, but this filter is not
         // really correct right now in custom ARC implementation, so using visibility.
         var applicability = oOption.getApplicabilityCalculator();
         if (applicability != null
             && !applicability.isOptionVisible(configuration, oTool, oOption) ) {
             continue;
         }
   
         Object oValue = oOption.getValue();
   
         String sCommand = oOption.getCommand();
         if ((oValue instanceof String)) {
             String sVal;
             try {
             sVal = oOption.getStringValue();
             }
             catch (BuildException e)
             {
               // yunluz comment String sVal;
             sVal = null;
             }
             String sEnumCommand;
             try
             {
             sEnumCommand = oOption.getEnumCommand(sVal);
             }
             catch (BuildException e1)
             {
               //yunluz String sEnumCommand;
             sEnumCommand = null;
             }

           if (sID.indexOf(CPU_OPTION) > 0) {
               sProcessor = sEnumCommand;
           } else if (sID.indexOf(".option.target.core700") > 0) { //Customized for ARC GNU core 700
               sCore700 = sEnumCommand;
           } else if (sID.indexOf(".option.warnings.syntax") > 0) {
               sSyntaxonly = sEnumCommand;
           } else if (sID.indexOf(FPUEM_OPTION) > 0) {
               sFPUEM = sEnumCommand;
           } else if (sID.indexOf(FPUHS_OPTION) > 0) {
               sFPUHS = sEnumCommand;
           } else if (sID.indexOf(MPYHS_OPTION) > 0) {
               smpyhs = sEnumCommand;
           } else if (sID.indexOf(MPYEM_OPTION) > 0) {
               smpyem = sEnumCommand;
           } else if (sID.indexOf(MPY600_OPTION) > 0) {
               smpy600 = sEnumCommand;
           } else if (sID.indexOf(FPX_OPTION) > 0) {
               smfpi = sEnumCommand;  //Customized for ARC GNU
           } else if (sID.indexOf(".option.debugging.level") > 0) {
               sDebugLevel = sEnumCommand;
           } else if (sID.indexOf(".option.debugging.format") > 0) {
               sDebugFormat = sEnumCommand;
           } else if (sID.indexOf(".option.debugging.other") > 0) {
               sDebugOther = sVal;
           } else if (sID.indexOf(TCF_PATH_OPTION) > 0) {
               sTCF = sVal;
           }
         }
           else if ((oValue instanceof Boolean)) {
             boolean bVal;
             try {
             bVal = oOption.getBooleanValue();
             }
             catch (BuildException e)
             {
               //yunluz boolean bVal;
             bVal = false;
             }

             if (bVal) {
                if (sID.indexOf(".option.debugging.prof") > 0) {
                    sDebugProf = sCommand;
                } else if (sID.indexOf(BARRELSHIFTER_OPTION) > 0) {
                    sBarrelshifter = sCommand; // Customized for ARC GNU barrelshifter
                } else if (sID.indexOf(CODEDENSITY_OPTION) > 0) {
                    sCodedensity = sCommand; // Customized for ARC GNU codedensity
                } else if (sID.indexOf(DIVREM_OPTION) > 0) {
                    sDivide = sCommand; // Customized for ARC GNU divide
                } else if (sID.indexOf(NORMALIZE_OPTION) > 0) {
                    sNormalize = sCommand; // Customized for ARC GNU normalize
                } else if (sID.indexOf(SWAP_OPTION) > 0) {
                    sSwap = sCommand; // Customized for ARC GNU swap
                } else if (sID.indexOf(EA_OPTION) > 0) {
                    sEa = sCommand; // Customized for ARC GNU ea
                } else if (sID.indexOf(XY_OPTION) > 0) {
                    smxy = sCommand;
                } else if (sID.indexOf(ATOMIC_OPTION) > 0) {
                    satomic = sCommand;
                } else if (sID.indexOf(LL64_OPTION) > 0) {
                    sll64 = sCommand;
                } else if (sID.indexOf(NO_DPFP_LRSR_OPTION) > 0) {
                    smno_dpfp_lrsr = sCommand;
                } else if (sID.indexOf(".option.debugging.gprof") > 0) {
                    sDebugGProf = sCommand;
                } else if (sID.indexOf(USE_TCF_OPTION) > 0) {
                    tcf_selected = true;
                } else if (sID.indexOf(TCF_MAP_OPTION) > 0) {
                    tcf_map_selected = true;
                } else if (sID.indexOf(TCF_CINCLUDE_OPTION) > 0) {
                    tcf_include_c_defines = true;
                }
             }
        }
       }

            if (sProcessor != null && !sProcessor.isEmpty())
                oList_gcc_options.add(sProcessor);
            if (sCore700 != null && !sCore700.isEmpty())
                oList.add(sCore700);
            if (sSyntaxonly != null && !sSyntaxonly.isEmpty()) {
                oList.add(sSyntaxonly);
            }
            if (sFPUEM != null && !sFPUEM.isEmpty())
                oList_gcc_options.add(sFPUEM);
            if (sFPUHS != null && !sFPUHS.isEmpty())
                oList_gcc_options.add(sFPUHS);
            if (smpyhs != null && !smpyhs.isEmpty())
                oList_gcc_options.add(smpyhs);
            if (smpyem != null && !smpyem.isEmpty())
                oList_gcc_options.add(smpyem);
            if (smpy600 != null && !smpy600.isEmpty())
                oList_gcc_options.add(smpy600);
            if (sDebugLevel != null && !sDebugLevel.isEmpty()) {
                oList.add(sDebugLevel);
                if (sDebugFormat != null && !sDebugFormat.isEmpty())
                    oList.add(sDebugFormat);
            }
            if (sDebugOther != null && !sDebugOther.isEmpty())
                oList.add(sDebugOther);
            if (sDebugProf != null && !sDebugProf.isEmpty())
                oList.add(sDebugProf);
            if (sDebugGProf != null && !sDebugGProf.isEmpty()) {
                oList.add(sDebugGProf);
            }
            if (sBarrelshifter != null && !sBarrelshifter.isEmpty()) {
                oList_gcc_options.add(sBarrelshifter); // Customized for ARC GNU barrelshifter
            }
            if (sCodedensity != null && !sCodedensity.isEmpty()) {
                oList_gcc_options.add(sCodedensity); // Customized for ARC GNU codedensity
            }
            if (sDivide != null && !sDivide.isEmpty()) {
                oList_gcc_options.add(sDivide); // Customized for ARC GNU divide
            }
            if (sNormalize != null && !sNormalize.isEmpty()) {
                oList_gcc_options.add(sNormalize); // Customized for ARC GNU normalize
            }
            if (sSwap != null && !sSwap.isEmpty()) {
                oList_gcc_options.add(sSwap); // Customized for ARC GNU swap
            }
            if (smfpi != null && !smfpi.isEmpty()) {
                oList_gcc_options.add(smfpi);
            }
            if (smno_dpfp_lrsr != null && !smno_dpfp_lrsr.isEmpty()) {
                oList_gcc_options.add(smno_dpfp_lrsr);
            }

            if (sEa != null && !sEa.isEmpty()) {
                oList_gcc_options.add(sEa);
            }
            if (smxy != null && !smxy.isEmpty()) {
                oList_gcc_options.add(smxy);
            }
            if (satomic != null && !satomic.isEmpty()) {
                oList_gcc_options.add(satomic);
            }
            if (sll64 != null && !sll64.isEmpty()) {
                oList_gcc_options.add(sll64);
            }
            if (sTCF != null && !sTCF.isEmpty()) {
                oList_gcc_options.addAll(tcf_properties);
            }
            if (sProcessor != null) {
                if (ArcCpu.fromCommand(sProcessor).getToolChain().equals(ArcCpuFamily.ARC700)) {
                    if (oList_gcc_options.indexOf(sNormalize) < 0) {
                        oList_gcc_options.add("-mno-norm");
                    } else {
                        int i = oList_gcc_options.indexOf(sNormalize);
                        oList_gcc_options.remove(i);
                    }
                    if (oList_gcc_options.indexOf(sBarrelshifter) < 0) {
                        oList_gcc_options.add("-mno-barrel-shifter");
                    } else {
                        int i = oList_gcc_options.indexOf(sBarrelshifter);
                        oList_gcc_options.remove(i);
                    }
                }
            }

            if ((tcf_selected != null) && tcf_selected && sTCF != null) {
                oList_gcc_options.clear();
                TcfContent fileContent = null;
                /*
                 * There are two ways we can handle exceptions occurring while reading TCF: we can
                 * handle them manually or throw them further. Since this method is called both when
                 * building a project and when determining what options to show in tool tab in IDE,
                 * we should choose the way which is good enough for both situations.
                 * 
                 * If we threw the exceptions further, the building of the project would be aborted,
                 * which is better than just ignoring TCF, as it is done if the other way is chosen.
                 * But in this case there would be a problem with IDE. If a user closed project
                 * properties window while on some tool tab and then tried to open these properties
                 * again, the page wouldn't be able to render correctly. For example, it is possible
                 * that the user wouldn't be able to change path to TCF, because the button might be
                 * missing from the page.
                 * 
                 * It is not obvious how to fix the page in that case, so we decided to show error
                 * dialogs manually so that the properties page would be displayed correctly.
                 */
                int showStyle = (oTool.equals(lastTool) && projectBuildPath.equals(lastProject))
                        ? StatusManager.NONE : StatusManager.SHOW;
                File tcf = new File(ARCPlugin.safeVariableExpansion(sTCF));
                fileContent = TcfContent.readFile(tcf, sProcessor, showStyle, "\n\nIgnoring TCF.");
                if (fileContent != null) {
                    Properties gccOptions = fileContent.getGccOptions();
                    Enumeration<?> e = gccOptions.propertyNames();
                    tcf_properties.clear();
                    while (e.hasMoreElements()) {
                        String gcc_option = (String) e.nextElement();
                        String value = gccOptions.getProperty(gcc_option);
                        if (!value.isEmpty()) {
                            gcc_option = gcc_option + "=" + value;
                            if (gcc_option.contains("-mmpy-option")) {
                                try {
                                    gcc_option = getMultiplyOption(gcc_option, oToolChain, sProcessor);
                                } catch (BuildException e1) {
                                    StatusManager.getManager().handle(
                                            new Status(IStatus.ERROR, ARCPlugin.PLUGIN_ID, e1.getMessage()),
                                            StatusManager.LOG);
                                }
                            }
                        }
                        tcf_properties.add(gcc_option);
                    }

                    if (oTool.getBaseId().contains("linker")) {
                        if (tcf_map_selected != null && tcf_map_selected) {
                            // Don't write file if build directory doesn't exist yet.
                            if (Files.exists(Paths.get(projectBuildPath))) {
                                String memoryMap = fileContent.getLinkerMemoryMap();
                                try {
                                    Files.deleteIfExists(Paths.get(tcfMapPath));
                                    Files.write(Paths.get(tcfMapPath), memoryMap.getBytes(),
                                            StandardOpenOption.CREATE);
                                } catch (IOException e1) {
                                    StatusManager.getManager().handle(new Status(IStatus.ERROR,
                                            ARCPlugin.PLUGIN_ID, e1.getMessage()),
                                            StatusManager.SHOW);
                                    e1.printStackTrace();
                                }
                            }
                            oList_gcc_options.add("-Wl,-marcv2elfx -L " + projectBuildPath);
                        }
                    }

                    // Apply this option to compiler and asm invocations. The way how this code
                    // identifies if tools is gcc or gas is very bad, though.
                    if (oTool.getBaseId().contains("compiler") || oTool.getBaseId().contains("assembler")
                        || oTool.getBaseId().contains("gcc") || oTool.getBaseId().contains("gxx")
                        || oTool.getBaseId().contains("asm")) {
                        if (tcf_include_c_defines != null && tcf_include_c_defines) {
                        	String filePath = projectBuildPath + File.separator + fileContent.getCDefinesFileName();
                            try {
                            	if (Files.exists(Paths.get(projectBuildPath)))
                            	{
	                                Files.write(Paths.get(filePath), fileContent.getCDefinesText().getBytes(),
	                                        StandardOpenOption.CREATE);
                            	}
                            } catch (IOException e1) {
                                StatusManager.getManager().handle(
                                        new Status(IStatus.ERROR, ARCPlugin.PLUGIN_ID, e1.getMessage()),
                                        StatusManager.SHOW);
                                e1.printStackTrace();
                            }
                            if (Files.exists(Paths.get(projectBuildPath))) {
                                oList_gcc_options.add("-include " + filePath);
                            }
                        }
                    }
                }
                oList_gcc_options.addAll(tcf_properties);
            }
            oList.addAll(Arrays.asList(asFlags));
            lastProject = projectBuildPath;
        }
        oList.addAll(0, oList_gcc_options);
        lastTool = oTool;
        return super.generateCommandLineInfo(oTool, sCommandName,
                (String[]) oList.toArray(new String[0]), sOutputFlag, sOutputPrefix, sOutputName,
                asInputResources, sCommandLinePattern);
     }

    private String getProjectBuildPath(IToolChain toolChain) {
        // Contains eclipse path variable, needs resolving
        String projectBuildPath = toolChain.getBuilder().getBuildPath();
        try {
            projectBuildPath = VariablesPlugin.getDefault().getStringVariableManager()
                    .performStringSubstitution(projectBuildPath);
        } catch (CoreException e) {
            StatusManager.getManager()
                    .handle(new Status(IStatus.WARNING, ARCPlugin.PLUGIN_ID,
                            "Can not save memory map from TCF in project directory, using temp"),
                    StatusManager.SHOW);
            projectBuildPath = System.getProperty("java.io.tmpdir");
        }
        return projectBuildPath;
    }

    private String getMultiplyOption(String gccOption, IToolChain toolChain,
            String cpuFlag) throws BuildException {
        String superClassId = ArcCpu.fromCommand(cpuFlag).getToolChain().equals(ArcCpuFamily.ARCEM)
                ? MMPY_OPTION_EM : MMPY_OPTION_HS;
        IOption mmpyOption = toolChain.getOptionBySuperClassId(superClassId);

        boolean isApplicable = false;
        for (String appValue : mmpyOption.getApplicableValues()) {
            if (gccOption.equals(mmpyOption.getEnumCommand(appValue))) {
                isApplicable = true;
                break;
            }
        }
        if (!isApplicable) {
            return MMPY_OPTION_FOR_REPLACEMENT;
        }
        return gccOption;
    }
}

