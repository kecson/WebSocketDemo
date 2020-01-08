package com.example.websocket

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class JWebSocketClient(serverUri: URI?) : WebSocketClient(serverUri) {
    private var mSocketListener: SocketListener? = null

    interface SocketListener {
        fun onOpen(serverHandshake: ServerHandshake?)
        fun onMessage(message: String?)
        fun onClose(code: Int, reason: String?, remote: Boolean)
        fun onError(ex: Exception?)
    }

    fun setSocketListener(socketListener: SocketListener?) {
        mSocketListener = socketListener
    }

    override fun onOpen(serverHandshake: ServerHandshake) {
        Log.d(
            TAG,
            "onOpen() called with: serverHandshake = [" + serverHandshake.httpStatusMessage + "]"
        )
        if (mSocketListener != null) {
            mSocketListener!!.onOpen(serverHandshake)
        }
    }

    override fun onMessage(message: String) {
        Log.d(
            TAG,
            "onMessage() called with: message = [$message]"
        )
        if (mSocketListener != null) {
            mSocketListener!!.onMessage(message)
        }
    }

    override fun onClose(
        code: Int,
        reason: String,
        remote: Boolean
    ) {
        Log.d(
            TAG,
            "onClose() called with: code = [$code], reason = [$reason], remote = [$remote]"
        )
        if (mSocketListener != null) {
            mSocketListener!!.onClose(code, reason, remote)
        }
    }

    override fun onError(ex: Exception) {
        Log.d(
            TAG,
            "onError() called with: ex = [$ex]"
        )
        if (mSocketListener != null) {
            mSocketListener!!.onError(ex)
        }
    }

    companion object {
        private const val TAG = "JWebSocketClient"
    }
}