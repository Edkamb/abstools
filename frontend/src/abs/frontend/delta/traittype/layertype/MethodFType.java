/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.layertype;

import abs.frontend.ast.MethodImpl;
import abs.frontend.ast.MethodSig;

public class MethodFType extends LayerTwoType {
    private MethodSig sig;
    private boolean usesOrig = false;
    @Override
    public String toString() {
        return "sig=" + sig + ", usesOrig=" + usesOrig + ", deps=" + dependencies;
    }
    public MethodFType(MethodImpl met, boolean empty) {
        if(!empty)traverse(met.getBlock());
        this.sig = met.getMethodSig();
    }

}
