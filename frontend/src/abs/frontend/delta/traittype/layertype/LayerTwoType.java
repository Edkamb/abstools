/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.layertype;

import abs.frontend.ast.ASTNode;
import abs.frontend.ast.Call;
import abs.frontend.ast.FieldUse;
import abs.frontend.ast.ThisExp;
import abs.frontend.ast.TypeUse;
import abs.frontend.ast.VarUse;
import abs.frontend.delta.traittype.dependency.DependencyList;
import abs.frontend.delta.traittype.dependency.FieldDep;
import abs.frontend.delta.traittype.dependency.FlatteningDependency;
import abs.frontend.delta.traittype.dependency.MethodDep;
import abs.frontend.delta.traittype.dependency.TypeDep;

public class LayerTwoType {

    protected DependencyList deps = new DependencyList();
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void traverse(ASTNode<ASTNode> node, DependencyList deps){
        //System.out.println(node);
        //System.out.println(node.getClass());
        for(int i = 0; i < node.getNumChild(); i++){
            traverse(node.getChild(i), deps);
        }
        if(node instanceof FieldUse){
            FlatteningDependency dep  = new FieldDep((FieldUse) node);
            deps.add(dep);
        }
        if(node instanceof TypeUse){
            TypeUse st = (TypeUse) node;
            if(st.getType().isUnknownType()){ //do not track dependencies for Unit, Rat etc.
                FlatteningDependency dep  = new TypeDep(st.getName());
                deps.add(dep);
            }
        }
        if(node instanceof Call){
            Call call = (Call)node;
            if(call.getCallee() instanceof ThisExp){
                FlatteningDependency dep = new MethodDep(call.getMethod(), call.getNumParam());
                //deps.add(dep);
            }
            if(call.getCallee() instanceof VarUse){
                VarUse vUse = (VarUse)call.getCallee();
                if(vUse.getDecl() == null){
                  //  System.out.println("dep: "+vUse+" must exist");
                    FlatteningDependency dep = new MethodDep(call.getMethod(), call.getNumParam());
                  //  System.out.println("dep: "+vUse+" must have a type having method "+call.getMethod()+" having "+call.getNumParam()+" parameters");
                }
            }
        }
    }

}
