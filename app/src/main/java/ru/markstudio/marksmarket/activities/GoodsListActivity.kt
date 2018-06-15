package ru.markstudio.marksmarket.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.widget.Switch
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_goods_list.*
import ru.markstudio.marksmarket.MarketApp
import ru.markstudio.marksmarket.R
import ru.markstudio.marksmarket.data.AppMode
import ru.markstudio.marksmarket.data.DataHolder
import ru.markstudio.marksmarket.view.DeviceListAdapter
import ru.markstudio.marksmarket.viewmodel.DeviceListViewModel

class GoodsListActivity : AppCompatActivity() {

    lateinit var compositeDisposable: CompositeDisposable
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
                    deviceListViewModel.getDeviceList(),
                    { position -> openDeviceInfoActivity(position) }
            )
        }

        fabAdd.setOnClickListener { openDeviceInfoActivity(-1) }
        initFabVisibility()
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
        compositeDisposable = CompositeDisposable(
                DataHolder.instance.buySubject.subscribe({ buySuccess -> onBuyFinishEvent(buySuccess) }),
                DataHolder.instance.addSubject.subscribe({ _ -> onAddFinishEvent() }),
                DataHolder.instance.deleteSubject.subscribe({ deleteSuccess -> onDeleteFinishEvent(deleteSuccess) }),
                DataHolder.instance.editSubject.subscribe({ editSuccess -> onEditFinishEvent(editSuccess) })
        )
        super.onResume()
    }

    override fun onPause() {
        compositeDisposable.dispose()
        super.onPause()
    }

    fun onBuyFinishEvent(success: Boolean) {
        consumeEvent(success, "Устройство успешно куплено!", "Устройство не куплено, попробуйте еще раз")
    }

    fun onAddFinishEvent() {
        consumeEvent(true, "Устройство успешно добавлено", "")
    }

    fun onDeleteFinishEvent(success: Boolean) {
        consumeEvent(success, "Устройство успешно удалено", "Ошибка, попробуйте еще раз")
    }

    fun onEditFinishEvent(success: Boolean) {
        consumeEvent(success, "Устройство успешно изменено", "Ошибка изменения, попробуйте еще раз")
    }

    fun consumeEvent(success: Boolean, successStr: String, unsuccessStr: String) {
        Toast.makeText(
                applicationContext,
                if (success) successStr else unsuccessStr,
                Toast.LENGTH_SHORT).show()
        goodsRecyclerView.adapter?.notifyDataSetChanged()
    }
}
