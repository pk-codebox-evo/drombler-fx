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
package org.drombler.fx.core.standard.desktop.classic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.drombler.acp.core.action.spi.ToolBarContainer;
import org.drombler.acp.core.action.spi.ToolBarContainerListener;
import org.drombler.acp.core.action.spi.ToolBarContainerToolBarButtonEvent;
import org.drombler.acp.core.action.spi.ToolBarContainerToolBarEvent;
import org.softsmithy.lib.util.PositionableAdapter;
import org.softsmithy.lib.util.Positionables;

/**
 *
 * @author puce
 */
public class ToolBarContainerPane extends HBox implements ToolBarContainer<ToolBar, Node> {

    private final Map<String, PositionableAdapter<ToolBar>> toolBarsMap = new ConcurrentHashMap<>();// TODO: synchronized needed?
    private final List<PositionableAdapter<ToolBar>> toolBars = Collections.synchronizedList(new ArrayList<>());// TODO: synchronized needed?
    private final ConcurrentMap<String, List<PositionableAdapter<? extends Node>>> toolBarButtonsMap = new ConcurrentHashMap<>();// TODO: synchronized needed?
    private final List<ToolBarContainerListener<ToolBar, Node>> containerListeners = Collections.synchronizedList(
            new ArrayList<>()); // TODO: synchronized needed?
    private final ConcurrentMap<String, List<ToolBarContainerListener<ToolBar, Node>>> containerListenerMap = new ConcurrentHashMap<>(); // TODO: synchronized needed?

    @Override
    public void addToolBar(final String toolBarId, final PositionableAdapter<ToolBar> toolBarAdapter) {
        toolBarsMap.put(toolBarId, toolBarAdapter);
        toolBarButtonsMap.putIfAbsent(toolBarId, Collections.synchronizedList(new ArrayList<>()));
        if (toolBarAdapter.getAdapted().isVisible()) {
            positionToolBar(toolBarAdapter);
        }
        fireToolBarAddedEvent(toolBarAdapter, toolBarId);
    }

    private void positionToolBar(PositionableAdapter<ToolBar> toolBarAdapter) {
        int insertionPoint = Positionables.getInsertionPoint(toolBars, toolBarAdapter);
        getChildren().add(insertionPoint, toolBarAdapter.getAdapted());
        toolBars.add(insertionPoint, toolBarAdapter);
        toolBarAdapter.getAdapted().setMaxHeight(Double.MAX_VALUE);
        toolBarAdapter.getAdapted().setMinWidth(0);

        if (insertionPoint == getChildren().size() - 1) {
            if (getChildren().size() > 1) {
                HBox.setHgrow(getChildren().get(getChildren().size() - 2), null);
            }
            HBox.setHgrow(toolBarAdapter.getAdapted(), Priority.ALWAYS);
        }
//        Button button = createButton();
//        toolBarAdapter.getAdapted().getItems().add(0, button);
    }

//    private Button createButton() {
//        Button button = new Button();
//        button.setFocusTraversable(false);
//        //        button.setMnemonicParsing(true);
//        //        button.acceleratorProperty().bind(action.acceleratorProperty());
//        button.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent arg0) {
//                System.out.println("clicked!");
//            }
//        });
//        try (InputStream resourceAsStream = ToolBarContainerPane.class.getResourceAsStream("/saveAll24.png")) {
//            Image iconImage = new Image(resourceAsStream, 24, 24, true,
//                    false);
//            button.setGraphic(new ImageView(iconImage));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
////        button.setText("Test");// TODO: ok? -fx-content-display: GRAPHIC_ONLY ?
//        button.setTooltip(new Tooltip("Test"));
//        button.setDisable(true);
//        return button;
//    }
    @Override
    public boolean containsToolBar(String toolBarId) {
        return toolBarsMap.containsKey(toolBarId);
    }

