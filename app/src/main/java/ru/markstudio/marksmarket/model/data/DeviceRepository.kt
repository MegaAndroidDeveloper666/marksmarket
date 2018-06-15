package ru.markstudio.marksmarket.model.data

import android.content.res.Resources
import android.os.Handler
import io.reactivex.subjects.PublishSubject
import ru.markstudio.marksmarket.R
import ru.markstudio.marksmarket.model.UpdateEvent
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal

class DeviceRepository(val resources: Resources) {

    companion object {
        private val BUY_TIME = 3000L
        private val EDIT_TIME = 5000L
    }

    val modelSubject = PublishSubject.create<UpdateEvent>()

    val privateDeviceList: ArrayList<Device> by lazy {
        getDeviceListFromResources()
    }

    fun getDeviceById(id: Int): Device? = privateDeviceList.find { device -> device.id == id }

    private fun getDeviceListFromResources(): ArrayList<Device> {
        return readFromCSV()
    }

    fun readFromCSV(): ArrayList<Device> {
        val deviceList = ArrayList<Device>()
        val inputStream = resources.openRawResource(R.raw.data)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        try {
            var csvLine = bufferedReader.readLine()
            var id = 0
            while (csvLine != null) {
                val row = removeQuotes(csvLine.split(","))
                deviceList.add(Device(row[0], row[1].toBigDecimal(), row[2].toInt(), id++))
                csvLine = bufferedReader.readLine()
            }
        } catch (e: Exception) {

        } finally {
            inputStream.close()
        }
        return deviceList
    }

    private fun removeQuotes(split: List<String>): List<String> {
        val resultList = ArrayList<String>()
        split.map {
            resultList.add(it.trim('\"', '"', ' '))
        }
        return resultList
    }

    private fun handleAction(time: Long, function: () -> Unit) {
        Handler().postDelayed(function, time)
    }

    fun buyDevice(id: Int) {
        handleAction(BUY_TIME) { handleBuyAction(id) }
    }

    fun addDevice(name: String, price: BigDecimal, count: Int) {
        handleAction(EDIT_TIME) { handleAddAction(name, price, count) }
    }

    fun editDevice(id: Int, name: String, price: BigDecimal, count: Int) {
        handleAction(EDIT_TIME) { handleEditAction(id, name, price, count) }
    }

    fun deleteDevice(id: Int) {
        handleAction(EDIT_TIME) { handleDeleteAction(id) }
    }

    private fun handleBuyAction(id: Int) {
        val success = getDeviceById(id)?.let {
            if (it.count > 0) {
                it.count--
                true
            } else {
                false
            }
        } ?: false
        handleBuyResponse(success)
    }

    private fun handleAddAction(name: String, price: BigDecimal, count: Int) {
        privateDeviceList.add(Device(name, price, count, privateDeviceList.last().id + 1))
        handleAddResponse(true)
    }

    private fun handleEditAction(id: Int, name: String, price: BigDecimal, count: Int) {
        val success = getDeviceById(id)?.let {
            it.name = name
            it.price = price
            it.count = count
            true
        } ?: run {
            false
        }
        handleEditResponse(success)
    }

    private fun handleDeleteAction(id: Int) {
        val success = getDeviceById(id)?.let {
            privateDeviceList.removeAll { device -> device.id == id }
            true
        } ?: run {
            false
        }
        handleDeleteResponse(success)
    }

    private fun handleBuyResponse(success: Boolean) {
        modelSubject.onNext(
                UpdateEvent(
                        success,
                        if (success)
                            "Устройство успешно куплено!"
                        else
                            "Устройство не куплено, попробуйте еще раз"))
    }

    private fun handleAddResponse(success: Boolean) {
        modelSubject.onNext(
                UpdateEvent(
                        success,
                        if (success)
                            "Устройство успешно добавлено!"
                        else
                            ""))
    }

    private fun handleEditResponse(success: Boolean) {
        modelSubject.onNext(
                UpdateEvent(
                        success,
                        if (success)
                            "Устройство успешно изменено!"
                        else
                            "Информация об устройстве не изменена, попробуйте еще раз"))
    }

    private fun handleDeleteResponse(success: Boolean) {
        modelSubject.onNext(
                UpdateEvent(
                        success,
                        if (success)
                            "Устройство успешно удалено!"
                        else
                            "При удалении устройства возникла ошибка, попробуйте еще раз"))
    }

}