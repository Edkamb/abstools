/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.classtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import abs.frontend.ast.ClassDecl;
import abs.frontend.ast.FieldDecl;
import abs.frontend.ast.InterfaceTypeUse;
import abs.frontend.ast.MethodImpl;
import abs.frontend.ast.MethodSig;

public class ClassEntry {
    private final ArrayList<ElementEntry> elements = new ArrayList<>();
    private final ArrayList<String> implemented = new ArrayList<>(); //TODO: module prefix
    public ClassEntry(ClassDecl cDecl) {
        enhance(cDecl);
    }

    public ClassEntry(String name) {

    }
    

    public boolean isGloballyTypeUniform() {
        HashMap<String, ElementEntry> seen = new HashMap<>();
        for (ElementEntry elementEntry : elements) {
            if(seen.containsKey(elementEntry.id())) {
                if(elementEntry.carryEquals(seen.get(elementEntry.id()))) continue;
                System.out.println("element "+elementEntry.id()+" violates type uniformity!");
                return false;
            }
            seen.put(elementEntry.id(),elementEntry);
        }
        return true;
    }

    public void enhance(ClassDecl cDecl) {
        for (MethodImpl method : cDecl.getMethodList()) enhance(method);
        for (FieldDecl field : cDecl.getFieldList())    enhance(field);
        for (InterfaceTypeUse iface : cDecl.getImplementedInterfaceUseList()) {
            implemented.add(iface.toString());
        }
    }
    @Override
    public String toString() {
        return "interfaces: "+implemented+"(" + elements + ")";
    }
    public ArrayList<ElementEntry> getElements() {
        return elements;
    }
    public ArrayList<String> getImplemented() {
        return implemented;
    }

    public void enhance(MethodImpl method) {
        elements.add(new MethodEntry(method.getMethodSig()));        
    }

    public void enhance(FieldDecl field) {
        elements.add(new FieldEntry(field.getName(), field.getChild(0).toString()));
    }

    public void enhance(MethodSig sig) {
        elements.add(new MethodEntry(sig));   
    }

    
}
