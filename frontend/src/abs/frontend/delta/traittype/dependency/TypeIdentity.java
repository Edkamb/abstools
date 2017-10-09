/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

import abs.frontend.typechecker.Type;

public class TypeIdentity extends TypeOfDependency {

    private final Type type;
    public TypeIdentity(Type type) {
        super(type.toString());
        this.type = type;
    }
    public TypeIdentity(String className) {
        super(className);
        this.type = null;
    }

}
