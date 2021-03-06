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

package org.vaadin.tori.util;

import java.util.Collection;
import java.util.Map;

import org.vaadin.tori.data.entity.Post;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Since formatting the posts depends on the back-end, this needs to be deferred
 * to the back-end specific projects.
 */
public interface PostFormatter {

    public interface FontsInfo {
        public interface FontFace {
            @NonNull
            String getFontName();

            @NonNull
            String getFontSyntax();
        }

        public interface FontSize {
            @NonNull
            String getFontSizeName();

            @NonNull
            String getFontSizeSyntax();
        }

        /**
         * @return Font face information. <code>null</code> is returned if the
         *         author is not allowed to change the font.
         */
        @CheckForNull
        Collection<FontFace> getFontFaces();

        /**
         * @return Font size information. <code>null</code> is returned if the
         *         author is not allowed to change the font size.
         */
        @CheckForNull
        Collection<FontSize> getFontSizes();
    }

    public interface FormatInfo {
        public static final String ICON_PACKAGE = "/icons/";

        @NonNull
        String getFormatName();

        @NonNull
        String getFormatSyntax();

        /**
         * The image file name that is located in the DataSource project's
         * classpath, relative to {@value #ICON_PACKAGE}. Usually only the file
         * name itself.
         */
        @NonNull
        String getFormatIcon();
    }

    /**
     * Given a raw string of text for a {@link Post}, format it in the
     * appropriate way into valid XHTML.
     * <p/>
     * <strong>Note:</strong> make sure to sanitize the raw post for possible
     * XHTML, if you don't want users to be able to format posts with XHTML,
     * risking XSS and other security attacks.
     * 
     * @return The XHTML to be rendered as-is.
     * @see Post#getBodyRaw()
     */
    @NonNull
    String format(@NonNull String rawPostBody);

    /**
     * The returned string should be an XHTML-formatted explanation of the
     * formatting currently being used to render the posts.
     */
    @NonNull
    String getFormattingSyntaxXhtml();

    @NonNull
    FontsInfo getFontsInfo();

    /**
     * Get the format info for "bold".
     * 
     * @return might be <code>null</code>, which indicates that there's no bold
     *         format.
     */
    @CheckForNull
    FormatInfo getBoldInfo();

    /**
     * Get the format info for "italic".
     * 
     * @return might be <code>null</code>, which indicates that there's no
     *         italic format.
     */
    @CheckForNull
    FormatInfo getItalicInfo();

    /**
     * All the other styles that are behind buttons.
     * 
     * @return might be <code>null</code>, which indicates that there's no other
     *         formats. The same goes for an empty {@link Collection}
     */
    @CheckForNull
    Collection<? extends FormatInfo> getOtherFormattingInfo();

    @NonNull
    String getQuote(final Post postToQuote);

    void setPostReplacements(Map<String, String> postReplacements);
}
