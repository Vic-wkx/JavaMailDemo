package com.example.maildemo

import java.util.*
import javax.activation.DataHandler
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource
import kotlin.concurrent.thread

/**
 * Created by Kevin 2021-09-23
 * JavaMail 发送邮件示例，以 QQ 邮箱为例
 */
object MailSender {

    private const val SMTP = "smtp.qq.com"
    private const val PORT = 587

    private fun connect(): Session {
        val username = MAIL_FROM
        val password = AUTHORIZATION_CODE
        val props = Properties().apply {
            put("mail.smtp.host", SMTP)
            put("mail.smtp.port", PORT.toString())
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }
        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })
        session.debug = true
        return session
    }

    fun send(recipient: String, title: String = "", text: String = "", subType: String = "plain", vararg attachment: Attachment) {
        thread {
            val session = connect()
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(MAIL_FROM))
                setRecipient(Message.RecipientType.TO, InternetAddress(recipient))
                setSubject(title, "UTF-8")
                val multipart = MimeMultipart().apply {
                    addBodyPart(MimeBodyPart().apply {
                        setContent(text, "text/$subType;charset=utf-8")
                    })
                    attachment.forEach {
                        addBodyPart(MimeBodyPart().apply {
                            fileName = it.fileName
                            dataHandler = DataHandler(ByteArrayDataSource(it.inputStream, it.type))
                        })
                    }
                }
                setContent(multipart)
            }
            Transport.send(message)
        }
    }

    fun sendInnerImage(recipient: String, title: String = "", text: String = "", attachment: Attachment) {
        thread {
            val session = connect()
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(MAIL_FROM))
                setRecipient(Message.RecipientType.TO, InternetAddress(recipient))
                setSubject(title, "UTF-8")
                val multipart = MimeMultipart().apply {
                    addBodyPart(MimeBodyPart().apply {
                        setContent(text + "<br><img src=\"cid:${attachment.fileName}/>", "text/html;charset=utf-8")
                    })
                    addBodyPart(MimeBodyPart().apply {
                        fileName = attachment.fileName
                        dataHandler = DataHandler(ByteArrayDataSource(attachment.inputStream, attachment.type))
                        setHeader("Content-ID", "<${attachment.fileName}>")
                    })
                }
                setContent(multipart)
            }
            Transport.send(message)
        }
    }
}