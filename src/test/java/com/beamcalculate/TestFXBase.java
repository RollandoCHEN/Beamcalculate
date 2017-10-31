package com.beamcalculate;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.After;
import org.junit.Before;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;


/**
 * Created by Ruolin on 30/10/2017 for Beamcalculate.
 */
public abstract class TestFXBase extends ApplicationTest{

    @Before
    public void setUp() throws Exception  {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(BeamCalculatorApp.class);
    }

    @After
    public void afterEachTest() throws Exception{
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    public <T extends Node> T find (final String query){
        return (T) lookup(query).queryAll().iterator().next();
    }
}
