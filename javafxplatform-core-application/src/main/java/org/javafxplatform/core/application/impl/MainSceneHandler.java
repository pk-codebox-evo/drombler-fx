/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javafxplatform.core.application.impl;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.References;
import org.javafxplatform.core.application.FocusOwnerChangeListenerProvider;
import org.osgi.service.component.ComponentContext;

/**
 *
 * @author puce
 */
@Component(immediate = true)
@References({
    @Reference(name = "mainSceneProvider", referenceInterface = MainSceneProvider.class),
    @Reference(name = "focusOwnerChangeListenerProvider", referenceInterface = FocusOwnerChangeListenerProvider.class)
})
public class MainSceneHandler {

    private Scene mainScene;
    private ChangeListener<Node> focusOwnerChangeListener;

    protected void bindMainSceneProvider(MainSceneProvider mainSceneProvider) {
        mainScene = mainSceneProvider.getMainScene();
    }

    protected void unbindMainSceneProvider(MainSceneProvider mainSceneProvider) {
        mainScene = null;
    }

    protected void bindFocusOwnerChangeListenerProvider(FocusOwnerChangeListenerProvider focusOwnerChangeListenerProvider) {
        this.focusOwnerChangeListener = focusOwnerChangeListenerProvider.getFocusOwnerChangeListener();
    }

    protected void unbindFocusOwnerChangeListenerProvider(FocusOwnerChangeListenerProvider focusOwnerChangeListenerProvider) {
        this.focusOwnerChangeListener = null;
    }

    @Activate
    protected void activate(ComponentContext context) {
        mainScene.focusOwnerProperty().addListener(focusOwnerChangeListener);
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        mainScene.focusOwnerProperty().removeListener(focusOwnerChangeListener);
    }
}
