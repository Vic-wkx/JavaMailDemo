package com.example.maildemo

import java.io.InputStream

/**
 * Created by Kevin 2021-09-23
 * 邮件附件/内嵌图片
 */
data class Attachment(val fileName: String, val inputStream: InputStream, val type: String)