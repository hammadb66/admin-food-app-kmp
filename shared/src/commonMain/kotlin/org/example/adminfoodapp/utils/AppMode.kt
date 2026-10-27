package org.example.adminfoodapp.utils

enum class AppMode(
    val label: String,
    val description: String
) {
    LOCAL_ONLY(
        label = "Local Only",
        description = "POS runs fully offline. No Firebase, no online orders."
    ),
    ONLINE_APP(
        label = "Online App",
        description = "Online orders + customer menu enabled via Firebase."
    )
}