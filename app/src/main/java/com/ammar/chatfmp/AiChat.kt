package com.ammar.chatfmp

data class AiChat(
    var message: String = "",
    var sentBy: String = ""
) {
    companion object {
        const val SENT_BY_ME = "Me"
        const val SENT_BY_GPT = "GPT"
    }
}
