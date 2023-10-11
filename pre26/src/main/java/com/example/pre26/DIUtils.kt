package com.example.pre26

object DIUtils {
    lateinit var replyRepository: ReplyRepository

    fun init() {
        replyRepository = ReplyRepository()
    }
}