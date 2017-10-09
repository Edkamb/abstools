/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

public class InnerSubTypeDep extends SubTypeDep {

    public InnerSubTypeDep(TypeOfDependency lhs, TypeOfDependency rhs) {
        super(lhs, rhs);
    }

    @Override
    public String toString() {
        return lhs +" ⪯⪯ " + rhs;
    }
}
