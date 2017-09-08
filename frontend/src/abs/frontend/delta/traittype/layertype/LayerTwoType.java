/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.layertype;

import java.util.HashSet;
import java.util.Set;

import abs.frontend.ast.ASTNode;
import abs.frontend.ast.FieldUse;
import abs.frontend.delta.traittype.dependency.FlatteningDependency;

public class LayerTwoType {

    protected Set<FlatteningDependency> dependencies = new HashSet<>();
    protected void traverse(ASTNode<ASTNode> node){
        //System.out.println(node);
        //System.out.println(node.getClass());
        for(int i = 0; i < node.getNumChild(); i++){
            traverse(node.getChild(i));
        }
        if(node instanceof FieldUse){
            dependencies.add(FlatteningDependency.getDep((FieldUse) node));
        }
        /*
        if(node instanceof VarOrFieldDecl){
            VarOrFieldDecl decl = (VarOrFieldDecl) node;
            System.out.println(decl.getType());
        }
        if(node instanceof TypedAnnotation){
            TypedAnnotation decl = (TypedAnnotation) node;
            System.out.println(decl.getType());
        }
        if(node instanceof VarDeclStmt){
            VarDeclStmt decl = (VarDeclStmt) node;
            System.out.println(decl.getType());
        }*/
        //FnApp, Literal, Call, New, NewLoc, Call, Assignstmt, FieldUse
            
    }

}
