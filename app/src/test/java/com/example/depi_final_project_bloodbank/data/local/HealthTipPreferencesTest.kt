package com.example.depi_final_project_bloodbank.data.local

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HealthTipPreferencesTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setup() {
        HealthTipPreferences.resetForTesting()

        prefs = mockk(relaxed = true)
        editor = mockk(relaxed = true)
        context = mockk()

        every { context.applicationContext } returns context
        every { context.getSharedPreferences("settings", Context.MODE_PRIVATE) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putInt(any(), any()) } returns editor
    }

    @After
    fun tearDown() {
        HealthTipPreferences.resetForTesting()
    }

    @Test
    fun `wraps around to zero when last persisted index is the final tip`() {
        every { prefs.getInt("health_tip_index", -1) } returns 5

        val index = HealthTipPreferences.getTipIndex(context, tipCount = 6)

        assertEquals(0, index)
        verify { editor.putInt("health_tip_index", 0) }
    }

    @Test
    fun `advances to next index after a persisted value`() {
        every { prefs.getInt("health_tip_index", -1) } returns 2

        val index = HealthTipPreferences.getTipIndex(context, tipCount = 6)

        assertEquals(3, index)
        verify { editor.putInt("health_tip_index", 3) }
    }

    @Test
    fun `starts at zero when nothing is persisted yet`() {
        every { prefs.getInt("health_tip_index", -1) } returns -1

        val index = HealthTipPreferences.getTipIndex(context, tipCount = 6)

        assertEquals(0, index)
    }

    @Test
    fun `returns cached value without re-reading prefs on repeat calls in the same process`() {
        every { prefs.getInt("health_tip_index", -1) } returns 1

        val first = HealthTipPreferences.getTipIndex(context, tipCount = 6)
        val second = HealthTipPreferences.getTipIndex(context, tipCount = 6)

        assertEquals(first, second)
        verify(exactly = 1) { prefs.getInt("health_tip_index", -1) }
        verify(exactly = 1) { editor.putInt(any(), any()) }
    }
}