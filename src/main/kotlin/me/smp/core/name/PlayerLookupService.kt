package me.smp.core.name

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class PlayerLookupService : KoinComponent {

    private val playerLookupRepository: PlayerLookupRepository by inject()

    fun getNameByUUID(uuid: UUID) = playerLookupRepository.getNameByUUID(uuid)

    fun getUUIDByName(name: String) = playerLookupRepository.getUUIDByName(name)

    fun getAddressByUUID(uuid: UUID) = playerLookupRepository.getAddressByUUID(uuid)
}
