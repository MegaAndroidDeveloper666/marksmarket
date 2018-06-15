package ru.markstudio.marksmarket.model

import ru.markstudio.marksmarket.R

enum class AppMode(val titleDetailsId: Int, val titleSwitchId: Int, val buttonDetailsTextId: Int) {
    BUY(R.string.mode_buy, R.string.store_front, R.string.mode_buy),
    EDIT(R.string.mode_edit, R.string.back_end, R.string.mode_edit_action_button)
}