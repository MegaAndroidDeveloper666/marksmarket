package ru.markstudio.marksmarket.model

import android.content.res.Resources
import android.os.Handler
import io.reactivex.subjects.PublishSubject
import ru.markstudio.marksmarket.R
import ru.markstudio.marksmarket.data.UpdateEvent
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

    fun getDeviceById(id: Int): Device? = privateDeviceList.find { device -> device.id == id }

}