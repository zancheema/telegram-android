package com.zancheema.android.telegram.temporary

import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@SmallTest
class TempTest {
    private lateinit var repository: Repository

    @Before
    fun setUp() {
        repository = Repository()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testPreconditions() = runBlockingTest {
        assertThat(repository.number.first(), `is`("Number: 1"))
        assertThat(repository.numbers.first(), `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun settingNewNumberChangesItsValue() = runBlockingTest {
        repository.setNumber(3)
        assertThat(repository.number.first(), `is`("Number: 3"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addNumberToNumbersList() = runBlockingTest {
        repository.addNumber(1)
        repository.addNumber(2)

        assertThat(repository.numbers.first(), `is`(listOf(1, 2)))
    }
}