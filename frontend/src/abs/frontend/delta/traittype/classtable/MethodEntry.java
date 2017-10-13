/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.classtable;

import abs.frontend.ast.MethodSig;

public class MethodEntry extends ElementEntry {

    private final MethodSig sig;

    public MethodEntry(MethodSig sig) {
        super();
        this.sig = sig;
    }

    public MethodSig getSig() {
        return sig;
    }

    @Override
    public String toString() {
        return "MethodEntry [sig=" + sig + "]";
    }
    
}
