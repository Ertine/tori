/*
 * Copyright 2012 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.tori.component;

import java.util.Collections;

import org.vaadin.tori.widgetset.client.ui.lazylayout.AbstractLazyLayoutClientRpc;

import com.vaadin.shared.Connector;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class LazyLayout extends AbstractLazyLayout {
    @Override
    protected AbstractLazyLayoutClientRpc getRpc() {
        return getRpcProxy(AbstractLazyLayoutClientRpc.class);
    }

    public void addComponentEagerly(final Component c) {
        addComponent(c);
        loadedComponents.add(c);

        /*
         * TODO: this maybe needs to be optimized so that it's not individual
         * rpc calls, but a queue that gets built and sent over as a state
         * change.
         */
        getRpcProxy(AbstractLazyLayoutClientRpc.class).sendComponents(
                Collections.singletonMap(components.indexOf(c), (Connector) c));
    }

    @Override
    public void replaceComponent(final Component oldComponent,
            final Component newComponent) {

        if (oldComponent == newComponent) {
            return;
        }

        if (!components.contains(oldComponent)) {
            throw new IllegalArgumentException("old component " + oldComponent
                    + " isn't in the layout");
        }

        final int oldIndex = components.indexOf(oldComponent);
        final boolean oldIsLoaded = loadedComponents.contains(oldComponent);

        if (components.contains(newComponent)) {
            final int newIndex = components.indexOf(newComponent);
            final boolean newIsLoaded = loadedComponents.contains(newComponent);

            components.remove(oldIndex);
            components.add(oldIndex, newComponent);
            components.remove(newIndex);
            components.add(newIndex, oldComponent);

            if (oldIsLoaded || newIsLoaded) {
                loadedComponents.add(oldComponent);
                loadedComponents.add(newComponent);
            }
        } else {
            components.remove(oldComponent);
            components.add(oldIndex, newComponent);

            super.addComponent(newComponent, oldIndex);
            super.removeComponent(oldComponent);

            if (oldIsLoaded) {
                loadedComponents.add(newComponent);
                loadedComponents.remove(oldComponent);
            }
        }

        markAsDirty();
    }
}
