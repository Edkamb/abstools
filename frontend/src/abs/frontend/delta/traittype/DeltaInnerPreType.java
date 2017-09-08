/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

import java.util.ArrayList;
import java.util.List;

import abs.frontend.ast.ClassDecl;

public class DeltaInnerPreType extends DeltaInnerType {
    private String className;
    private List<TraitInnerType> inner = new ArrayList<>();
    
    public DeltaInnerPreType(ClassDecl decl){
        className = decl.getName();
    }
    
    public void add(TraitInnerType inType){
        inner.add(inType);
    }

    @Override
    public String toString() {
        return className + " : PRE(" + inner + ")";
    }
    
}
