package org.ntu.pcs.telecom.util

import java.util.Base64
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class ByteArrayAdapter : TypeAdapter<ByteArray>() {
    override fun write(out: JsonWriter, value: ByteArray?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(Base64.getEncoder().withoutPadding().encodeToString(value))
        }
    }

    override fun read(input: JsonReader): ByteArray? {
        return if (input.peek() == com.google.gson.stream.JsonToken.NULL) {
            input.nextNull()
            null
        } else {
            Base64.getDecoder().decode(input.nextString())
        }
    }
}
