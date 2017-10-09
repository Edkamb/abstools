/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

public class TypeOfDependency {
    private final String name;
    public static final TypeOfDependency NUMERICAL = new TypeOfDependency("ABS.StdLib.Rat");
    public static final TypeOfDependency STRING = new TypeOfDependency("ABS.StdLib.String");
    public static final TypeOfDependency FUTURE = new TypeOfDependency("ABS.StdLib.Fut<T>");
    protected TypeOfDependency(String name) {
        super();
        this.name = name;
    }
    
    public String toString(){
        return name;
    }
    
    public static TypeOfDependency futureFor(String t){
        return new TypeOfDependency("ABS.StdLib.Fut<"+t+">");
    }

}
