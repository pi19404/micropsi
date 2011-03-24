package org.micropsi.eclipse.mindconsole;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author daniel
 *
 */
public class SensorPerspective implements IPerspectiveFactory {
	
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
    
    String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible(false);
    
    IFolderLayout one = this.layout.createFolder("one", IPageLayout.TOP, 1.0f, editorArea);
    one.addView("org.micropsi.eclipse.mindconsole.visualsituationview");
		
	IFolderLayout two = this.layout.createFolder("two", IPageLayout.RIGHT, 0.4f, "one");
	two.addView("org.micropsi.eclipse.console.adminperspective.logview");
  }
  /**
   * Method defineActions.
   */
  private void defineActions() {
  }

}
