package org.sableos.design

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SableDialogPolicyTest {
    private fun b(label: String) = SableDialogButton(label) {}

    @Test
    fun alertKeepsOnlyTheFirstButton() {
        assertEquals(listOf("OK"), SableDialogPolicy.buttonsFor(SableDialogKind.Alert, listOf(b("OK"), b("Cancel"))).map { it.label })
    }

    @Test
    fun informationAndStatusHaveNoButtons() {
        assertTrue(SableDialogPolicy.buttonsFor(SableDialogKind.Information, listOf(b("OK"))).isEmpty())
        assertTrue(SableDialogPolicy.buttonsFor(SableDialogKind.Status, listOf(b("OK"))).isEmpty())
    }

    @Test
    fun inquiryTakesAtMostThreeButtonsInOrder() {
        val labels = listOf("Save", "Discard", "Cancel", "Extra").map(::b)
        assertEquals(listOf("Save", "Discard", "Cancel"), SableDialogPolicy.buttonsFor(SableDialogKind.Inquiry, labels).map { it.label })
    }

    @Test
    fun shortcutIsTheFirstLetterAndNeverRepeats() {
        assertEquals(listOf('s', 'd', 'c', null), SableDialogPolicy.shortcuts(listOf("Save", "Discard", "Cancel", "Close")))
    }

    @Test
    fun yesNoAndPunctuationAreDiscouraged() {
        assertTrue(SableDialogPolicy.isDiscouragedLabel("Yes"))
        assertTrue(SableDialogPolicy.isDiscouragedLabel("Save?"))
        assertFalse(SableDialogPolicy.isDiscouragedLabel("Delete"))
    }
}
