/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.dependency;

import java.util.HashMap;

import abs.frontend.ast.FieldUse;
import abs.frontend.typechecker.Type;

//TODO: proper factory
public class FlatteningDependency {
    private static HashMap<String, FieldDep> fields = new HashMap<>();
    private static HashMap<String, TypeDep> types = new HashMap<>();
    private static HashMap<String, MethodDep> mets = new HashMap<>();
    
    public static FieldDep getFieldDep(FieldUse use){
        FieldDep ret = fields.get(use.getName());
        if(ret == null){
            ret = new FieldDep(use);
            fields.put(use.getName(), ret);
        }
        return ret;
    }
    

    public static FlatteningDependency getTypeDep(String type) {
        TypeDep ret = types.get(type);
        if(ret == null){
            ret = new TypeDep(type);
            types.put(type, ret);
        }
        return ret;
    }


    public static FlatteningDependency getMethodDep(String method) {
        MethodDep ret = mets.get(method);
        if(ret == null){
            ret = new MethodDep(method);
            mets.put(method, ret);
        }
        return ret;
    }
}
