/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

import abs.frontend.ast.MethodImpl;
import abs.frontend.delta.traittype.layertype.LayerTwoType;
import abs.frontend.delta.traittype.layertype.MethodFType;

public class TraitInnerPreType extends TraitInnerType {
    public TraitInnerPreType(MethodImpl met, boolean empty) {
        methodName = met.getMethodSig().getName();
        type = new MethodFType(met, empty);
    }
    @Override
    public String toString() {
        return methodName + " : Pre(" + type + ")";
    }
    private String methodName;
    private LayerTwoType type;
}
