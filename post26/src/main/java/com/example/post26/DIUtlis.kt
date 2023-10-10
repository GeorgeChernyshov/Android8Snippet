package com.example.post26

object DIUtils {
    lateinit var replyRepository: ReplyRepository

    fun init() {
        replyRepository = ReplyRepository()
    }
}