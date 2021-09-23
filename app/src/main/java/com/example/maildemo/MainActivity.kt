package com.example.maildemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

// 发件人
const val MAIL_FROM = "发件人 QQ 邮箱"

// 根据网页引导配置授权码：https://service.mail.qq.com/cgi-bin/help?subtype=1&&id=28&&no=1001256
const val AUTHORIZATION_CODE = "你的授权码"

// 收件人
const val MAIL_TO = "收件人邮箱"

/**
 * Created by Kevin 2021-09-23
 */
class MainActivity : AppCompatActivity() {
    private val btnSendText: Button by lazy { findViewById(R.id.btnSendText) }
    private val btnSendHtml: Button by lazy { findViewById(R.id.btnSendHtml) }
    private val btnSendAttachment: Button by lazy { findViewById(R.id.btnSendAttachment) }
    private val btnSendInnerImage: Button by lazy { findViewById(R.id.btnSendInnerImage) }
    private val btnReceive: Button by lazy { findViewById(R.id.btnReceive) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSendText.setOnClickListener {
            MailSender.send(recipient = MAIL_TO, title = "Hello", text = "Hi, Kevin")
        }
        btnSendHtml.setOnClickListener {
            MailSender.send(recipient = MAIL_TO, title = "Hello", text = "<h1>Hi, Kevin</h1>", "html")
        }
        btnSendAttachment.setOnClickListener {
            val docAttachment = Attachment("MyDocument.docx", assets.open("testDocument.docx"), "application/msword")
            val imageAttachment = Attachment("MyImage.png", assets.open("testImage.png"), "application/octet-stream")
            MailSender.send(recipient = MAIL_TO, title = "Hello", text = "Hi, Kevin", attachment = arrayOf(docAttachment, imageAttachment))
        }
        btnSendInnerImage.setOnClickListener {
            val innerImageAttachment = Attachment("MyImage.png", assets.open("testImage.png"), "image/png")
            MailSender.sendInnerImage(recipient = MAIL_TO, title = "Hello", text = "Hi, Kevin", innerImageAttachment)
        }
        btnReceive.setOnClickListener {
            MailReceiver.receive()
        }
    }
}