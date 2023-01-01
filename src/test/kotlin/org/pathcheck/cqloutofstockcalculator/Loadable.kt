package org.pathcheck.cqloutofstockcalculator

import java.io.InputStream

open class Loadable {
    fun open(assetName: String): InputStream {
        return javaClass.getResourceAsStream(assetName)!!
    }

    fun load(asset: InputStream): String {
        return asset.bufferedReader().use { bufferReader -> bufferReader.readText() }
    }

    fun load(assetName: String): String {
        return load(open(assetName))
    }
}