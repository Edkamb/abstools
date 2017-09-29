/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.layertype;

public class LayerTwoVarType extends LayerTwoType {
    private final String name;
    private static int counter = 0;

    public LayerTwoVarType() {
        this.name = "β_"+(counter++);
    }

    @Override
    public String toString() {
        return name;
    }

}
