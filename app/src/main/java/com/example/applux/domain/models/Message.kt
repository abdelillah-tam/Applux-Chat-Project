package com.example.applux.domain.models

data class Message(
    val messageId: String,
    var timestamp: String?,
    var text: String?,
    var senderPhoneOrEmail: String?,
    var senderUID: String?,
    var receiverPhoneOrEmail: String?,
    var receiverUID: String?
) {

    constructor() : this("", "", "", "", "", "", "")

}
