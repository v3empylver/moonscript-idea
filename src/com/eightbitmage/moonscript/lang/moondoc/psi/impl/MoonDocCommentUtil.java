/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eightbitmage.moonscript.lang.moondoc.psi.impl;

import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocCommentOwner;
import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonTableConstructor;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonMaybeDeclarationAssignmentStatement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocPsiElement;
import org.jetbrains.annotations.Nullable;


public abstract class MoonDocCommentUtil {
    private  static final Logger log = Logger.getInstance("Lua.MoonDocCommentUtil");
    @Nullable
    public static MoonDocCommentOwner findDocOwner(MoonDocPsiElement docElement) {
        PsiElement element = docElement;
        while (element != null && element.getParent() instanceof MoonDocPsiElement) element = element.getParent();
        if (element == null) return null;

        while (true) {
            element = element.getNextSibling();
            if (element instanceof MoonBlock)
                element = element.getFirstChild();
            if (element == null) return null;
            final ASTNode node = element.getNode();
            if (node == null) return null;
            if (MoonElementTypes.LUADOC_COMMENT.equals(node.getElementType()) ||
                !MoonElementTypes.WHITE_SPACES_OR_COMMENTS.contains(node.getElementType())) {
                break;
            }
        }

        if (element instanceof MoonDocCommentOwner) return (MoonDocCommentOwner) element;

        if (element instanceof MoonMaybeDeclarationAssignmentStatement) {
            MoonExpression[] expressions = ((MoonMaybeDeclarationAssignmentStatement) element).getDefinedSymbolValues();

            for (MoonExpression e : expressions)
                if (e instanceof MoonDocCommentOwner) return (MoonDocCommentOwner) e;
        }


        return null;
    }

    @Nullable
    public static MoonDocComment findDocComment(MoonDocCommentOwner owner) {
        PsiElement element;

        if (owner instanceof MoonTableConstructor) element = owner.getParent().getParent().getPrevSibling();
        else element = owner.getPrevSibling();

        while (true) {
            if (element == null) return null;
            final ASTNode node = element.getNode();
            if (node == null) return null;
            if (MoonElementTypes.LUADOC_COMMENT.equals(node.getElementType()) ||
                !MoonElementTypes.WHITE_SPACES_OR_COMMENTS.contains(node.getElementType())) {
                break;
            }
            element = element.getPrevSibling();
        }
        if (element instanceof MoonDocComment) return (MoonDocComment) element;
        return null;
    }
}
