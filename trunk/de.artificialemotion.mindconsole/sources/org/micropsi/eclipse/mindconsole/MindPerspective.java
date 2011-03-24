package org.micropsi.eclipse.mindconsole;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author daniel
 *
 */
public class MindPerspective implements IPerspectiveFactory {
	
  IPageLayout layout;

  /**
   * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
   */
  public void createInitialLayout(IPageLayout layout) {
    this.layout = layout;
    defineActions();
    defineLayout();
  }
 
  /**
   * Method defineLayout.
   */
  private void defineLayout() {
    
    String editorArea = this.layout.getEditorArea();
    
    IFolderLayout one = this.layout.createFolder("one", IPageLayout.TOP, 0.8f, editorArea);
    one.addView("org.micropsi.eclipse.mindconsole.mindeditview");
    
    IFolderLayout two = this.layout.createFolder("two", IPageLayout.RIGHT, 0.8f, "org.micropsi.eclipse.mindconsole.mindeditview");
    two.addView("org.micropsi.eclipse.mindconsole.entityeditview");
    
  	IFolderLayout three = this.layout.createFolder("three", IPageLayout.BOTTOM, 0.5f, "org.micropsi.eclipse.mindconsole.entityeditview");
  	three.addView("org.micropsi.eclipse.mindconsole.linkageeditview");
	three.addView("org.micropsi.eclipse.mindconsole.incominglinksview");
    
	IFolderLayout four = this.layout.createFolder("four", IPageLayout.RIGHT, 0.7f, editorArea);
	four.addView("org.eclipse.ui.views.TaskList");
    four.addView("org.micropsi.eclipse.console.adminperspective.logview");
	four.addView("org.micropsi.eclipse.mindconsole.libraryview");
	four.addView("org.micropsi.eclipse.mindconsole.scriptview");
	
  }
  /**
   * Method defineActions.
   */
  private void defineActions() {
  }

}
