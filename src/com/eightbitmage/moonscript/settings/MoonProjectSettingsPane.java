/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: LuaProjectSettingsPane.java, Class: LuaProjectSettingsPane
 * Last modified: 2010-02-17
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eightbitmage.moonscript.settings;

import org.picocontainer.Disposable;

import javax.swing.*;

/**
 * User: jansorg
 * Date: Oct 30, 2009
 * Time: 9:18:52 PM
 */
public class MoonProjectSettingsPane implements Disposable {
    private JPanel settingsPane;

    public MoonProjectSettingsPane() {
    }

    public void dispose() {
    }

    public void setData(MoonProjectSettings settings) {

    }

    public void storeSettings(MoonProjectSettings settings) {
    }

    public boolean isModified(MoonProjectSettings settings) {
        return false;
    }

    public JPanel getPanel() {
        return settingsPane;
    }

}
