/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
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

package com.eightbitmage.moonscript.lang.psi.impl.expressions;

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonParenthesizedExpression;
import com.intellij.lang.ASTNode;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jul 3, 2010
 * Time: 11:08:50 AM
 */
public class MoonParenthesizedExpressionImpl extends MoonExpressionImpl implements MoonParenthesizedExpression {
    public MoonParenthesizedExpressionImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MoonExpression getOperand() {
        return (MoonExpression) findChildByType(MoonElementTypes.EXPRESSION_SET);
    }

    @Override
    public String toString() {
        final MoonExpression opr = getOperand();
        return super.toString() + ": (" + (opr!=null?opr.getText():"null") + ")";
    }
}
