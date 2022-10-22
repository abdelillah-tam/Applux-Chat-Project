package com.example.applux.domain.models

data class Message(
    val messageId: String,
    var timestamp: String?,
    var text: String?,
    var senderPhone: String?,
    var senderUID: String?,
    var receiverPhone: String?,
    var receiverUID: String?
) {

    constructor() : this("", "", "", "", "", "", "")

}
