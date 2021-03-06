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

import org.vaadin.hene.expandingtextarea.ExpandingTextArea;
import org.vaadin.hene.expandingtextarea.widgetset.client.ui.ExpandingTextAreaState;
import org.vaadin.tori.widgetset.client.ui.expandingtextarea.ToriExpandingTextAreaClientRpc;

@SuppressWarnings("serial")
public class ToriExpandingTextArea extends ExpandingTextArea {

    public void blur() {
        getRpcProxy(ToriExpandingTextAreaClientRpc.class).blur();
    }

    public void setMaxRows(final int maxRowsExpanded) {
        getState().maxRows = maxRowsExpanded;
    }

    @Override
    protected ExpandingTextAreaState getState() {
        return super.getState();
    }
}
