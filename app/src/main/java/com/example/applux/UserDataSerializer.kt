package com.example.applux

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserDataSerializer : Serializer<userData> {


    override suspend fun readFrom(input: InputStream): userData {
        try{
            return userData.parseFrom(input)
        }catch (exception: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: userData, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: userData
        get() = TODO("Not yet implemented")
}