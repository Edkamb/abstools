/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.layertype;

import java.util.HashSet;
import java.util.Set;

import abs.frontend.ast.ASTNode;
import abs.frontend.ast.Call;
import abs.frontend.ast.DataTypeUse;
import abs.frontend.ast.FieldUse;
import abs.frontend.ast.ThisExp;
import abs.frontend.ast.TypeUse;
import abs.frontend.ast.UnresolvedTypeUse;
import abs.frontend.ast.VarDecl;
import abs.frontend.ast.VarDeclStmt;
import abs.frontend.ast.VarUse;
import abs.frontend.delta.traittype.dependency.FlatteningDependency;
import abs.frontend.delta.traittype.dependency.SubTypeDep;
import abs.frontend.delta.traittype.dependency.TypeOfLocation;
import abs.frontend.delta.traittype.dependency.TypeOfMethod;

public class LayerTwoType {

    protected Set<FlatteningDependency> dependencies = new HashSet<>();
    protected void traverse(ASTNode<ASTNode> node){
        //System.out.println(node);
        //System.out.println(node.getClass());
        for(int i = 0; i < node.getNumChild(); i++){
            traverse(node.getChild(i));
        }
        if(node instanceof FieldUse){
            dependencies.add(FlatteningDependency.getFieldDep((FieldUse) node));
        }
        if(node instanceof TypeUse){
            TypeUse st = (TypeUse) node;
            if(st.getType().isUnknownType()) //do not track dependencies for Unit, Rat etc.
                dependencies.add(FlatteningDependency.getTypeDep(st.getName()));
        }
        if(node instanceof Call){
            Call call = (Call)node;
            if(call.getCallee() instanceof ThisExp){
                dependencies.add(FlatteningDependency.getMethodDep(call.getMethod()));
            }
            if(call.getCallee() instanceof VarUse){
                VarUse vUse = (VarUse)call.getCallee();
                if(vUse.getDecl().getChild(0) instanceof UnresolvedTypeUse){
                    UnresolvedTypeUse use = (UnresolvedTypeUse)vUse.getDecl().getChild(0);
                    System.out.println(use);
                    TypeOfLocation locT = new TypeOfLocation(use.getName());
                    TypeOfMethod metT = new TypeOfMethod(call.getMethod());
                    dependencies.add(new SubTypeDep(locT, metT));
                }
            }
            if(call.getCallee() instanceof FieldUse){
                FieldUse vUse = (FieldUse)call.getCallee();
                TypeOfLocation locT = new TypeOfLocation(vUse.getName());
                TypeOfMethod metT = new TypeOfMethod(call.getMethod());
                dependencies.add(new SubTypeDep(locT, metT));
            }
        }
    }

}
