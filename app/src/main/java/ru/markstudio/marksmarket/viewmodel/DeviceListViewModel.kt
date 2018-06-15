package ru.markstudio.marksmarket.viewmodel

import android.arch.lifecycle.MutableLiveData
import ru.markstudio.marksmarket.model.AppMode
import ru.markstudio.marksmarket.model.UpdateEvent
import ru.markstudio.marksmarket.model.data.Device
import ru.markstudio.marksmarket.model.data.DeviceRepository
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

    fun buyDevice(id: Int) {
        deviceRepository.buyDevice(id)
    }

    fun addDevice(name: String, price: BigDecimal, count: Int) {
        deviceRepository.addDevice(name, price, count)
    }

    fun editDevice(id: Int, name: String, price: BigDecimal, count: Int) {
        deviceRepository.editDevice(id, name, price, count)
    }

    fun deleteDevice(id: Int) {
        deviceRepository.deleteDevice(id)
    }
}