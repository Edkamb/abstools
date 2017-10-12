/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

public class TypeOfLocation extends TypeOfDependency {
    public static final TypeOfLocation SELF = new TypeOfLocation("This");
    public TypeOfLocation(String name) {
        super(name);
    }
}
