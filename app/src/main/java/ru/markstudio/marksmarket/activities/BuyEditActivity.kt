package ru.markstudio.marksmarket.activities

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_buy_edit.*
import ru.markstudio.marksmarket.MarketApp
import ru.markstudio.marksmarket.R
import ru.markstudio.marksmarket.data.AppMode
import ru.markstudio.marksmarket.data.DataHolder
import ru.markstudio.marksmarket.model.Device
import ru.markstudio.marksmarket.viewmodel.DeviceListViewModel
import java.math.RoundingMode

class BuyEditActivity : AppCompatActivity() {

    val deviceListViewModel: DeviceListViewModel by lazy { (application as MarketApp).deviceListViewModel }
    lateinit var device: Device

    companion object {
        val ITEM_POSITION = "ItemPosition"
    }

    var itemPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_edit)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(deviceListViewModel.currentMode.titleDetailsId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener { onBackPressed() }

        itemPosition = intent.getIntExtra(ITEM_POSITION, -1)
        if (itemPosition != -1) {
            device = deviceListViewModel.getDeviceList()[itemPosition]
            deviceDescriptionTextView.setText(device.name)
            devicePriceTextView.setText("${device.price.setScale(2, RoundingMode.HALF_UP)}")
            deviceAvailabilityTextView.setText("${device.count}")
            deviceImage.setImageResource(R.drawable.placeholder_big)
        } else { //Новая штука
            deviceDescriptionTextView.setText("")
            devicePriceTextView.setText("")
            deviceAvailabilityTextView.setText("")
            deviceImage.setImageResource(R.drawable.placeholder_big)
        }

        deviceListViewModel.currentMode.run {
            actionButton.text = getString(this.buttonDetailsTextId)
            val isEditable = this == AppMode.EDIT
            deviceDescriptionTextView.isEnabled = isEditable
            devicePriceTextView.isEnabled = isEditable
            deviceAvailabilityTextView.isEnabled = isEditable
            if (!isEditable) {
                deviceDescriptionTextView.setBackgroundColor(resources.getColor(android.R.color.transparent))
                devicePriceTextView.setBackgroundColor(resources.getColor(android.R.color.transparent))
                deviceAvailabilityTextView.setBackgroundColor(resources.getColor(android.R.color.transparent))
            }
            title = getString(this.titleDetailsId)
        }

        actionButton.setOnClickListener {
            if (deviceListViewModel.currentMode == AppMode.EDIT) {
                if (itemPosition == -1) {
                    deviceListViewModel.addDevice(
                            deviceDescriptionTextView.text.toString(),
                            devicePriceTextView.text.toString().toBigDecimal(),
                            deviceAvailabilityTextView.text.toString().toInt()
                    )
                } else {
                    DataHolder.instance.editDevice(
                            device.id,
                            deviceDescriptionTextView.text.toString(),
                            devicePriceTextView.text.toString().toBigDecimal(),
                            deviceAvailabilityTextView.text.toString().toInt())
                }
            } else {
                deviceListViewModel.buyDevice(device.id)
            }
            finish()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (deviceListViewModel.currentMode == AppMode.EDIT && itemPosition != -1) {
            menuInflater.inflate(R.menu.edit_menu, menu)
            return true
        } else {
            return super.onPrepareOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            AlertDialog.Builder(this@BuyEditActivity, R.style.DialogTheme).apply {
                setMessage("Вы действительно хотите удалить товар из списка?")
                setPositiveButton(
                        "Да",
                        { _, _ ->
                            run {
                                DataHolder.instance.deleteDevice(device.id)
                                finish()
                            }
                        })
                setNegativeButton(
                        "Нет",
                        { _, _ -> })
            }.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back)
    }
}
