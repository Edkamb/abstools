/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

import abs.frontend.ast.MethodSig;

/**
 *   An inner type of a trait type: an absent method flattening type
 */
public class TraitInnerAbsType extends TraitInnerType {
    public TraitInnerAbsType(MethodSig sig) {
        methodName = sig.getName();
    }

    @Override
    public String toString() {
        return methodName + ": ABS";
    }

    private String methodName;
}
