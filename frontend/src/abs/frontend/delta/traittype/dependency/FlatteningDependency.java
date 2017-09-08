/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

import java.util.HashMap;

import abs.frontend.ast.FieldUse;

//TODO: proper factory
public class FlatteningDependency {
    private static HashMap<String, FieldDep> fields = new HashMap<>();
    public static FieldDep getDep(FieldUse use){
        FieldDep ret = fields.get(use.getName());
        if(ret == null){
            ret = new FieldDep(use);
            fields.put(use.getName(), ret);
        }
        return ret;
    }
}
