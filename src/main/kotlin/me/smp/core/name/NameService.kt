package me.smp.core.name

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class NameService : KoinComponent {

    private val nameRepository: NameRepository by inject()

    fun getByUUID(uuid: UUID) = nameRepository.getByUUID(uuid)

    fun getByName(name: String) = nameRepository.getByName(name)
}