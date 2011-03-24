package org.micropsi.eclipse.mindconsole;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author daniel
 *
 */
public class NetDebugPerspective implements IPerspectiveFactory {
	
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
	this.layout.setEditorAreaVisible(false);
    
    IFolderLayout one = this.layout.createFolder("one", IPageLayout.TOP, 0.5f, editorArea);
    one.addView("org.micropsi.eclipse.console.adminperspective.parameterview");
        
	IFolderLayout two = this.layout.createFolder("two", IPageLayout.BOTTOM, 0.5f, "org.micropsi.eclipse.console.adminperspective.parameterview");
    two.addView("org.micropsi.eclipse.console.adminperspective.logview");
	
  }
  /**
   * Method defineActions.
   */
  private void defineActions() {
  }

}
