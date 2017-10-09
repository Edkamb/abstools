/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

public class HasMethodDep extends FlatteningDependency {
    private final MethodDep method;
    private final String element;
    private final boolean elementIsType;
    public HasMethodDep(MethodDep method, String element, boolean elementIsType) {
        super();
        this.method = method;
        method.setAt(this);
        this.element = element;
        this.elementIsType = elementIsType;
    }
    @Override
    public String toString() {
        return element +(elementIsType?"▶":"▷")+method;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        result = prime * result + (elementIsType ? 1231 : 1237);
        result = prime * result + ((method == null) ? 0 : method.hashCode());
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
        HasMethodDep other = (HasMethodDep) obj;
        if (element == null) {
            if (other.element != null)
                return false;
        } else if (!element.equals(other.element))
            return false;
        if (elementIsType != other.elementIsType)
            return false;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        return true;
    }
    
}
