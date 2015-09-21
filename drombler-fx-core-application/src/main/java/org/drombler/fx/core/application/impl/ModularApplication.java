/*
 *         COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Notice
 *
 * The contents of this file are subject to the COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL)
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.opensource.org/licenses/cddl1.txt
 *
 * The Original Code is Drombler.org. The Initial Developer of the
 * Original Code is Florian Brunner (Sourceforge.net user: puce).
 * Copyright 2012 Drombler.org. All Rights Reserved.
 *
 * Contributor(s): .
 */
package org.drombler.fx.core.application.impl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.drombler.acp.core.action.spi.ApplicationToolBarContainerProvider;
import org.drombler.acp.core.action.spi.MenuBarMenuContainerProvider;
import org.drombler.acp.core.application.ApplicationExecutorProvider;
import org.drombler.acp.core.application.MainWindowProvider;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author puce
 */
public class ModularApplication extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(ModularApplication.class);

    // TODO: better way than static fields?
    private static BundleContext BUNDLE_CONTEXT;
    private static String APPLICATION_TITLE = "Drombler FX based Application";
    private static double APPLICATION_WIDTH = 1024;
    private static double APPLICATION_HEIGHT = 768;

    private final FXApplicationExecutorProvider fxApplicationExecutorProvider = new FXApplicationExecutorProvider();
    private ApplicationPane root;
    private MainSceneProvider mainSceneProvider;
    private MainWindowProvider<Stage> mainWindowProvider;

    public static void launch(BundleContext bundleContext, String applicationTitle, double applicationWidth,
            double applicationHeight) {
        BUNDLE_CONTEXT = bundleContext;
        if (applicationTitle != null) {
            APPLICATION_TITLE = applicationTitle;
        }
        if (applicationWidth > 0) {
            APPLICATION_WIDTH = applicationWidth;
        }
        if (applicationHeight > 0) {
            APPLICATION_HEIGHT = applicationHeight;
        }
        LOG.info("Launching JavaFX Application '{}' ({}x{})...", applicationTitle, applicationWidth, applicationHeight);
        Application.launch(ModularApplication.class);
        LOG.info("Stopped JavaFX Application '{}'", applicationTitle);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        Pane root = FXMLLoader.load(getClass().getResource("ContactCenterPane.fxml"));
        root = new ApplicationPane();
        BUNDLE_CONTEXT.registerService(
                new String[]{
                    MenuBarMenuContainerProvider.class.getName(), 
                    ContentPaneProvider.class.getName(),
                    ApplicationToolBarContainerProvider.class.getName()
                }, root, null);
//        Parent personEditorPane = FXMLLoader.load(getClass().getResource("PersonEditorPane.fxml"));
//        root.getChildren().add(personEditorPane);
        stage.setTitle(getTitle());
        final Scene scene = new Scene(root, getWidth(), getHeight());
//        scene.focusOwnerProperty().addListener(new ChangeListener<Node>() {
//
//            @Override
//            public void changed(ObservableValue<? extends Node> ov, Node oldValue, Node newValue) {
//                System.out.println("Focus changed: " + newValue);
//            }
//        });
        mainSceneProvider = () -> scene;
        BUNDLE_CONTEXT.registerService(MainSceneProvider.class, mainSceneProvider, null);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        mainWindowProvider = () -> stage;
        BUNDLE_CONTEXT.registerService(MainWindowProvider.class, mainWindowProvider, null);
        // Only register the ApplicationExecutorProvider once the JavaFX Platform has been started.
        BUNDLE_CONTEXT.registerService(ApplicationExecutorProvider.class, fxApplicationExecutorProvider, null);
        LOG.info("Started JavaFX Application '{}'", getTitle());
    }

    @Override
    public void stop() throws Exception {
    }

    private double getWidth() {
        return APPLICATION_WIDTH;
    }

    private double getHeight() {
        return APPLICATION_HEIGHT;
    }

    private String getTitle() {
        return APPLICATION_TITLE;
    }
}