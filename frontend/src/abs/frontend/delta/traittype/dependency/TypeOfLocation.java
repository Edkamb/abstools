/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

public class TypeOfLocation extends TypeOfDependency {
    private String name;

    public String getName() {
        return name;
    }

    public TypeOfLocation(String name) {
        super();
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
