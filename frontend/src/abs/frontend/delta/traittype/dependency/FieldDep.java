/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

import abs.frontend.ast.FieldUse;

//models that name must exist as a field
public class FieldDep extends FlatteningDependency {
    private final String name;
    public FieldDep(FieldUse use){
        this.name = use.getName();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FieldDep other = (FieldDep) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    public FieldDep(String name){
        this.name = name;
    }
    @Override
    public String toString() {
        return "∃"+name;
    }
}
