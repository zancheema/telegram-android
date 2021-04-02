package com.zancheema.android.telegram.temporary

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class Repository {
    private val _number = MutableStateFlow(1)
    val number: Flow<String> =
        _number.map { "Number: $it" }

    private val _numbers = MutableStateFlow<List<Int>>(emptyList())
    val numbers: Flow<List<Int>>
        get() = _numbers

    fun setNumber(n: Int) {
        _number.value = n
    }

    fun addNumber(n: Int) {
        val temp = _numbers.value.toMutableList()
        temp.add(n)
        _numbers.value = temp
    }
}