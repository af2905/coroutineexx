package com.github.coroutineexx

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    private val scope = CoroutineScope(Job())

    lateinit var job: Job

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btnRun).setOnClickListener { onRun() }
        findViewById<View>(R.id.btnCancel).setOnClickListener { onCancel() }

        //channelSendAndReceive()
        //channelSendAndReceiveCapacity()
        //channelSendAndReceiveClose()
        produceChannel()
    }

    private fun channelSendAndReceive() {
        val channel = Channel<User>()

        scope.launch {
            delay(1000)
            log("send User")
            channel.send(User.getDefaultUser())
            channel.send(User("John", 29))
            channel.send(User("Mary", 25))
            channel.close()
            log("send, done")
        }
        scope.launch {
            delay(300)
            log("receive")

            for (element in channel) {
                log("receive User: name ${element.name}, age ${element.age}, done")
            }
        }
    }

    private fun channelSendAndReceiveCapacity() {
        val channel = Channel<Int>(3)

        scope.launch {
            delay(300)
            repeat(9) {
                log("send $it")
                channel.send(it)
            }
        }

        scope.launch {
            log("send 100")
            channel.send(100)
            delay(500)
            log("send 101")
            channel.send(101)
        }

        scope.launch {
            for (element in channel) {
                log("received $element")
                delay(1000)
            }
        }
    }

    private fun channelSendAndReceiveClose() {
        val channel = Channel<Int>()

        scope.launch {
            coroutineContext[Job]?.invokeOnCompletion {
                log("channel closed")
                channel.close()
            }

            launch {
                delay(300)
                repeat(3) {
                    log("send $it")
                    channel.send(it)
                }
            }

            launch {
                delay(500)
                log("send 100")
                channel.send(100)
            }
        }

        scope.launch {
            for (element in channel) {
                log("received $element")
                delay(1000)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun produceChannel() {

        scope.launch {
            val channel = produce {
                delay(300)
                repeat(5) {
                    log("send $it")
                    send(it)
                }
            }

            launch {
                for (element in channel) {
                    log("received $element")
                    delay(1000)
                }
            }
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