    private void hideToolBar(PositionableAdapter<ToolBar> toolBarAdapter) {
        int index = toolBars.indexOf(toolBarAdapter);
        getChildren().remove(index);
        toolBars.remove(index);
        if (index == getChildren().size()) {
            HBox.setHgrow(toolBarAdapter.getAdapted(), null);
            if (!getChildren().isEmpty()) {
                HBox.setHgrow(getChildren().get(getChildren().size() - 1), Priority.ALWAYS);
            }
        }
    }

    @Override
    public void addToolBarButton(final String toolBarId, final PositionableAdapter<? extends Node> toolBarButtonAdapter) {
        List<PositionableAdapter<? extends Node>> toolBarButtons = toolBarButtonsMap.get(toolBarId);
        int insertionPoint = Positionables.getInsertionPoint(toolBarButtons, toolBarButtonAdapter);
        ToolBar toolBar = toolBarsMap.get(toolBarId).getAdapted();
        toolBar.getItems().add(insertionPoint, toolBarButtonAdapter.getAdapted());
        toolBarButtons.add(insertionPoint, toolBarButtonAdapter);
        fireToolBarButtonAddedEvent(toolBarButtonAdapter, toolBarId);
    }

    @Override
    public boolean isToolBarVisible(String toolBarId) {
        return toolBarsMap.get(toolBarId).getAdapted().isVisible();
    }

    @Override
    public void setToolBarVisible(final String toolBarId, final boolean visible) {
        if (toolBarsMap.containsKey(toolBarId)) {
            PositionableAdapter<ToolBar> toolBar = toolBarsMap.get(toolBarId);
            if (toolBar.getAdapted().isVisible() && !visible) {
                hideToolBar(toolBar);
            } else if (!toolBar.getAdapted().isVisible() && visible) {
                positionToolBar(toolBar);
            }
            toolBar.getAdapted().setVisible(visible);
        }
    }

    @Override
    public void addToolBarContainerListener(ToolBarContainerListener<ToolBar, Node> containerListener) {
        containerListeners.add(containerListener);
    }

    @Override
    public void removeToolBarContainerListener(ToolBarContainerListener<ToolBar, Node> containerListener) {
        containerListeners.remove(containerListener);
    }

    private void fireToolBarAddedEvent(PositionableAdapter<? extends ToolBar> toolBar, String toolBarId) {
        ToolBarContainerToolBarEvent<ToolBar, Node> event = new ToolBarContainerToolBarEvent<>(this, toolBarId, toolBar);
        containerListeners.forEach(containerListener -> containerListener.toolBarAdded(event));

        if (containerListenerMap.containsKey(toolBarId)) {
            containerListenerMap.get(toolBarId).forEach(containerListener -> containerListener.toolBarAdded(event));
        }
    }

    private void fireToolBarButtonAddedEvent(PositionableAdapter<? extends Node> toolBarButton, String toolBarId) {
        ToolBarContainerToolBarButtonEvent<ToolBar, Node> event = new ToolBarContainerToolBarButtonEvent<>(this,
                toolBarId, toolBarButton);
        containerListeners.forEach(containerListener -> containerListener.toolBarButtonAdded(event));

        if (containerListenerMap.containsKey(toolBarId)) {
            containerListenerMap.get(toolBarId).
                    forEach(containerListener -> containerListener.toolBarButtonAdded(event));

        }
    }

    @Override
    public void addToolBarContainerListener(String toolBarId, ToolBarContainerListener<ToolBar, Node> containerListener) {
        containerListenerMap.putIfAbsent(toolBarId, Collections.synchronizedList(new ArrayList<>()));
        containerListenerMap.get(toolBarId).add(containerListener);
    }

    @Override
    public void removeToolBarContainerListener(String toolBarId,
            ToolBarContainerListener<ToolBar, Node> containerListener) {
        List<ToolBarContainerListener<ToolBar, Node>> listeners = containerListenerMap.get(toolBarId);
        if (listeners != null) {
            listeners.remove(containerListener);
        }
    }
}
