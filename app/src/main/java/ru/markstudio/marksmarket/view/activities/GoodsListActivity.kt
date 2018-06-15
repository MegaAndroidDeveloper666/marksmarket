package ru.markstudio.marksmarket.view.activities

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.widget.Switch
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_goods_list.*
import ru.markstudio.marksmarket.MarketApp
import ru.markstudio.marksmarket.R
import ru.markstudio.marksmarket.model.AppMode
import ru.markstudio.marksmarket.model.UpdateEvent
import ru.markstudio.marksmarket.view.DeviceListAdapter
import ru.markstudio.marksmarket.viewmodel.DeviceListViewModel

class GoodsListActivity : AppCompatActivity() {

    val deviceListViewModel: DeviceListViewModel by lazy { (application as MarketApp).deviceListViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_list)

        setSupportActionBar(toolbarMain)
        supportActionBar?.title = getString(R.string.app_name)

        goodsRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = DeviceListAdapter(
                    applicationContext,
                    deviceListViewModel,
                    { position -> openDeviceInfoActivity(position) }
            )
        }

        fabAdd.setOnClickListener { openDeviceInfoActivity(-1) }
        initFabVisibility()
        deviceListViewModel.modelUpdateMutableLiveData.observe(this, Observer{event -> handleEvent(event)})
    }

    private fun initFabVisibility() {
        if (deviceListViewModel.currentMode == AppMode.EDIT) {
            fabAdd.show()
        } else {
            fabAdd.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        (menu.getItem(0).actionView.findViewById<Switch>(R.id.frontBackSwitch)).also {
            it.setOnCheckedChangeListener(
                    { _, checked ->
                        val mode = if (checked) AppMode.EDIT else AppMode.BUY
                        mode.run {
                            deviceListViewModel.currentMode = this
                            it.text = getString(this.titleSwitchId)
                        }
                        goodsRecyclerView.adapter?.notifyDataSetChanged()
                        initFabVisibility()
                    })
            it.text = getString(deviceListViewModel.currentMode.titleSwitchId)
        }
        return true
    }

    private fun openDeviceInfoActivity(position: Int) {
        val intent = Intent(applicationContext, BuyEditActivity::class.java)
        intent.putExtra(BuyEditActivity.ITEM_POSITION, position)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }

    override fun onResume() {
        goodsRecyclerView.adapter?.notifyDataSetChanged()
        super.onResume()
    }

    private fun handleEvent(updateEvent: UpdateEvent?) {
        Toast.makeText(applicationContext, updateEvent?.message, Toast.LENGTH_SHORT).show()
        if (updateEvent?.success == true){
            goodsRecyclerView.adapter?.notifyDataSetChanged()
        }
    }
}
