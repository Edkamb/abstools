/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

public class TypeOfMethod extends TypeOfDependency{
    private String name;

    public String getName() {
        return name;
    }

    public TypeOfMethod(String name) {
        super();
        this.name = name;
    }

    @Override
    public String toString() {
        return "("+name+")";
    }
}
