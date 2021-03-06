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

package com.eightbitmage.moonscript.lang.psi.stubs.elements;

import com.eightbitmage.moonscript.lang.psi.expressions.MoonFieldIdentifier;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonFieldIdentifierImpl;
import com.eightbitmage.moonscript.lang.psi.stubs.MoonStubElementType;
import com.eightbitmage.moonscript.lang.psi.stubs.impl.MoonFieldStub;
import com.eightbitmage.moonscript.lang.psi.stubs.index.MoonFieldIndex;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

import java.io.IOException;

/**
* Created by IntelliJ IDEA.
* User: Jon S Akhtar
* Date: 1/23/11
* Time: 8:01 PM
*/
public class MoonFieldStubType
        extends MoonStubElementType<MoonFieldStub, MoonFieldIdentifier> {

    public MoonFieldStubType() {
        super("field name stub");
    }

    @Override
    public MoonFieldIdentifier createPsi(MoonFieldStub stub) {
        return new MoonFieldIdentifierImpl(stub);
    }

    @Override
    public MoonFieldStub createStub(MoonFieldIdentifier psi, StubElement parentStub) {
        return new MoonFieldStub(parentStub, StringRef.fromString(psi.getName()));
    }

    @Override
    public void serialize(MoonFieldStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @Override
    public MoonFieldStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        return new MoonFieldStub(parentStub, ref);
    }

    @Override
    public String getExternalId() {
        return "moon.FIELD";
    }

    @Override
    public void indexStub(MoonFieldStub stub, IndexSink sink) {
        String name = stub.getName();
        
        if (name != null) {
          sink.occurrence(MoonFieldIndex.KEY, name);
        }
    }

    @Override
    public PsiElement createElement(ASTNode node) {
        return new MoonFieldIdentifierImpl(node);
    }
}
