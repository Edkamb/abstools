/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

public class DeltaInnerAbsType extends DeltaInnerType {

    private String className;
    
    public DeltaInnerAbsType(String name) {
        className = name;
    }

    public void add(TraitInnerType inType){
    }

    @Override
    public String toString() {
        return className + " : ABS";
    }
}
