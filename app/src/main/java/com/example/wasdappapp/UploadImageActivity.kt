package com.example.wasdappapp

import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import java.io.File
import java.util.*

class UploadImageActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun UploadImageActivity(args : Array<String>) {
        val imagePath = "C:\\base64\\image.png"
        /*
           Encoder File/Image to Base4
         */
        val base64ImageString = encoder(imagePath)
        println("Base64ImageString = $base64ImageString")

        /*
           Decoder Base4 to File/Image
         */
       // decoder(base64ImageString, "C:\\base64\\decoderimage.png")

        println("DONE!")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encoder(filePath: String): String{
        val bytes = File(filePath).readBytes()
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return base64
    }



}
