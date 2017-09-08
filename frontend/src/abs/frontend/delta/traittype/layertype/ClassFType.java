/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.layertype;

import java.util.ArrayList;
import java.util.List;

import abs.frontend.ast.ClassDecl;
import abs.frontend.delta.traittype.TraitInnerType;

public class ClassFType extends LayerOneType {
    private String className;
    private List<TraitInnerType> content = new ArrayList<>();
    public ClassFType(ClassDecl decl){
        content.add(TraitInnerType.ABS);
        className = decl.getName();
    }
    public void addInner(TraitInnerType inner){
        content.add(inner);
    }
}
