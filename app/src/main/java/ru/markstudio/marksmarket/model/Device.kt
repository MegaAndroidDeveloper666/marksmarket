package ru.markstudio.marksmarket.model

import java.math.BigDecimal

data class Device (var name: String, var price: BigDecimal, var count: Int, val id: Int)