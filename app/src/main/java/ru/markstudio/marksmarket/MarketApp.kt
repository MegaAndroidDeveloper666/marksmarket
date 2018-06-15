package ru.markstudio.marksmarket

import android.app.Application
import ru.markstudio.marksmarket.model.DeviceRepository
import ru.markstudio.marksmarket.viewmodel.DeviceListViewModel

class MarketApp : Application() {

    private val deviceRepository: DeviceRepository by lazy { DeviceRepository(resources) }
    val deviceListViewModel: DeviceListViewModel by lazy { DeviceListViewModel(deviceRepository) }

}