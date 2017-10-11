/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.layertype;

import abs.frontend.ast.MethodSig;

public class SignatureFType extends LayerTwoType {
    private final MethodSig sig;

    @Override
    public String toString() {
        return "SignatureType [sig=" + sig + "]";
    }

    public SignatureFType(MethodSig sig) {
        this.sig = sig;
    }

}
