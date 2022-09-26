package com.smascaro.trackmixing

import com.smascaro.trackmixing.main.view.MainActivityUiTest
import com.smascaro.trackmixing.settingsOld.view.SettingsActivityUiTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainActivityUiTest::class,
    SettingsActivityUiTest::class
)
class ActivityTestsSuite