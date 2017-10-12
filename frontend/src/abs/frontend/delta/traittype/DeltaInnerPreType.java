/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

import java.util.ArrayList;
import java.util.List;

import abs.frontend.ast.ClassDecl;
import abs.frontend.ast.MethodImpl;
import abs.frontend.delta.traittype.dependency.DependencyList;
import abs.frontend.delta.traittype.dependency.FlatteningDependency;

public class DeltaInnerPreType extends DeltaInnerType {
    private String className;
    private List<TraitInnerType> inner = new ArrayList<>();
    private DependencyList deps = new DependencyList();
    
    public DeltaInnerPreType(ClassDecl decl){
        className = decl.getName();
        for (MethodImpl met : decl.getMethods()) {
            inner.add(new TraitInnerPreType(met, false));
        }

    }
    
    public DeltaInnerPreType(String name, DeltaInnerPreType previous){
        className = name;
        inner.add(previous.inner.get(0));
    }
    
    public DeltaInnerPreType(String name) {
        className = name;
        inner.add(new TraitInnerVarType());
    }

    public void add(TraitInnerType inType){
        inner.add(inType);
    }
    
    public void addDep(FlatteningDependency dep){
        deps.add(dep);
    }

    @Override
    public String toString() {
        return className + " : PRE(" + inner + ", deps: "+deps+")";
    }
    
}
