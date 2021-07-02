package com.github.coroutineexx

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    private val scope = CoroutineScope(Job())

    lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btnRun).setOnClickListener { onRun() }
        findViewById<View>(R.id.btnCancel).setOnClickListener { onCancel() }

        channelSendAndReceive()
    }

    private fun channelSendAndReceive() {
        val channel = Channel<User>()

        scope.launch {
            delay(1000)
            log("send User")
            channel.send(User.getDefaultUser())
            log("send, done")
        }
        scope.launch {
            delay(300)
            log("receive")
            val element = channel.receive()
            log("receive User: name ${element.name}, age ${element.age}, done")
        }
    }

    private fun onRun() {
        log("onRun, start")
        job = scope.launch {
            log("coroutine, start")

            var x = 0

            while (x < 5 && isActive) {
                delay(1000)
                log("coroutine, ${x++}, isActive = $isActive")
            }
            log("coroutine, end")
        }
        log("onRun, end")
    }

    private fun onCancel() {
        log("onCancel")
        job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        scope.cancel()
    }

    private fun log(text: String) {
        Log.d("TAG", "${formatter.format(Date())} $text [${Thread.currentThread().name}]")
    }
}