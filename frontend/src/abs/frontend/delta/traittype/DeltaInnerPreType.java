/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

import java.util.ArrayList;
import java.util.List;

import abs.frontend.ast.ClassDecl;
import abs.frontend.ast.MethodImpl;

public class DeltaInnerPreType extends DeltaInnerType {
    private String className;
    private List<TraitInnerType> inner = new ArrayList<>();
    
    public DeltaInnerPreType(ClassDecl decl, boolean empty){
        className = decl.getName();
        if(!empty)
            for (MethodImpl met : decl.getMethods()) {
                inner.add(new TraitInnerPreType(met, empty));
            }
    }
    
    public DeltaInnerPreType(String name) {
        className = name;
    }

    public void add(TraitInnerType inType){
        inner.add(inType);
    }

    @Override
    public String toString() {
        return className + " : PRE(" + inner + ")";
    }
    
}
