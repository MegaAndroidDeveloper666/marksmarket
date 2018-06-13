package ru.markstudio.marksmarket.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.widget.Switch
import kotlinx.android.synthetic.main.activity_goods_list.*
import ru.markstudio.marksmarket.R
import ru.markstudio.marksmarket.data.AppMode
import ru.markstudio.marksmarket.data.DataHolder
import ru.markstudio.marksmarket.view.DeviceListAdapter

class GoodsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_list)

        setSupportActionBar(toolbarMain)
        supportActionBar?.title = getString(R.string.app_name)

        goodsRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = DeviceListAdapter(applicationContext).apply {
                onItemClickListener = { position -> openDeviceInfoActivity(position) }
            }
        }

        fabAdd.setOnClickListener { openDeviceInfoActivity(-1) }
        initFabVisibility()
    }

    private fun initFabVisibility() {
        if (DataHolder.instance.currentMode == AppMode.EDIT) {
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
                            DataHolder.instance.currentMode = this
                            it.text = getString(this.titleSwitchId)
                        }
                        goodsRecyclerView.adapter?.notifyDataSetChanged()
                        initFabVisibility()
                    })
            it.text = getString(DataHolder.instance.currentMode.titleSwitchId)
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
}
