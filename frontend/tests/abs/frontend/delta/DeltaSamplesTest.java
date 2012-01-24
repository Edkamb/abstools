/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import abs.backend.maude.MaudeTests;
import abs.frontend.FrontendTest;
import abs.frontend.analyser.SemanticErrorList;
import abs.frontend.ast.Model;

public class DeltaSamplesTest extends FrontendTest {

    @Test
    public void test_P2P_P1() throws Exception {
        Model m = assertTypeCheckFileOk("tests/abssamples/deltas/PeerToPeer.abs", true);
        m.setNullPrintStream();
        m.flattenForProduct("PeerToPeer.P1");
    }

    @Test
    public void test_P2P_P2() throws Exception {
        Model m = assertTypeCheckFileOk("tests/abssamples/deltas/PeerToPeer.abs", true);
        m.setNullPrintStream();
        m.flattenForProduct("PeerToPeer.P2");
    }

    @Test
    public void test_P2P_P3() throws Exception {
        Model m = assertTypeCheckFileOk("tests/abssamples/deltas/PeerToPeer.abs", true);
        m.setNullPrintStream();
        m.flattenForProduct("PeerToPeer.P3");
    }

    @Test
    public void test_P2P_P4() throws Exception {
        Model m = assertTypeCheckFileOk("tests/abssamples/deltas/PeerToPeer.abs", true);
        m.setNullPrintStream();
        m.flattenForProduct("PeerToPeer.P4");
    }

    @Test
    public void test_ticket280() throws Exception {
        Model m = assertTypeCheckFileOk("tests/abssamples/deltas/bug280.abs", true);
        m.setNullPrintStream();
        m.flattenForProduct("D.P");
        /* Run Maude gen (from the ticket), although it's not relevant. */
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.generateMaude(new PrintStream(out), null, 100, 0);
    }
    
    @Test
    public void test_ticket329() throws Exception {
        Model m = assertTypeCheckFileOk("tests/abssamples/deltas/bug329.abs", true);
        SemanticErrorList errs = m.typeCheck();
        /* We are expecting a missing delta in product M.PL: */
        Assert.assertFalse(errs.isEmpty());
    }
}
