package com.github.freshmorsikov.fake_trading.domain.model

import com.github.freshmorsikov.fake_trading.presentation.model.DAYS_COUNT
import com.github.freshmorsikov.fake_trading.presentation.model.DayTime
import com.github.freshmorsikov.fake_trading.presentation.model.STEP_IN_DAY

data class Step(
    val number: Int,
) {
    val isPreviousStepAvailable = number > 0
    val isNextStepAvailable = number < DAYS_COUNT * STEP_IN_DAY - 1

    val day: Int = number / STEP_IN_DAY + 1
    val dayTime: DayTime = when (number % STEP_IN_DAY) {
        0 -> DayTime.Morning
        1 -> DayTime.Noon
        else -> DayTime.Evening
    }
    val progress: Float = (number + 1f) / (DAYS_COUNT * STEP_IN_DAY)
}