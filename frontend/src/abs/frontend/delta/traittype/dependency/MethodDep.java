/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

public class MethodDep extends FlatteningDependency {

    private final String type;
    MethodDep(String use){
        this.type = use;
    }
    @Override
    public String toString() {
        return type;
    }

}
