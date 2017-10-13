/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.classtable;

public class FieldEntry extends ElementEntry {
    private final String name;
    private final String type;
    

    public String id(){return name;}
    public Object content(){return type;}
    public FieldEntry(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        FieldEntry other = (FieldEntry) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "FieldEntry [name=" + name + ", type=" + type + "]";
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    
}
