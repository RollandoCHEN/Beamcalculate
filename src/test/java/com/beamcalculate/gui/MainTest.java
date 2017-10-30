package com.beamcalculate.gui;

import com.beamcalculate.Main;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.matcher.base.NodeMatchers;


import static org.testfx.api.FxAssert.verifyThat;

public class MainTest extends FxRobot {


    @Before
    public void setUp() throws Exception  {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }


    @Test
    public void button_should_enabled() {
        clickOn("#numSpans_chcb").clickOn("3");
        clickOn("#equalSupport_chkb").clickOn("#equalSupportWidth_tf").write("0.2");
        clickOn("#equalSpan_chkb").clickOn("#equalSpanLength_tf").write("5.0");
//        clickOn("#sectionWidth_tf").write("0.5");
//        clickOn("#sectionHeight_tf").write("0.6");
        clickOn("#permanentLoad_tf").write("3.4");
        clickOn("#variableLoad_tf").write("5.8");
//        clickOn("#fck_tf").write("25");
//        clickOn("#fyk_tf").write("500");
//        clickOn("#ductibilityClass_chcb").clickOn("B");
        clickOn("#graphGenerate_button");

        // expect:
        verifyThat("#graphGenerate_button", NodeMatchers.isEnabled());
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}
