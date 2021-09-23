package com.example.maildemo

import android.util.Log
import com.sun.mail.pop3.POP3SSLStore
import java.lang.StringBuilder
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimeUtility
import kotlin.concurrent.thread

/**
 * Created by Kevin 2021-09-23
 * JavaMail 收邮件示例，以 QQ 邮箱为例
 */
object MailReceiver {
    private const val HOST = "pop.qq.com"
    private const val PORT = 995
    private fun connect(): Store {
        val prop = Properties().apply {
            setProperty("mail.store.protocol", "pop3")
            setProperty("mail.pop3.host", HOST)
            setProperty("mail.pop3.port", PORT.toString())
            setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            setProperty("mail.smtp.socketFactory.port", PORT.toString())
        }
        val url = URLName("pop3", HOST, PORT, "", MAIL_FROM, AUTHORIZATION_CODE)
        val session = Session.getInstance(prop)
        session.debug = true
        val store = POP3SSLStore(session, url)
        store.connect()
        return store
    }

    fun receive() {
        thread {
            val store = connect()
            val folder = store.getFolder("INBOX")
            folder.open(Folder.READ_WRITE)
            Log.d("~~~", "Total messages: ${folder.messageCount}, New Messages: ${folder.newMessageCount}, Unread Messages: ${folder.unreadMessageCount}")
            folder.messages.forEach {
                printMessage(it)
            }
            folder.close(true)
            store.close()
        }
    }

    private fun printMessage(message: Message) {
        val subject = MimeUtility.decodeText(message.subject)
        val from = message.from
        val address = from.first() as InternetAddress
        val personal = address.personal
        val body = getBody(message.content)
        Log.d(
            "~~~", "Subject: $subject, " +
                    "From: ${if (personal.isNullOrEmpty()) "" else MimeUtility.decodeText(personal)}<${address.address}>, " +
                    "To: ${Arrays.toString(message.allRecipients)}, " +
                    "Body: $body"
        )
    }

    private fun getBody(part: Any?): String? {
        part ?: return ""
        when (part) {
            is String -> {
                return part
            }
            is MimeMultipart -> {
                val parts = StringBuilder()
                for (i in 0 until part.count) {
                    parts.appendLine(getBody(part.getBodyPart(i)))
                }
                return parts.toString()
            }
            is MimeBodyPart -> {
                return "Attachment: " + part.fileName?.toString()
            }
            else -> Log.d("~~~", "Unsupported type: $part")
        }
        return ""
    }

}