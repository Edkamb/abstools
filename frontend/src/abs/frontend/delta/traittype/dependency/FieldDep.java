/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

import abs.frontend.ast.FieldUse;

public class FieldDep extends FlatteningDependency {
    private final String name;
    FieldDep(FieldUse use){
        this.name = use.getName();
    }
    @Override
    public String toString() {
        return name;
    }
}
