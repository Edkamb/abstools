/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

public class TraitInnerVarType extends TraitInnerType {
    private final String name;
    private static int counter = 0;

    public TraitInnerVarType() {
        this.name = "α_"+(counter++);
    }

    @Override
    public String toString() {
        return name;
    }
}
