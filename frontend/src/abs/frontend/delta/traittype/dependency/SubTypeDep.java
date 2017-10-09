/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

public class SubTypeDep extends FlatteningDependency {
    protected TypeOfDependency lhs;
    protected TypeOfDependency rhs;
    public SubTypeDep(TypeOfDependency lhs, TypeOfDependency rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    public TypeOfDependency getLhs() {
        return lhs;
    }
    public TypeOfDependency getRhs() {
        return rhs;
    }
    @Override
    public String toString() {
        return lhs +" ⪯ " + rhs;
    }
    
}
