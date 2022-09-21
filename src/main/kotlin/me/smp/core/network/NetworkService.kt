package me.smp.core.network

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NetworkService : KoinComponent {

    private val networkRepository: NetworkRepository by inject()

    fun publish(obj: Any) = networkRepository.publish(obj)
}