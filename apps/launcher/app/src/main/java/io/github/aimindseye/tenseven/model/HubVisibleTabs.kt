package io.github.aimindseye.tenseven.model

/**
 * The Hub's top-level tabs, as on the approved board: All, Messages, Email, Calls and Social. Unread, Snoozed,
 * Remind, Contacts and Calendar live in Views.
 */
internal object HubVisibleTabs {
    fun items(): List<String> = listOf("All", "Messages", "Email", "Calls", "Social")
}
