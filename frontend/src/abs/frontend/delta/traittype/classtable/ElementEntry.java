/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.classtable;

public abstract class ElementEntry {
    public abstract String id();
    public abstract Object content();
    public boolean carryEquals(Object obj){
        return equals(obj);
    }
}
