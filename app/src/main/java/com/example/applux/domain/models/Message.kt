package com.example.applux.domain.models

data class Message(
    val messageId: String = "",
    var timestamp: String? = "",
    var text: String? = "",
    var receiverUid: String? = "",
    var senderUID: String? = "",
    var messageType: MessageType? = null,
    var imageLink: String? = null,
    var day: Int = 0,
    var week: String = "",
    var month: Int = 0,
    var year: Int = 0
) {

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        null,
        null,
        0,
        "",
        0,
        0
    )

}
