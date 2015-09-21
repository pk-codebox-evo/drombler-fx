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
package org.drombler.fx.core.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.drombler.acp.core.action.spi.MenuItemContainer;
import org.drombler.acp.core.action.spi.PositionableMenuItemAdapter;
import org.drombler.fx.core.action.impl.MenuMenuItemContainer;
import org.softsmithy.lib.util.Positionables;

/**
 *
 * @author puce
 */
public abstract class AbstractMenuItemContainer implements MenuItemContainer<MenuItem, Menu> {

    private static final int SEPARATOR_STEPS = 1000;
    private final Map<String, MenuItemContainer<MenuItem, Menu>> menuContainers = new HashMap<>();
    private final String id;
    private final boolean supportingItems;
    private final List<PositionableMenuItemAdapter<?>> xMenuItems = new ArrayList<>();
//    private final Map<String, List<PositionableMenuItemAdapter<?>>> unresolvedMenus = new HashMap<>();
    private final AbstractMenuItemContainer parentContainer;

    public AbstractMenuItemContainer(String id, boolean supportingItems, AbstractMenuItemContainer parentContainer) {
        this.id = id;
        this.supportingItems = supportingItems;
        this.parentContainer = parentContainer;
    }

    /**
     * @param id
     * @return the menuContainers
     */
    @Override
    public MenuItemContainer<MenuItem, Menu> getMenuContainer(String id) {
        return menuContainers.get(id);
    }

    @Override
    public void addMenu(String id, PositionableMenuItemAdapter<? extends Menu> menu) {
        MenuMenuItemContainer menuMenuContainer = new MenuMenuItemContainer(id, menu.getAdapted(), this,
                getMenuItemRootContainer());
        menuContainers.put(id, menuMenuContainer);

        addMenuItem(menu, getMenus(), true);
        fireMenuAddedEvent(menu, id, menuMenuContainer);
    }

    private void fireMenuAddedEvent(PositionableMenuItemAdapter<? extends Menu> menu, String id,
            AbstractMenuItemContainer menuMenuContainer) {
        getMenuItemRootContainer().fireMenuAddedEvent(menu, id, menuMenuContainer.getPath());
    }

    private List<String> getPath() {
        List<String> path = new ArrayList<>();
        AbstractMenuItemContainer currentParentContainer = parentContainer;
        while (currentParentContainer != null && currentParentContainer.id != null) {
            path.add(currentParentContainer.id);
            currentParentContainer = currentParentContainer.parentContainer;
        }
        Collections.reverse(path);
        return path;
    }

    private <T extends MenuItem> void addMenuItem(PositionableMenuItemAdapter<? extends T> menuItemAdapter, ObservableList<? super T> menuItemList, boolean menu) {
        int index = Positionables.getInsertionPoint(xMenuItems, menuItemAdapter);

        addMenuItem(index, menuItemAdapter, menuItemList, menu);

        if (index < xMenuItems.size() - 1
                && ((xMenuItems.get(index + 1).getPosition() / SEPARATOR_STEPS) - (menuItemAdapter.getPosition() / SEPARATOR_STEPS)) >= 1
                && !xMenuItems.get(index + 1).isSeparator()) {
            addSeparator(index + 1, xMenuItems.get(index + 1).getPosition());
        }

        if (index > 0
                && ((menuItemAdapter.getPosition() / SEPARATOR_STEPS) - (xMenuItems.get(index - 1).getPosition() / SEPARATOR_STEPS)) >= 1
                && !xMenuItems.get(index - 1).isSeparator()) {
            addSeparator(index, menuItemAdapter.getPosition());
        }
    }

    private <T extends MenuItem> void addMenuItem(final int index, final PositionableMenuItemAdapter<? extends T> menuItem,
            final ObservableList<? super T> menuItemList, final boolean menu) {
        xMenuItems.add(index, menuItem);
        menuItemList.add(index, menuItem.getAdapted());
        fireMenuAddedEvent(menuItem, menu);
    }

    private <T extends MenuItem> void fireMenuAddedEvent(PositionableMenuItemAdapter<? extends T> menuItem, boolean menu) {
        if (!menu) {
            getMenuItemRootContainer().fireMenuItemAddedEvent(menuItem, getPath());
        }
    }

    private void addSeparator(int index, int position) {
        if (isSupportingItems()) {
            PositionableMenuItemAdapter<SeparatorMenuItem> xMenuItemWrapper = PositionableMenuItemAdapter.wrapSeparator(
                    new SeparatorMenuItem(),
                    position / SEPARATOR_STEPS * SEPARATOR_STEPS);
            addMenuItem(index, xMenuItemWrapper, getItems(), false);
        }
    }

    protected abstract ObservableList<? super Menu> getMenus();

    @Override
    public void addMenuItem(PositionableMenuItemAdapter<? extends MenuItem> menuItem) {
        addMenuItem(menuItem, getItems(), false);
    }

    protected abstract ObservableList<MenuItem> getItems();

    @Override
    public boolean isSupportingItems() {
        return supportingItems;
    }

    protected abstract AbstractMenuItemRootContainer getMenuItemRootContainer();
}