package org.eclipse.cdt.cross.arc.gnu.uclibc;

import java.io.PrintStream;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.internal.core.FolderInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ResourceConfiguration;

public class ARCAdditionalToolsManagedOptionValueHandler extends ManagedOptionValueHandler
{
  public boolean handleValue(IBuildObject configuration, IHoldsOptions holder, IOption option, String extraArgument, int event)
  {
     if (event == 4) {
       System.out.println("should update window!");
       if ((configuration instanceof FolderInfo))
      {
         FolderInfo oFolderInfo = (FolderInfo)configuration;

         return true;
       }if (!(configuration instanceof ResourceConfiguration))
      {
         System.out.println("unexpected instanceof configuration " + configuration.getClass().getCanonicalName());
      }

    }

     return false;
  }
}

/* Location:           C:\Users\yunluz\Desktop\plugins\bin\
 * Qualified Name:     org.eclipse.cdt.cross.arm.gnu.ARMAdditionalToolsManagedOptionValueHandler
 * JD-Core Version:    0.6.2
 */
