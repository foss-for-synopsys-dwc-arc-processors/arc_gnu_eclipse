package test.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;

import com.arc.embeddedcdt.launch.WinRegistry;
/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class LaunchPuTTYAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public LaunchPuTTYAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		LaunchPuTTY();
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	protected void LaunchPuTTY() {
		String comport=COMserialport().get(0).toString();
		String[] commandsstart = { "putty.exe", "-serial", comport, "-sercfg", "115200" };
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(commandsstart);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		/* String[] commandsfind = { "tasklist", "/nh", "/FI", "\"IMAGENAME", "eq", "putty.exe\"" };
	    

	    String[] commandsstart = { "putty.exe", "-serial", comport, "-sercfg", "115200" };
	    
	    try {
	      Process process = runtime.exec(commandsfind);
	      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
	      String line = null;
	      while ((line = br.readLine()) != null)
	        if (line.indexOf("putty.exe") != -1) {
	          String[] commandskill = { "tskill", "putty" };
	          process = runtime.exec(commandskill);
	          try {
	            Thread.sleep(500L);
	          }
	          catch (InterruptedException e) {
	            e.printStackTrace();
	          }
	          process = Runtime.getRuntime().exec(commandsstart);
	        } else {
	          if (line.indexOf("putty.exe") != -1)
	            continue;
	          process = Runtime.getRuntime().exec(commandsstart);
	        }
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	  }*/
	public static List COMserialport()
	{
		List<String> list = new ArrayList<String>(); Boolean status=true;
		try {
			int i=0; String regedit=null;
			while(status){
				
				if ((regedit=WinRegistry.readString (WinRegistry.HKEY_LOCAL_MACHINE, "HARDWARE\\DEVICEMAP\\SERIALCOMM", "\\Device\\VCP"+Integer.toString(i)))!=null)
					{
					//list.add(i, regedit);
					System.out.println("please connect to EM Starter Kit specify port manually or by using default "+Integer.toString(i+1)+"; COM Port = " + regedit);
					//list.add("EM Starter Kit "+Integer.toString(i+1)+"; COM Port = " + regedit);
					list.add(regedit);//com4
					i++;
					}
				else 
					{
					status= false;
					break;
					}
					
			}
	
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(list.size()<1) {list.add("Please connect to EM Starter Kit");}
		return list;
	}
}