/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

import java.util.ArrayList;
import java.util.List;

import abs.frontend.ast.MethodImpl;
import abs.frontend.ast.TraitSetExpr;

public class TraitType {
    List<TraitInnerType> inner = new ArrayList<>();
    public TraitType(TraitSetExpr trait){
        for (MethodImpl met : trait.getMethodImpls()) {
            inner.add(new TraitInnerPreType(met, false));
        }
    }
    @Override
    public String toString() {
        return inner.toString();
    }
}
