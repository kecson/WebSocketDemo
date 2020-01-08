package com.example.websocket

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.websocket.https.HttpsUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var socketClient: JWebSocketClient

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            arrayOf(
                "ws://echo.websocket.org",
                "wss://192.168.42.94:443/websocket",
                "ws://192.168.42.94:8081/websocket",
                "wss://192.168.128.30:8081/websocket",
                "wss://echo.websocket.org"
            )

        )
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                input_addr.setText(spinner.selectedItem.toString())
            }
        }

        input_addr.setText(spinner.selectedItem.toString())
        btn_send.isEnabled = false

        input_msg.setText("{\"method\":\"auth\",\"body\":{\"session\":\"a2c17827\"}}")

        toogle_connect.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                thread {
                    initClient()
                }
            } else {
                socketClient.close()
                tv_text.append("disconnected")
                btn_send.isEnabled = false
            }
        }

        btn_send.setOnClickListener {
            if (input_msg.text.toString().isNotEmpty()) {
                try {
                    socketClient?.send(input_msg.text.toString())

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }

    /**
     * init WebSocket client and ignore certificate validation
     */
    @SuppressLint("SetTextI18n")
    private fun initClient() {
        val address = input_addr.text.toString()
        val uri = URI.create(address)
        socketClient = JWebSocketClient(uri)
        if (uri.scheme == "wss") {
            HttpsUtil.allowAllSSL()
            val newSslSocketFactory = HttpsUtil.newSslSocketFactory(applicationContext)
//            socketClient.setSocketFactory(newSslSocketFactory)
            socketClient.setSocketFactory(newSslSocketFactory)
        }
        socketClient.setSocketListener(object : JWebSocketClient.SocketListener {
            override fun onOpen(serverHandshake: ServerHandshake?) {
                runOnUiThread {
                    serverHandshake?.apply {
                        tv_text.apply {
                            append("onOpen: httpStatus=${serverHandshake.httpStatus}  ${serverHandshake.httpStatusMessage}")
                            append("\n")
                        }
                    }
                }
            }

            override fun onMessage(message: String?) {
                runOnUiThread {
                    tv_text.apply {
                        append("onMessage: $message")
                        append("\n")
                    }
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                runOnUiThread {
                    tv_text.apply {
                        append("onClose: code=$code, reason=$reason, remote=$remote")
                        append("\n")
                    }
                }
            }

            override fun onError(ex: java.lang.Exception?) {
                runOnUiThread {
                    tv_text.apply {
                        append("onError: ${ex.toString()}")
                        append("\n")
                    }
                }
            }
        })
        var isConnected = false
        try {
            Log.w(javaClass.simpleName, "connecting...")
            runOnUiThread {
                tv_text.text = "connecting...$address\n"
            }
            isConnected = socketClient.connectBlocking()
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                tv_text.append("${e}\n")
            }
        } finally {
            Log.w(javaClass.simpleName, "connect : $isConnected")
            runOnUiThread {
                tv_text.append("connected\n")
                if (!isConnected) {
                    toogle_connect.isChecked = false
                }
                btn_send.isEnabled = isConnected
            }
        }
    }
}
