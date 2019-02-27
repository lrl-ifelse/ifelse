/*
 * Copyright 1999-2019 fclassroom Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ifelse.editors;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.ifelse.utils.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IELanguageFileType  extends LanguageFileType {


    static  IELanguageFileType instance = new IELanguageFileType(IELanguage.INSTANCE);

    protected IELanguageFileType(@NotNull Language language) {
        super(language);
    }

    @NotNull
    @Override
    public String getName() {
        return "ie";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ifelse";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ie";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.icon_logo;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }


}
