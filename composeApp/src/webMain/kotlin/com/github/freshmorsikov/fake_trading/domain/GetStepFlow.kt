package com.github.freshmorsikov.fake_trading.domain

import com.github.freshmorsikov.fake_trading.api.SupabaseApi
import com.github.freshmorsikov.fake_trading.domain.model.Step
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetStepFlow {

    private val supabaseApi = SupabaseApi()

    operator fun invoke(): Flow<Step> {
        return supabaseApi.getStepFlow()
            .map { number ->
                Step(number = number)
            }
    }

}