/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.classtable;

import abs.frontend.ast.MethodSig;

public class MethodEntry extends ElementEntry {

    private final MethodSig sig;

    public MethodEntry(MethodSig sig) {
        super();
        this.sig = sig;
    }

    public String id(){return sig.getName();}
    public Object content(){return sig;}

    public MethodSig getSig() {
        return sig;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sig == null) ? 0 : sig.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodEntry other = (MethodEntry) obj;
        if (sig == null) {
            if (other.sig != null)
                return false;
        } else if (!sig.equals(other.sig))
            return false;
        return true;
    }
    
    public boolean carryEquals(Object obj){
        if(!(obj instanceof MethodEntry)) return false;
        MethodEntry other = (MethodEntry) obj;
        return sig.toString().equals(other.sig.toString());
    }

    @Override
    public String toString() {
        return "MethodEntry [sig=" + sig + "]";
    }
    
}
