package test.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
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
public class LaunchOpenOCDAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public LaunchOpenOCDAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		LaunchOpenocd();
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
	protected void LaunchOpenocd() {
	    MessageConsole myConsole = findConsole("OpenOCD");
	    final MessageConsoleStream outopenocd = myConsole.newMessageStream();
	    Color red = new Color(null, 255, 0, 0);
	    outopenocd.setColor(red);
	    try {
	      /*String[] commandsfind = { "tasklist", "/nh", "/FI", "\"IMAGENAME", "eq", "openocd.exe\"" };
	      Runtime runtime = Runtime.getRuntime();
	      Process process = runtime.exec(commandsfind);
	      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
	      String line = null;
	      while ((line = br.readLine()) != null)
	        if (line.indexOf("openocd.exe") != -1) {
	          String[] commandskill = { "tskill", "openocd" };
	          process = runtime.exec(commandskill);
	        }
	      try
	      {
	        Thread.sleep(500L);
	      }
	      catch (InterruptedException e) {
	        e.printStackTrace();
	      }*/
	      //String[] commandstart = { "openocd.exe", " -f C:/ARC48/share/openocd/scripts/target/snps_starter_kit_arc-em.cfg -c init -c halt -c reset halt " };
	      String commandstart = "openocd.exe -f  C:\\ARC48\\share\\openocd\\scripts\\target\\snps_starter_kit_arc-em.cfg -c init -c halt  -c reset halt " ;
	      
	      //System.out.println("Output JTAG via OpenOCD: " + commandstart[0].toString() + commandstart[1].toString());
	      Process p = Runtime.getRuntime().exec(commandstart);
	      final BufferedReader br1 = new BufferedReader(new InputStreamReader(p.getInputStream())); 
	      final BufferedReader br2 = new BufferedReader(new InputStreamReader(p.getErrorStream())); 
	        new Thread(new Runnable() {  
	            public void run() {  
	                try {  
	                    while (br1.readLine() != null)  
	                    	{outopenocd.println("Output JTAG via OpenOCD: " + br1.readLine());  }  ;
	                    br1.close();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                } 
	               
	            }  
	        }).start();  
	      new Thread(new Runnable()
	      {
	        public void run()
	        {
	          try {
	            while (br2.readLine() != null)
	            {
	            	outopenocd.println("JTAG via OpenOCD: " + br2.readLine());
	            }br2.close();
	           } catch (IOException e) {
	            e.printStackTrace();
	          }
	        }
	      }).start();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
	private MessageConsole findConsole(String name)
	  {
	    ConsolePlugin plugin = ConsolePlugin.getDefault();
	    IConsoleManager conMan = plugin.getConsoleManager();
	    IConsole[] existing = conMan.getConsoles();
	    for (int i = 0; i < existing.length; i++) {
	      if (name.equals(existing[i].getName()))
	        return (MessageConsole)existing[i];
	    }
	    MessageConsole myConsole = new MessageConsole(name, null);
	    conMan.addConsoles(new IConsole[] { myConsole });
	    return myConsole;
	  }
}