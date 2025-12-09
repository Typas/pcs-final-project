package org.ntu.pcs.telecom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ntu.pcs.telecom.database.AppDatabase
import org.ntu.pcs.telecom.database.VerificationDao

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            if (messages != null && context != null) {
                val db = AppDatabase.getDatabase(context)
                val verificationDao = db.verificationDao()
                val client = HttpClient(CIO)
                val smsSender: (String, String) -> Unit = { phone, msg ->
                    val smsManager = context.getSystemService(SmsManager::class.java)
                    smsManager.sendTextMessage(phone, null, msg, null, null)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    for (smsMessage in messages) {
                        val senderNum = smsMessage.displayOriginatingAddress
                        val messageBody = smsMessage.messageBody
                        processSms(senderNum, messageBody, verificationDao, client, smsSender)
                    }
                    client.close()
                }
            }
        }
    }
}

suspend fun processSms(
    senderNum: String,
    messageBody: String,
    verificationDao: VerificationDao,
    client: HttpClient,
    smsSender: (String, String) -> Unit
) {
    val tag = "SmsReceiver"
    Log.d(tag, "SMS received from: $senderNum, Message: $messageBody")
    val receivedHash = messageBody.trim()

    val record = verificationDao.getRecordByHashAndPhone(receivedHash, senderNum)

    if (record != null) {
        Log.d(tag, "Hash and phone match found. Deleting record.")
        verificationDao.deleteByHash(receivedHash)
        try {
            client.post("http://${record.source}:8081/notify") {
                setBody("{\"hash\":\"${record.hash}\", \"status\":\"accepted\"}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to send acceptance to source: ${e.message}")
        }
    } else {
        Log.w(tag, "No matching hash and phone found. Sending warning.")
        val recordByHash = verificationDao.getRecordByHash(receivedHash)
        if (recordByHash != null) {
            try {
                client.post("http://${recordByHash.source}:8081/notify") {
                    setBody("{\"hash\":\"${recordByHash.hash}\", \"status\":\"rejected\", \"reason\":\"phone number mismatch\"}")
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to send warning to source: ${e.message}")
            }
            smsSender(recordByHash.phone, "Warning: A message with your verification hash was received from a different phone number.")
        } else {
            Log.w(tag, "Received hash does not exist in the database.")
        }
    }
}
