package com.smascaro.trackmixing

import com.smascaro.trackmixing.main.view.MainActivityTest
import com.smascaro.trackmixing.settings.view.SettingsActivityTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainActivityTest::class,
    SettingsActivityTest::class
)
class ActivityTestsSuite