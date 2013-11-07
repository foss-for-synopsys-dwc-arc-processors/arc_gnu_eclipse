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
public class LaunchAshlingopellaAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public LaunchAshlingopellaAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		launchashling();
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
	public  void launchashling()
	  {
			/*String[] commandsfind = { "tasklist", "/nh", "/FI", "\"IMAGENAME", "eq", "ash-arc-gdb-server.exe\"" };
		      Process process1 = null;
			try {
				process1 = Runtime.getRuntime().exec(commandsfind);
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
		      BufferedReader br = new BufferedReader(new InputStreamReader(process1.getInputStream()));
		      String line = null;
		      try {
				while ((line = br.readLine()) != null)
				    if (line.indexOf("ash-arc-gdb-server.exe") != -1) {
				      String[] commandskill = { "tskill", "ash-arc-gdb-server" };
				      process1 = Runtime.getRuntime().exec(commandskill);
				    }
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
		      try
		      {
		        Thread.sleep(500L);
		      }
		      catch (InterruptedException e) {
		        e.printStackTrace();
		      }*/
			 Runtime runtime = Runtime.getRuntime();
			 String current = null;
			try {
				current = new java.io.File( "." ).getCanonicalPath();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		     System.out.println("Current dir:"+current);
		     String currentDir = System.getProperty("user.dir");
		     String home = System.getProperty("user.home");
		     System.out.println("Home system property is: " + home);
		     System.setProperty("Ashling", "C:\\AshlingOpellaXDforARC");
		     String Ashling = System.getProperty("Ashling");
		     System.out.println("Ashling system property is: " + Ashling);
	    	 String commands = Ashling+"\\ash-arc-gdb-server.exe --jtag-frequency 8mhz --device arc --arc-reg-file "+Ashling+"\\arc-opella-em.xml" ;
	    	 Process process = null;
	    	 System.out.println("Ashling:"+commands);
	    	 java.io.File ashldir = new  java.io.File("C:\\AshlingOpellaXDforARC");
		    try {
		      process = runtime.exec(commands, null,ashldir);
		    }
		    catch (IOException e2) {
		      e2.printStackTrace();
		    }

		    final BufferedReader br1 = new BufferedReader(new InputStreamReader(process.getInputStream())); 
		    final BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getErrorStream())); 
		      new Thread(new Runnable() {  
		          public void run() {  
		              try {  
		                  while (br2.readLine() != null)  
		                  	{System.out.println("Output JTAG via Ashling: " + br2.readLine());  }  ;
		                  br2.close();  
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
		        	  System.out.println("JTAG via Ashling: " + br2.readLine());
		          }br2.close();
		         } catch (IOException e) {
		          e.printStackTrace();
		        }
		      }
		    }).start();
	  }
	  
}