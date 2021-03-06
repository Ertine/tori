/*
 * Copyright 2011 Vaadin Ltd.
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
package org.vaadin.tori.widgetset.client.ui.threadlistingrow;

import org.vaadin.tori.component.thread.ThreadListingRow;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.VCaption;
import com.vaadin.client.VCaptionWrapper;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings({ "serial", "deprecation" })
@Connect(ThreadListingRow.class)
public class ThreadListingRowConnector extends
        AbstractComponentContainerConnector implements Paintable,
        PostLayoutListener {

    public static class RowLayoutTimer extends Timer {
        private static final int DELAY = 200;
        private int rows = 0;
        private JsArray<Element> elementsToResize = Element.createArray()
                .cast();
        private boolean isStarted;

        public void addRow(final ThreadListingRowConnector row) {
            rows++;
            VConsole.log("adding row. Now total of " + rows);
            elementsToResize.push(row.getWidget().getElement());
        }

        public void start() {
            if (!isStarted) {
                run();
                isStarted = true;
                scheduleRepeating(DELAY);
            }
        }

        @Override
        public void run() {
            if (elementsToResize.length() > 0) {
                fixTopicWidth(elementsToResize);
                elementsToResize = Element.createArray().cast();
            }
        }

        private native void fixTopicWidth(JsArray<Element> elementsToResize)
        /*-{
            // 365 is a precalculated amount if pixels that the other columns occupy
            var topicWidth = (elementsToResize[0].offsetWidth - 365)+"px";
            
            for (var i=0; i<elementsToResize.length; i++) {
                var topicElement = elementsToResize[i].children[0].children[0];
                topicElement.style.width = topicWidth;
            } 
        }-*/;

        public void removeRow() {
            rows--;
            if (rows <= 0) {
                rows = 0;
                cancel();
            }
            VConsole.log("removing row. Now total of " + rows);
        }
    }

    private static final RowLayoutTimer ROW_TIMER = new RowLayoutTimer();

    private boolean centerAfterLayout = false;

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    protected void init() {
        ROW_TIMER.addRow(this);
    }

    @Override
    public void onUnregister() {
        ROW_TIMER.removeRow();
        super.onUnregister();
    }

    /**
     * 
     * 
     * @see com.vaadin.terminal.gwt.client.ComponentConnector#updateFromUIDL(com.vaadin.terminal.gwt.client.UIDL,
     *      com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    @Override
    public void updateFromUIDL(final UIDL uidl,
            final ApplicationConnection client) {
        if (uidl.hasAttribute("cached")) {
            return;
        }
        // These are for future server connections
        getWidget().client = client;
        getWidget().uidlId = uidl.getId();

        getWidget().hostPopupVisible = uidl
                .getBooleanVariable("popupVisibility");

        getWidget().setHTML(uidl.getStringAttribute("html"));

        if (uidl.hasAttribute("hideOnMouseOut")) {
            getWidget().popup.setHideOnMouseOut(uidl
                    .getBooleanAttribute("hideOnMouseOut"));
        }

        // Render the popup if visible and show it.
        if (getWidget().hostPopupVisible) {
            final UIDL popupUIDL = uidl.getChildUIDL(0);

            // showPopupOnTop(popup, hostReference);
            getWidget().preparePopup(getWidget().popup);
            getWidget().popup.updateFromUIDL(popupUIDL, client);
            getWidget().popup.setStyleName(getWidget().popup
                    .getStylePrimaryName());
            getWidget().showPopup(getWidget().popup);
            centerAfterLayout = true;

            // The popup shouldn't be visible, try to hide it.
        } else {
            getWidget().popup.hide();
        }
    }// updateFromUIDL

    @Override
    public void updateCaption(final ComponentConnector component) {
        if (VCaption.isNeeded(component.getState())) {
            if (getWidget().popup.captionWrapper != null) {
                getWidget().popup.captionWrapper.updateCaption();
            } else {
                getWidget().popup.captionWrapper = new VCaptionWrapper(
                        component, getConnection());
                getWidget().popup.setWidget(getWidget().popup.captionWrapper);
                getWidget().popup.captionWrapper.updateCaption();
            }
        } else {
            if (getWidget().popup.captionWrapper != null) {
                getWidget().popup
                        .setWidget(getWidget().popup.popupComponentWidget);
            }
        }
    }

    @Override
    public VThreadListingRow getWidget() {
        return (VThreadListingRow) super.getWidget();
    }

    @Override
    public void postLayout() {
        if (centerAfterLayout) {
            centerAfterLayout = false;
            getWidget().reposition();
        }

        ROW_TIMER.start();
        /*
         * this doesn't support resizing of the topic part on window resizes.
         * Maybe in the future again.
         */
    }

    @Override
    public void onConnectorHierarchyChange(
            final ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // ignore?
    }

}
