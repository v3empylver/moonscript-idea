/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.eightbitmage.moonscript.lang.documentor;

import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocTag;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocCommentOwner;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/1/11
 * Time: 12:28 AM
 */
public class MoonDocDocumentationProvider implements DocumentationProvider {
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        element = element.getParent().getParent();
        if (element instanceof MoonDocCommentOwner) {
            MoonDocComment docComment = ((MoonDocCommentOwner) element).getDocComment();
            if (docComment!=null) {
                StringBuilder sb = new StringBuilder();
                for(PsiElement e : docComment.getDescriptionElements())
                    sb.append(e.getText()).append("\n");

                sb.append("<br><br><br>");
                
                for (MoonDocTag tag : docComment.getTags()) {
                    if (tag.getName().contains("return"))
                        sb.append("<b>returns  </b>");
                    else
                        sb.append("<pre>").append(StringUtil.notNullize((tag.getValueElement()!=null)?tag.getValueElement().getText():null)).append("</pre>");

                    for(PsiElement desc : tag.getDescriptionElements())
                        sb.append(desc.getText()).append("\n");

                    sb.append("<br><br>");
                }

                return sb.toString();
            }
        }
                
        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }
}
