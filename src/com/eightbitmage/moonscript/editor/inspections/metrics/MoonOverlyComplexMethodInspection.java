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
package com.eightbitmage.moonscript.editor.inspections.metrics;


import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

public class MoonOverlyComplexMethodInspection extends MoonMethodMetricInspection {

  @NotNull
  public String getDisplayName() {
    return "Overly complex method";
  }


  protected int getDefaultLimit() {
    return 20;
  }

  protected String getConfigurationLabel() {
    return "Method complexity limit:";
  }

  public String buildErrorString(Object... args) {
    return "Method '#ref' is overly complex ( cyclomatic complexity =" + args[0] + '>' + args[1] + ')';
  }

    @Override
  public MoonElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
    return new MoonElementVisitor() {
         public void visitFunctionDef(MoonFunctionDefinitionStatement func) {
              super.visitFunctionDef(func);
              final int limit = getLimit();
              final CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
              final MoonBlock body = func.getBlock();
              if (body == null) {
                return;
              }
              body.accept(visitor);
              final int complexity = visitor.getComplexity();
              if (complexity <= limit) {
                return;
              }
              holder.registerProblem(func.getIdentifier(), buildErrorString(complexity, limit), LocalQuickFix.EMPTY_ARRAY);
         }
    };
  }
//
//  private class Visitor extends BaseInspectionVisitor {
//    public void visitMethod(MoonFunctionDefinitionStatement func) {
//      super.visitFunctionDef(func);
//      final int limit = getLimit();
//      final CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
//      final MoonBlock body = func.getBlock();
//      if (body == null) {
//        return;
//      }
//      body.accept(visitor);
//      final int complexity = visitor.getComplexity();
//      if (complexity <= limit) {
//        return;
//      }
//      registerMethodError(grMethod, complexity, limit);
//    }
//  }
}