package ru.markstudio.marksmarket.viewmodel

import android.arch.lifecycle.MutableLiveData
import ru.markstudio.marksmarket.data.AppMode
import ru.markstudio.marksmarket.data.UpdateEvent
import ru.markstudio.marksmarket.model.Device
import ru.markstudio.marksmarket.model.DeviceRepository
import java.math.BigDecimal

class DeviceListViewModel(private val deviceRepository: DeviceRepository) {

    init {
        deviceRepository.modelSubject.subscribe(
                {event -> modelUpdateMutableLiveData.value = event }
        )
    }

    var currentMode: AppMode = AppMode.BUY

    val modelUpdateMutableLiveData: MutableLiveData<UpdateEvent> = MutableLiveData()

    fun getDeviceList(): ArrayList<Device> =
            if (currentMode == AppMode.BUY)
                deviceRepository.privateDeviceList.filter { it.count > 0 } as ArrayList<Device>
            else
                deviceRepository.privateDeviceList

    fun getDeviceById(id: Int) = deviceRepository.getDeviceById(id)

    fun buyDevice(id: Int) {
        deviceRepository.buyDevice(id)
    }

    fun addDevice(name: String, price: BigDecimal, count: Int) {
        deviceRepository.addDevice(name, price, count)
    }
}