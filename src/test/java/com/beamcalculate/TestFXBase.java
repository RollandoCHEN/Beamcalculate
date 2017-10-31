package com.beamcalculate;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;


/**
 * Created by Ruolin on 30/10/2017 for Beamcalculate.
 */
public abstract class TestFXBase extends ApplicationTest{

    private Stage mPrimaryStage;

    @Before
    public void setUp() throws Exception  {
        mPrimaryStage = FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }

    @After
    public void afterEachTest() throws Exception{
        FxToolkit.cleanupStages();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    public <T extends Node> T find (final String query){
        return (T) lookup(query).queryAll().iterator().next();
    }

    public Stage getPrimaryStage(){
        return mPrimaryStage;
    }
}
