/*     */ package de.innot.avreclipse.ui.preferences;
/*     */ 
/*     */ import de.innot.avreclipse.AVRPlugin;
/*     */ import de.innot.avreclipse.core.avrdude.AVRDudeException;
/*     */ import de.innot.avreclipse.core.avrdude.ProgrammerConfig;
/*     */ import de.innot.avreclipse.core.avrdude.ProgrammerConfigManager;
/*     */ import de.innot.avreclipse.core.targets.IProgrammer;
/*     */ import de.innot.avreclipse.core.toolinfo.AVRDude;
/*     */ import de.innot.avreclipse.core.toolinfo.AVRDude.ConfigEntry;
/*     */ import de.innot.avreclipse.ui.dialogs.AVRDudeErrorDialog;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.core.runtime.IPath;
/*     */ import org.eclipse.core.runtime.IProgressMonitor;
/*     */ import org.eclipse.core.runtime.IStatus;
/*     */ import org.eclipse.core.runtime.Status;
/*     */ import org.eclipse.core.runtime.jobs.Job;
/*     */ import org.eclipse.jface.dialogs.StatusDialog;
/*     */ import org.eclipse.swt.custom.SashForm;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.events.VerifyEvent;
/*     */ import org.eclipse.swt.events.VerifyListener;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ 
/*     */ public class AVRDudeConfigEditor extends StatusDialog
/*     */ {
/*     */   private final ProgrammerConfig fConfig;
/*     */   private Map<String, IProgrammer> fConfigIDMap;
/*     */   private Map<String, IProgrammer> fConfigNameMap;
/*     */   private final Set<String> fAllConfigs;
/*     */   private Text fPreviewText;
/*     */ 
/*     */   public AVRDudeConfigEditor(Shell parent, ProgrammerConfig config, Set<String> allconfigs)
/*     */   {
/* 111 */     super(parent);
/*     */ 
/* 113 */     setTitle("Edit AVRDude Programmer Configuration " + config.getName());
/*     */ 
/* 116 */     setShellStyle(getShellStyle() | 0x10);
/*     */ 
/* 119 */     this.fConfig = ProgrammerConfigManager.getDefault().getConfigEditable(config);
/*     */ 
/* 122 */     this.fAllConfigs = allconfigs;
/* 123 */     if (this.fAllConfigs.contains(this.fConfig.getName())) {
/* 124 */       this.fAllConfigs.remove(this.fConfig.getName());
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 133 */       Collection programmers = AVRDude.getDefault().getProgrammersList();
/* 134 */       this.fConfigIDMap = new HashMap(programmers.size());
/* 135 */       this.fConfigNameMap = new HashMap(programmers.size());
/* 136 */       for (IProgrammer type : programmers) {
/* 137 */         this.fConfigIDMap.put(type.getId(), type);
/* 138 */         this.fConfigNameMap.put(type.getDescription(), type);
/*     */       }
/*     */     } catch (AVRDudeException e) {
/* 141 */       AVRDudeErrorDialog.openAVRDudeError(getShell(), e, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Control createDialogArea(Composite parent)
/*     */   {
/* 155 */     Composite composite = (Composite)super.createDialogArea(parent);
/* 156 */     composite.setLayout(new GridLayout(3, false));
/*     */ 
/* 158 */     addNameControl(composite);
/*     */ 
/* 160 */     addDescriptionControl(composite);
/*     */ 
/* 162 */     addProgrammersComposite(composite);
/*     */ 
/* 164 */     addPortControl(composite);
/*     */ 
/* 166 */     addBaudrateControl(composite);
/*     */ 
/* 168 */     addExitspecComposite(composite);
/*     */ 
/* 170 */     addPostAVRDudeDelayControl(composite);
/*     */ 
/* 172 */     addCommandlinePreview(composite);
/*     */ 
/* 174 */     updateCommandPreview();
/*     */ 
/* 176 */     return composite;
/*     */   }
/*     */ 
/*     */   private void addNameControl(Composite parent)
/*     */   {
/* 193 */     Label label = new Label(parent, 0);
/* 194 */     label.setText("Configuration name");
/* 195 */     Text name = new Text(parent, 2048);
/* 196 */     name.setText(this.fConfig.getName());
/* 197 */     name.setLayoutData(new GridData(4, 0, true, false, 2, 1));
/*     */ 
/* 201 */     name.addModifyListener(new ModifyListener(name)
/*     */     {
/*     */       public void modifyText(ModifyEvent e)
/*     */       {
/* 212 */         String newname = this.val$name.getText();
/* 213 */         AVRDudeConfigEditor.this.fConfig.setName(newname);
/* 214 */         if (newname.length() == 0) {
/* 215 */           Status status = new Status(4, "AVRDude", 
/* 216 */             "Configuration name may not be empty", null);
/* 217 */           AVRDudeConfigEditor.this.updateStatus(status);
/* 218 */         } else if (AVRDudeConfigEditor.this.fAllConfigs.contains(newname)) {
/* 219 */           Status status = new Status(4, "AVRDude", 
/* 220 */             "Configuration with the same name already exists", null);
/* 221 */           AVRDudeConfigEditor.this.updateStatus(status);
/*     */         } else {
/* 223 */           AVRDudeConfigEditor.this.updateStatus(Status.OK_STATUS);
/*     */         }
/*     */       }
/*     */     });
/* 229 */     name.addVerifyListener(new VerifyListener()
/*     */     {
/*     */       public void verifyText(VerifyEvent event)
/*     */       {
/* 236 */         String text = event.text;
/* 237 */         if (text.indexOf('/') != -1)
/* 238 */           event.doit = false;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void addDescriptionControl(Composite parent)
/*     */   {
/* 253 */     Label label = new Label(parent, 0);
/* 254 */     label.setText("Description");
/* 255 */     Text description = new Text(parent, 2048);
/* 256 */     description.setText(this.fConfig.getDescription());
/* 257 */     description.setLayoutData(new GridData(4, 0, true, false, 2, 1));
/* 258 */     description.addModifyListener(new ModifyListener(description)
/*     */     {
/*     */       public void modifyText(ModifyEvent e)
/*     */       {
/* 265 */         String newdescription = this.val$description.getText();
/* 266 */         AVRDudeConfigEditor.this.fConfig.setDescription(newdescription);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void addProgrammersComposite(Composite parent)
/*     */   {
/* 286 */     Group listgroup = new Group(parent, 16);
/* 287 */     listgroup.setText("Programmer Hardware (-c)");
/* 288 */     listgroup.setLayoutData(new GridData(4, 4, true, true, 3, 1));
/* 289 */     FillLayout fl = new FillLayout();
/* 290 */     fl.marginHeight = 5;
/* 291 */     fl.marginWidth = 5;
/* 292 */     listgroup.setLayout(fl);
/*     */ 
/* 294 */     SashForm sashform = new SashForm(listgroup, 256);
/* 295 */     sashform.setLayout(new GridLayout(2, false));
/*     */ 
/* 297 */     org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(sashform, 2816);
/* 298 */     list.setLayoutData(new GridData(4, 4, true, true));
/* 299 */     String[] allprogrammers = getProgrammers();
/* 300 */     list.setItems(allprogrammers);
/*     */ 
/* 302 */     Composite devicedetails = new Composite(sashform, 0);
/* 303 */     devicedetails.setLayout(new GridLayout());
/* 304 */     devicedetails.setLayoutData(new GridData(4, 4, false, false));
/*     */ 
/* 306 */     Text fromtext = new Text(devicedetails, 0);
/* 307 */     fromtext.setEditable(false);
/* 308 */     fromtext.setLayoutData(new GridData(4, 0, true, false));
/*     */ 
/* 310 */     Text details = new Text(devicedetails, 2050);
/* 311 */     details.setEditable(false);
/* 312 */     details.setLayoutData(new GridData(4, 4, true, true));
/*     */ 
/* 314 */     list.addSelectionListener(new SelectionAdapter(list, fromtext, details)
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/* 322 */         String devicename = this.val$list.getItem(this.val$list.getSelectionIndex());
/* 323 */         IProgrammer type = (IProgrammer)AVRDudeConfigEditor.this.fConfigNameMap.get(devicename);
/* 324 */         AVRDudeConfigEditor.this.fConfig.setProgrammer(type.getId());
/* 325 */         AVRDudeConfigEditor.this.updateDetails(type, this.val$fromtext, this.val$details);
/* 326 */         AVRDudeConfigEditor.this.updateCommandPreview();
/*     */       }
/*     */     });
/* 329 */     String programmer = this.fConfig.getProgrammer();
/* 330 */     IProgrammer type = (IProgrammer)this.fConfigIDMap.get(programmer);
/* 331 */     if (programmer.length() != 0) {
/* 332 */       list.select(list.indexOf(type.getDescription()));
/* 333 */       updateDetails(type, fromtext, details);
/*     */     }
/*     */ 
/* 336 */     sashform.pack();
/*     */   }
/*     */ 
/*     */   private void addPortControl(Composite parent)
/*     */   {
/* 345 */     Label label = new Label(parent, 0);
/* 346 */     label.setText("Override default port (-P)");
/* 347 */     Text port = new Text(parent, 2048);
/* 348 */     port.setText(this.fConfig.getPort());
/* 349 */     port.setLayoutData(new GridData(4, 0, true, false, 2, 1));
/* 350 */     port.addModifyListener(new ModifyListener(port)
/*     */     {
/*     */       public void modifyText(ModifyEvent e)
/*     */       {
/* 357 */         String newport = this.val$port.getText();
/* 358 */         AVRDudeConfigEditor.this.fConfig.setPort(newport);
/* 359 */         AVRDudeConfigEditor.this.updateCommandPreview();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void addBaudrateControl(Composite parent)
/*     */   {
/* 373 */     Label label = new Label(parent, 0);
/* 374 */     label.setText("Override default baudrate (-b)");
/*     */ 
/* 376 */     Combo baudrate = new Combo(parent, 133120);
/* 377 */     baudrate.setLayoutData(new GridData(16384, 0, true, false, 2, 1));
/* 378 */     baudrate.setItems(new String[] { "", "1200", "2400", "4800", "9600", "19200", "38400", 
/* 379 */       "57600", "115200", "230400", "460800" });
/* 380 */     baudrate.select(baudrate.indexOf(this.fConfig.getBaudrate()));
/*     */ 
/* 382 */     baudrate.addModifyListener(new ModifyListener(baudrate)
/*     */     {
/*     */       public void modifyText(ModifyEvent e)
/*     */       {
/* 389 */         String newbaudrte = this.val$baudrate.getText();
/* 390 */         AVRDudeConfigEditor.this.fConfig.setBaudrate(newbaudrte);
/* 391 */         AVRDudeConfigEditor.this.updateCommandPreview();
/*     */       }
/*     */     });
/* 396 */     baudrate.addVerifyListener(new VerifyListener()
/*     */     {
/*     */       public void verifyText(VerifyEvent event)
/*     */       {
/* 403 */         String text = event.text;
/* 404 */         if (!text.matches("[0-9]*"))
/* 405 */           event.doit = false;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void addExitspecComposite(Composite parent)
/*     */   {
/* 424 */     Group groupcontainer = new Group(parent, 4);
/* 425 */     groupcontainer.setText("State of Parallel Port lines after AVRDude exit");
/* 426 */     groupcontainer.setForeground(parent.getForeground());
/*     */ 
/* 428 */     groupcontainer.setLayoutData(new GridData(4, 4, false, false, 3, 1));
/* 429 */     FillLayout containerlayout = new FillLayout(256);
/* 430 */     containerlayout.spacing = 10;
/* 431 */     containerlayout.marginWidth = 10;
/* 432 */     containerlayout.marginHeight = 5;
/* 433 */     groupcontainer.setLayout(containerlayout);
/*     */ 
/* 435 */     FillLayout grouplayout = new FillLayout(512);
/* 436 */     grouplayout.marginHeight = 5;
/* 437 */     grouplayout.marginWidth = 5;
/* 438 */     grouplayout.spacing = 5;
/*     */ 
/* 446 */     Group resetgroup = new Group(groupcontainer, 0);
/* 447 */     resetgroup.setText("/Reset Line");
/* 448 */     resetgroup.setLayout(grouplayout);
/* 449 */     Button resetDefault = new Button(resetgroup, 16);
/* 450 */     resetDefault.setText("restore to previous state");
/* 451 */     resetDefault.setData("");
/* 452 */     Button resetReset = new Button(resetgroup, 16);
/* 453 */     resetReset.setText("activated (-E reset)");
/* 454 */     resetReset.setData("reset");
/* 455 */     Button resetNoReset = new Button(resetgroup, 16);
/* 456 */     resetNoReset.setText("deactivated (-E noreset)");
/* 457 */     resetNoReset.setData("noreset");
/*     */ 
/* 459 */     String exitReset = this.fConfig.getExitspecResetline();
/* 460 */     if ("noreset".equals(exitReset))
/* 461 */       resetNoReset.setSelection(true);
/* 462 */     else if ("reset".equals(exitReset))
/* 463 */       resetReset.setSelection(true);
/*     */     else {
/* 465 */       resetDefault.setSelection(true);
/*     */     }
/* 467 */     SelectionListener resetlistener = new SelectionAdapter()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 470 */         Button button = (Button)e.widget;
/* 471 */         AVRDudeConfigEditor.this.fConfig.setExitspecResetline((String)button.getData());
/* 472 */         AVRDudeConfigEditor.this.updateCommandPreview();
/*     */       }
/*     */     };
/* 475 */     resetDefault.addSelectionListener(resetlistener);
/* 476 */     resetReset.addSelectionListener(resetlistener);
/* 477 */     resetNoReset.addSelectionListener(resetlistener);
/*     */ 
/* 485 */     Group vccgroup = new Group(groupcontainer, 0);
/* 486 */     vccgroup.setText("Vcc Lines");
/* 487 */     vccgroup.setLayout(grouplayout);
/*     */ 
/* 489 */     Button vccDefault = new Button(vccgroup, 16);
/* 490 */     vccDefault.setText("restore to previous state");
/* 491 */     vccDefault.setData("");
/* 492 */     Button vccVCC = new Button(vccgroup, 16);
/* 493 */     vccVCC.setText("activated (-E vcc)");
/* 494 */     vccVCC.setData("vcc");
/* 495 */     Button vccNoVcc = new Button(vccgroup, 16);
/* 496 */     vccNoVcc.setText("deactivated (-E novcc)");
/* 497 */     vccNoVcc.setData("novcc");
/*     */ 
/* 499 */     String exitVcc = this.fConfig.getExitspecVCCline();
/* 500 */     if ("novcc".equals(exitVcc))
/* 501 */       vccVCC.setSelection(true);
/* 502 */     else if ("vcc".equals(exitVcc))
/* 503 */       vccNoVcc.setSelection(true);
/*     */     else {
/* 505 */       vccDefault.setSelection(true);
/*     */     }
/* 507 */     SelectionListener vcclistener = new SelectionAdapter()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 510 */         Button button = (Button)e.widget;
/* 511 */         AVRDudeConfigEditor.this.fConfig.setExitspecVCCline((String)button.getData());
/* 512 */         AVRDudeConfigEditor.this.updateCommandPreview();
/*     */       }
/*     */     };
/* 515 */     vccDefault.addSelectionListener(vcclistener);
/* 516 */     vccVCC.addSelectionListener(vcclistener);
/* 517 */     vccNoVcc.addSelectionListener(vcclistener);
/*     */   }
/*     */ 
/*     */   private void addPostAVRDudeDelayControl(Composite parent)
/*     */   {
/* 526 */     Label label = new Label(parent, 0);
/* 527 */     label.setText("Delay between avrdude invocations");
/*     */ 
/* 529 */     Text delay = new Text(parent, 133120);
/* 530 */     GridData gd = new GridData(4, 0, false, false, 1, 1);
/* 531 */     gd.widthHint = 60;
/* 532 */     delay.setLayoutData(gd);
/* 533 */     delay.setTextLimit(8);
/* 534 */     delay.setText(this.fConfig.getPostAvrdudeDelay());
/*     */ 
/* 536 */     delay.addModifyListener(new ModifyListener(delay)
/*     */     {
/*     */       public void modifyText(ModifyEvent e)
/*     */       {
/* 543 */         String newdelay = this.val$delay.getText();
/* 544 */         AVRDudeConfigEditor.this.fConfig.setPostAvrdudeDelay(newdelay);
/*     */       }
/*     */     });
/* 549 */     delay.addVerifyListener(new VerifyListener()
/*     */     {
/*     */       public void verifyText(VerifyEvent event)
/*     */       {
/* 556 */         String text = event.text;
/* 557 */         if (!text.matches("[0-9]*"))
/* 558 */           event.doit = false;
/*     */       }
/*     */     });
/* 563 */     label = new Label(parent, 0);
/* 564 */     label.setText("milliseconds");
/*     */   }
/*     */ 
/*     */   private void addCommandlinePreview(Composite parent)
/*     */   {
/* 578 */     Label label = new Label(parent, 0);
/* 579 */     label.setText("Command line preview");
/* 580 */     this.fPreviewText = new Text(parent, 2048);
/* 581 */     this.fPreviewText.setEditable(false);
/* 582 */     this.fPreviewText.setLayoutData(new GridData(4, 0, true, false, 2, 1));
/*     */   }
/*     */ 
/*     */   public ProgrammerConfig getResult()
/*     */   {
/* 598 */     return this.fConfig;
/*     */   }
/*     */ 
/*     */   private String[] getProgrammers()
/*     */   {
/* 609 */     Set nameset = this.fConfigNameMap.keySet();
/* 610 */     String[] allnames = (String[])nameset.toArray(new String[nameset.size()]);
/* 611 */     Arrays.sort(allnames, String.CASE_INSENSITIVE_ORDER);
/* 612 */     return allnames;
/*     */   }
/*     */ 
/*     */   private void updateCommandPreview()
/*     */   {
/* 620 */     java.util.List arglist = this.fConfig.getArguments();
/*     */ 
/* 622 */     StringBuffer sb = new StringBuffer("avrdude ");
/* 623 */     for (String argument : arglist) {
/* 624 */       sb.append(argument).append(" ");
/*     */     }
/* 626 */     sb.append(" [...part specific options...]");
/* 627 */     this.fPreviewText.setText(sb.toString());
/*     */   }
/*     */ 
/*     */   private void updateDetails(IProgrammer type, Text from, Text details)
/*     */   {
/*     */     try
/*     */     {
/* 643 */       entry = AVRDude.getDefault().getProgrammerInfo(type.getId());
/*     */     }
/*     */     catch (AVRDudeException e)
/*     */     {
/*     */       AVRDude.ConfigEntry entry;
/* 646 */       e.printStackTrace();
/* 647 */       from.setText("Error reading avrdude.conf file");
/* 648 */       return;
/*     */     }
/*     */     AVRDude.ConfigEntry entry;
/* 650 */     from.setText("Programmer details from [" + entry.configfile.toOSString() + ":" + 
/* 651 */       entry.linenumber + "]");
/* 652 */     Job job = new UpdateDetailsJob(entry, details);
/* 653 */     job.setSystem(true);
/* 654 */     job.setPriority(20);
/* 655 */     job.schedule();
/*     */   }
/*     */ 
/*     */   private static class UpdateDetailsJob extends Job
/*     */   {
/*     */     private final AVRDude.ConfigEntry fConfigEntry;
/*     */     private final Text fTextControl;
/*     */ 
/*     */     public UpdateDetailsJob(AVRDude.ConfigEntry entry, Text textcontrol)
/*     */     {
/* 684 */       super();
/* 685 */       this.fConfigEntry = entry;
/* 686 */       this.fTextControl = textcontrol;
/*     */     }
/*     */ 
/*     */     protected IStatus run(IProgressMonitor monitor)
/*     */     {
/*     */       try
/*     */       {
/* 696 */         monitor.beginTask("Retrieving programmer info", 1);
/* 697 */         if (this.fTextControl.isDisposed()) {
/* 698 */           return Status.CANCEL_STATUS;
/*     */         }
/*     */ 
/* 702 */         String content = AVRDude.getDefault().getConfigDetailInfo(this.fConfigEntry);
/* 703 */         Display display = this.fTextControl.getDisplay();
/* 704 */         if ((display != null) && (!display.isDisposed()))
/* 705 */           display.syncExec(new Runnable(content) {
/*     */             public void run() {
/* 707 */               AVRDudeConfigEditor.UpdateDetailsJob.this.fTextControl.setText(this.val$content);
/*     */             }
/*     */           });
/* 711 */         monitor.worked(1);
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 717 */         Status status = new Status(4, "de.innot.avreclipse.core", 
/* 718 */           "Can't access avrdude configuration file " + 
/* 719 */           this.fConfigEntry.configfile.toOSString(), ioe);
/* 720 */         AVRPlugin.getDefault().log(status);
/*     */       } finally {
/* 722 */         monitor.done();
/*     */       }
/* 724 */       return Status.OK_STATUS;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\yunluz\Documents\importfile\ARC_GNU_Plugin relevant\other company\avreclipse.2.4.0.final.p2repository\plugins\de.innot.avreclipse.ui_2.4.0.201203041437\
 * Qualified Name:     de.innot.avreclipse.ui.preferences.AVRDudeConfigEditor
 * JD-Core Version:    0.6.0
 */