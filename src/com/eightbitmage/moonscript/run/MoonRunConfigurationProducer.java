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

package com.eightbitmage.moonscript.run;

import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.sdk.KahluaSdk;
import com.eightbitmage.moonscript.sdk.MoonSdkType;
import com.intellij.execution.Location;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.junit.RuntimeConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * This class is based on code of the intellij-batch plugin.
 *
 * @author wibotwi, jansorg, sylvanaar
 */
public class MoonRunConfigurationProducer extends RuntimeConfigurationProducer implements Cloneable {
    private PsiFile sourceFile = null;

    public MoonRunConfigurationProducer() {
        super(MoonConfigurationType.getInstance());
    }



    @Override
    public PsiElement getSourceElement() {
        return sourceFile;
    }

    @Override
    protected RunnerAndConfigurationSettingsImpl createConfigurationByElement(Location location, ConfigurationContext configurationContext) {
        sourceFile = location.getPsiElement().getContainingFile();

        if (sourceFile != null && sourceFile.getFileType().equals(MoonFileType.MOON_FILE_TYPE)) {
            Project project = sourceFile.getProject();
            RunnerAndConfigurationSettings settings = cloneTemplateConfiguration(project, configurationContext);

            VirtualFile file = sourceFile.getVirtualFile();

            MoonRunConfiguration runConfiguration = (MoonRunConfiguration) settings.getConfiguration();
            if (file != null) {
                runConfiguration.setName(file.getName());

                runConfiguration.setScriptName(file.getPath());
                if (file.getParent() != null) {
                    runConfiguration.setWorkingDirectory(file.getParent().getPath());
                }
            }

            Module module = ModuleUtil.findModuleForPsiElement(location.getPsiElement());
            if (module != null) {
                runConfiguration.setModule(module);
            }

            if (StringUtil.isEmptyOrSpaces(runConfiguration.getInterpreterPath())) {
                if (module != null) {
                    Sdk sdk = ModuleRootManager.getInstance(module).getSdk();

                    if (sdk != null) {
                        if (sdk.getSdkType() == MoonSdkType.getInstance()) {

                             if (sdk.getName().equals(KahluaSdk.NAME))
                                runConfiguration.setUsingInternalInterpreter(true);
                             else
                                runConfiguration.setInterpreterPath(MoonSdkType.getTopLevelExecutable(sdk.getHomePath()).getAbsolutePath());
                        }
                    }
                }
            }



            return (RunnerAndConfigurationSettingsImpl) settings;
        }

        return null;
    }

    public int compareTo(Object o) {
        return 0;
    }
}