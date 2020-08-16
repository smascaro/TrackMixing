package com.smascaro.trackmixing.common.di

import com.smascaro.trackmixing.common.di.main.MainComponent
import com.smascaro.trackmixing.common.di.player.PlayerComponent
import dagger.Module

@Module(subcomponents = [MainComponent::class, PlayerComponent::class])
class AppSubcomponents