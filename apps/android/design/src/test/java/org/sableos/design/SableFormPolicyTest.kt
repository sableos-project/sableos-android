package org.sableos.design

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SableFormPolicyTest {
    @Test
    fun emailSpaceTypesAtThenDots() {
        assertEquals('@', SableFormPolicy.spaceInsertion(SableTextFieldKind.Email, "me"))
        assertEquals('.', SableFormPolicy.spaceInsertion(SableTextFieldKind.Email, "me@mail"))
    }

    @Test
    fun webAddressSpaceTypesDotAndTextLeavesTheKey() {
        assertEquals('.', SableFormPolicy.spaceInsertion(SableTextFieldKind.WebAddress, "www"))
        assertNull(SableFormPolicy.spaceInsertion(SableTextFieldKind.Text, "a"))
    }

    @Test
    fun choiceLabelsLoseEndPunctuationAndStartWithACapital() {
        assertEquals("Turn on alerts", SableFormPolicy.choiceLabel(" turn on alerts. "))
    }

    @Test
    fun percentIsClamped() {
        assertEquals(0, SableFormPolicy.percent(-1f))
        assertEquals(42, SableFormPolicy.percent(0.42f))
        assertEquals(100, SableFormPolicy.percent(3f))
    }

    @Test
    fun progressShowsAfterTwoSeconds() {
        assertFalse(SableFormPolicy.showProgress(2000))
        assertTrue(SableFormPolicy.showProgress(2001))
    }
}
