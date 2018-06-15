package ru.markstudio.marksmarket.data

import android.os.Handler
import io.reactivex.subjects.PublishSubject
import ru.markstudio.marksmarket.model.Device
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal

class DataHolder private constructor() {
    private object Holder {
        val INSTANCE = DataHolder()
    }

    companion object {
        val instance: DataHolder by lazy { Holder.INSTANCE }
        private val BUY_TIME = 3000L
        private val EDIT_TIME = 5000L
    }

    private lateinit var privateDeviceList: ArrayList<Device>
    lateinit var currentMode: AppMode
    val buySubject = PublishSubject.create<Boolean>()
    val editSubject = PublishSubject.create<Boolean>()
    val addSubject = PublishSubject.create<Unit>()
    val deleteSubject = PublishSubject.create<Boolean>()

    fun getDeviceList(): ArrayList<Device> {
        if (currentMode == AppMode.BUY) {
            return privateDeviceList.filter {
                it.count > 0
            } as ArrayList<Device>
        } else {
            return privateDeviceList
        }
    }

    private fun getDeviceById(id: Int): Device? {
        return privateDeviceList.find { device -> device.id == id }
    }

    fun readFromJSON() {}

    fun loadFromWeb() {}

    fun readFromCSV(inputStream: InputStream) {
        privateDeviceList = ArrayList()
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        try {
            var csvLine = bufferedReader.readLine()
            var id = 0
            while (csvLine != null) {
                val row = removeQuotes(csvLine.split(","))
                privateDeviceList.add(Device(row[0], row[1].toBigDecimal(), row[2].toInt(), id++))
                csvLine = bufferedReader.readLine()
            }
        } catch (e: Exception) {

        } finally {
            inputStream.close()
        }
    }

    private fun removeQuotes(split: List<String>): List<String> {
        val resultList = ArrayList<String>()
        split.map {
            resultList.add(it.trim('\"', '"', ' '))
        }
        return resultList
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

    private fun handleAction(time: Long, function: () -> Unit) {
        Handler().postDelayed(function, time)
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
        buySubject.onNext(success)
    }

    private fun handleAddAction(name: String, price: BigDecimal, count: Int) {
        privateDeviceList.add(Device(name, price, count, privateDeviceList.last().id + 1))
        addSubject.onNext(Unit)
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
        editSubject.onNext(success)
    }

    private fun handleDeleteAction(id: Int) {
        val success = getDeviceById(id)?.let {
            privateDeviceList.removeAll { device -> device.id == id }
            true
        } ?: run {
            false
        }
        deleteSubject.onNext(success)
    }
}