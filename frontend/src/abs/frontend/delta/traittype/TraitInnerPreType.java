/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

import abs.frontend.ast.MethodImpl;
import abs.frontend.ast.MethodSig;
import abs.frontend.delta.traittype.layertype.LayerTwoType;
import abs.frontend.delta.traittype.layertype.LayerTwoVarType;
import abs.frontend.delta.traittype.layertype.MethodFType;
import abs.frontend.delta.traittype.layertype.SignatureFType;

/**
 *   An inner type of a trait type: a present method flattening type
 */
public class TraitInnerPreType extends TraitInnerType {
    public TraitInnerPreType(MethodImpl met, boolean empty) {
        methodName = met.getMethodSig().getName();
        if(empty)
            type = new LayerTwoVarType();            
        else
            type = new MethodFType(met, empty);
    }
    public TraitInnerPreType(MethodSig sig) {
        methodName = sig.getName();
        type = new SignatureFType(sig);
    }
    @Override
    public String toString() {
        return methodName + " : Pre(" + type + ")";
    }
    private String methodName;
    private LayerTwoType type;
}
