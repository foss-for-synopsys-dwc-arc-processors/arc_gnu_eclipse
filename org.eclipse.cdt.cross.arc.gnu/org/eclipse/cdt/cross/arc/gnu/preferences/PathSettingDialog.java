/*     */ package de.innot.avreclipse.ui.preferences;
/*     */ 
/*     */ import de.innot.avreclipse.core.paths.AVRPathManager;
/*     */ import de.innot.avreclipse.core.paths.AVRPathManager.SourceType;
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.core.runtime.IPath;
/*     */ import org.eclipse.core.runtime.IStatus;
/*     */ import org.eclipse.core.runtime.Status;
/*     */ import org.eclipse.jface.dialogs.StatusDialog;
/*     */ import org.eclipse.swt.custom.BusyIndicator;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.eclipse.ui.part.PageBook;
/*     */ 
/*     */ public class PathSettingDialog extends StatusDialog
/*     */ {
/*  48 */   private Combo fTypeCombo = null;
/*  49 */   private Combo fBundleCombo = null;
/*  50 */   private Text fPathText = null;
/*  51 */   private Button fFolderButton = null;
/*     */ 
/*  53 */   private PageBook fPageBook = null;
/*  54 */   private Composite fSystemPage = null;
/*  55 */   private Composite fBundlePage = null;
/*  56 */   private Composite fCustomPage = null;
/*     */ 
/*  59 */   private AVRPathManager fPathManager = null;
/*     */ 
/*     */   public PathSettingDialog(Shell parent, AVRPathManager pathmanager)
/*     */   {
/*  73 */     super(parent);
/*     */ 
/*  76 */     this.fPathManager = new AVRPathManager(pathmanager);
/*     */ 
/*  78 */     setTitle("Change Path for " + pathmanager.getName());
/*     */ 
/*  81 */     setShellStyle(getShellStyle() | 0x10);
/*     */   }
/*     */ 
/*     */   protected Control createDialogArea(Composite parent)
/*     */   {
/*  91 */     Composite composite = (Composite)super.createDialogArea(parent);
/*     */ 
/*  94 */     String[] types = { AVRPathManager.SourceType.System.toString(), 
/*  95 */       AVRPathManager.SourceType.Bundled.toString(), 
/*  96 */       AVRPathManager.SourceType.Custom.toString() };
/*     */ 
/*  99 */     getShell().setMinimumSize(400, 220);
/*     */ 
/* 102 */     Label description = new Label(composite, 0);
/* 103 */     description.setText(this.fPathManager.getDescription());
/*     */ 
/* 106 */     Composite top = new Composite(composite, 0);
/* 107 */     GridLayout layout = new GridLayout();
/* 108 */     layout.marginHeight = 0;
/* 109 */     layout.marginWidth = 0;
/* 110 */     layout.numColumns = 2;
/* 111 */     top.setLayout(layout);
/*     */ 
/* 114 */     Label typelabel = new Label(top, 0);
/* 115 */     typelabel.setText("Path source:");
/* 116 */     this.fTypeCombo = new Combo(top, 12);
/* 117 */     this.fTypeCombo.setItems(types);
/* 118 */     this.fTypeCombo.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 120 */         String value = PathSettingDialog.this.fTypeCombo.getText();
/* 121 */         PathSettingDialog.this.changeSourceType(value);
/*     */       }
/*     */     });
/* 126 */     Label separator = new Label(composite, 258);
/* 127 */     separator.setLayoutData(new GridData(4, 0, true, false));
/*     */ 
/* 131 */     this.fPageBook = new PageBook(composite, 0);
/* 132 */     this.fPageBook.setLayoutData(new GridData(4, 4, true, true));
/*     */ 
/* 134 */     this.fSystemPage = addSystemPage(this.fPageBook);
/* 135 */     this.fBundlePage = addBundlePage(this.fPageBook);
/* 136 */     this.fCustomPage = addCustomPage(this.fPageBook);
/*     */ 
/* 139 */     String currenttype = this.fPathManager.getSourceType().toString();
/* 140 */     this.fTypeCombo.select(this.fTypeCombo.indexOf(currenttype));
/* 141 */     changeSourceType(currenttype);
/*     */ 
/* 143 */     return composite;
/*     */   }
/*     */ 
/*     */   private Composite addSystemPage(Composite parent)
/*     */   {
/* 157 */     Composite page = new Composite(parent, 0);
/* 158 */     GridLayout layout = new GridLayout();
/* 159 */     layout.marginHeight = 0;
/* 160 */     layout.marginWidth = 0;
/* 161 */     layout.numColumns = 3;
/* 162 */     page.setLayout(layout);
/*     */ 
/* 164 */     Label label = new Label(page, 0);
/* 165 */     label.setText("System value:");
/*     */ 
/* 167 */     Text text = new Text(page, 2056);
/* 168 */     text.setLayoutData(new GridData(768));
/*     */ 
/* 170 */     text.setText(this.fPathManager.getSystemPath(false).toOSString());
/*     */ 
/* 172 */     Button rescanbutton = new Button(page, 0);
/* 173 */     rescanbutton.setText("Rescan");
/* 174 */     rescanbutton.addSelectionListener(new SelectionAdapter(text)
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/* 183 */         BusyIndicator.showWhile(PathSettingDialog.this.fPageBook.getDisplay(), new Runnable() {
/*     */           public void run() {
/* 185 */             PathSettingDialog.this.fPathManager.getSystemPath(true);
/*     */           }
/*     */         });
/* 189 */         this.val$text.setText(PathSettingDialog.this.fPathManager.getSystemPath(false).toOSString());
/*     */       }
/*     */     });
/* 193 */     return page;
/*     */   }
/*     */ 
/*     */   private Composite addBundlePage(Composite parent)
/*     */   {
/* 206 */     Composite page = new Composite(parent, 0);
/* 207 */     GridLayout layout = new GridLayout();
/* 208 */     layout.marginHeight = 0;
/* 209 */     layout.marginWidth = 0;
/* 210 */     layout.numColumns = 2;
/* 211 */     page.setLayout(layout);
/*     */ 
/* 213 */     Label label = new Label(page, 0);
/* 214 */     label.setText("Select AVR-GCC Bundle");
/*     */ 
/* 217 */     this.fBundleCombo = new Combo(page, 12);
/* 218 */     this.fBundleCombo.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 220 */         String value = PathSettingDialog.this.fTypeCombo.getText();
/* 221 */         System.out.println(value);
/*     */       }
/*     */     });
/* 226 */     return page;
/*     */   }
/*     */ 
/*     */   private Composite addCustomPage(Composite parent)
/*     */   {
/* 237 */     Composite page = new Composite(parent, 0);
/* 238 */     GridLayout layout = new GridLayout();
/* 239 */     layout.marginHeight = 0;
/* 240 */     layout.marginWidth = 0;
/* 241 */     layout.numColumns = 3;
/* 242 */     page.setLayout(layout);
/*     */ 
/* 244 */     Label label = new Label(page, 0);
/* 245 */     label.setText("Custom value:");
/*     */ 
/* 248 */     this.fPathText = new Text(page, 2052);
/* 249 */     this.fPathText.setText(this.fPathManager.getPath().toOSString());
/* 250 */     this.fPathText.setLayoutData(new GridData(768));
/* 251 */     this.fPathText.addListener(24, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 254 */         String newpath = PathSettingDialog.this.fPathText.getText();
/* 255 */         PathSettingDialog.this.fPathManager.setPath(newpath, AVRPathManager.SourceType.Custom);
/* 256 */         PathSettingDialog.this.testStatus();
/*     */       }
/*     */     });
/* 260 */     this.fFolderButton = new Button(page, 0);
/* 261 */     this.fFolderButton.setText("Browse...");
/*     */ 
/* 264 */     this.fFolderButton.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 267 */         String newpath = PathSettingDialog.this.getDirectory(PathSettingDialog.this.fPathText.getText());
/* 268 */         if (newpath != null) {
/* 269 */           PathSettingDialog.this.fPathText.setText(newpath);
/* 270 */           PathSettingDialog.this.fPathManager.setPath(newpath, AVRPathManager.SourceType.Custom);
/*     */         }
/* 272 */         PathSettingDialog.this.testStatus();
/*     */       }
/*     */     });
/* 276 */     return page;
/*     */   }
/*     */ 
/*     */   public AVRPathManager getResult()
/*     */   {
/* 293 */     return this.fPathManager;
/*     */   }
/*     */ 
/*     */   private void changeSourceType(String type)
/*     */   {
/* 309 */     if (type.equals(AVRPathManager.SourceType.System.toString()))
/*     */     {
/* 311 */       this.fPageBook.showPage(this.fSystemPage);
/* 312 */       this.fPathManager.setPath(this.fPathManager.getSystemPath(false).toOSString(), AVRPathManager.SourceType.System);
/*     */     }
/* 314 */     if (type.equals(AVRPathManager.SourceType.Bundled.toString()))
/*     */     {
/* 316 */       this.fPageBook.showPage(this.fBundlePage);
/*     */ 
/* 318 */       updateStatus(
/* 319 */         new Status(4, "de.innot.avreclipse.core", 
/* 319 */         "Bundled toolchains not yet supported"));
/* 320 */       return;
/*     */     }
/*     */ 
/* 323 */     if (type.equals(AVRPathManager.SourceType.Custom.toString()))
/*     */     {
/* 325 */       this.fPageBook.showPage(this.fCustomPage);
/* 326 */       this.fPathManager.setPath(this.fPathText.getText(), AVRPathManager.SourceType.Custom);
/*     */     }
/*     */ 
/* 330 */     testStatus();
/*     */   }
/*     */ 
/*     */   private void testStatus()
/*     */   {
/* 344 */     IStatus status = Status.OK_STATUS;
/*     */ 
/* 346 */     boolean empty = "".equals(this.fPathManager.getPath().toString());
/* 347 */     boolean valid = this.fPathManager.isValid();
/*     */ 
/* 349 */     if ((empty) && (valid))
/*     */     {
/* 351 */       status = new Status(2, "de.innot.avreclipse.core", "Optional path is empty");
/*     */     }
/* 353 */     if (!valid)
/*     */     {
/* 355 */       status = new Status(4, "de.innot.avreclipse.core", "Path is invalid");
/*     */     }
/*     */ 
/* 358 */     super.updateStatus(status);
/*     */   }
/*     */ 
/*     */   private String getDirectory(String startingDirectory)
/*     */   {
/* 371 */     DirectoryDialog fileDialog = new DirectoryDialog(getShell(), 4096);
/* 372 */     if (startingDirectory != null) {
/* 373 */       fileDialog.setFilterPath(startingDirectory);
/*     */     }
/* 375 */     String dir = fileDialog.open();
/* 376 */     if (dir != null) {
/* 377 */       dir = dir.trim();
/* 378 */       if (dir.length() > 0) {
/* 379 */         return dir;
/*     */       }
/*     */     }
/*     */ 
/* 383 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\yunluz\Documents\importfile\ARC_GNU_Plugin relevant\other company\avreclipse.2.4.0.final.p2repository\plugins\de.innot.avreclipse.ui_2.4.0.201203041437\
 * Qualified Name:     de.innot.avreclipse.ui.preferences.PathSettingDialog
 * JD-Core Version:    0.6.0
 */