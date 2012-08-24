#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ${package}.sample.impl;

import org.richclientplatform.core.action.AbstractToggleActionListener;
import org.richclientplatform.core.action.ToggleAction;
import org.richclientplatform.core.action.ToggleMenuEntry;
import org.richclientplatform.core.action.ToolBarToggleEntry;

/**
 *
 * @author puce
 */
@ToggleAction(id = "yellowRectangle", category = "test", displayName = "#yellowRectangle.displayName", accelerator = "Shortcut+Y",
icon = "yellow-rectangle.png")
@ToggleMenuEntry(path = "Custom", position = 1220)
@ToolBarToggleEntry(toolBarId = "rectangle", position = 160)
public class YellowRectangleAction extends AbstractToggleActionListener<Object> {

    @Override
    public void onSelectionChanged(boolean oldValue, boolean newValue) {
        System.out.println("Yellow Rectangle: " + newValue);
    }
}
