package ru.markstudio.marksmarket.data

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
    }

    private lateinit var privateDeviceList: ArrayList<Device>
    lateinit var currentMode: AppMode

    fun getDeviceList(): ArrayList<Device> {
        if (currentMode == AppMode.BUY) {
            return privateDeviceList.filter {
                it.count > 0
            } as ArrayList<Device>
        }else{
            return privateDeviceList
        }
    }

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

    fun readFromJSON() {}

    fun loadFromWeb() {}

    fun addDevice(name: String, price: BigDecimal, count: Int) {
        privateDeviceList.add(Device(name, price, count, privateDeviceList[privateDeviceList.size - 1].id + 1))
    }

    fun editDevice(itemPosition: Int, name: String, price: BigDecimal, count: Int) {
        getDeviceList()[itemPosition].run {
            this.name = name
            this.price = price
            this.count = count
        }

    }

    fun deleteDevice(itemPosition: Int) {
        privateDeviceList.removeAt(itemPosition)
    }
}