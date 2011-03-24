package org.micropsi.eclipse.worldconsole;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class WorldPerspective implements IPerspectiveFactory {
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
	layout.setEditorAreaVisible(false);
    
	layout.addView("org.micropsi.eclipse.worldconsole.worldobjectlistview", IPageLayout.LEFT, 0.25F, editorArea);
	layout.addView("org.micropsi.eclipse.worldconsole.worldobjectpropertyview", IPageLayout.BOTTOM, 0.6F, "org.micropsi.eclipse.worldconsole.worldobjectlistview");
	layout.addView("org.micropsi.eclipse.worldconsole.worldmapview", IPageLayout.RIGHT, 0.75F, editorArea);
           
  }
  /**
   * Method defineActions.
   */
  private void defineActions() {
  }

}
