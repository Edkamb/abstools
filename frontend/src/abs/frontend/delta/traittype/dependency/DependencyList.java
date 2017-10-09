/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

import java.util.LinkedHashSet;

public class DependencyList {
    @Override
    public String toString() {
        return deps.toString();
    }

    private final LinkedHashSet<FlatteningDependency> deps = new LinkedHashSet<>();
    
    public LinkedHashSet<FlatteningDependency> getDeps(){
        return deps;
    }
    
    public void add(FlatteningDependency dep){
        deps.add(dep);
    }
}
