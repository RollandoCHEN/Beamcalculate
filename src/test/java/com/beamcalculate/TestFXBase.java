package com.beamcalculate;

import com.beamcalculate.pages.InputPage;
import com.beamcalculate.pages.MomentPage;
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
    protected InputPage mInputPage;
    protected MomentPage mMomentPage;

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

    protected void setSampleForAllInputs(){
        mInputPage.setAllInputs(false,3, new Double[]{3.2, 3.4, 4.0}, new Double[]{0.2, 0.3, 0.2, 0.2},
                0.4, 0.6, 0,0, 5.4, 6.4,
                25, 500, 'B'
        );
    }
}
