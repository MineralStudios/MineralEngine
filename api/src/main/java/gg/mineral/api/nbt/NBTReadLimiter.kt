package gg.mineral.api.nbt

import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
open class NBTReadLimiter {
    private val limit: Long = 0
    private var currentRead: Long = 0

    open fun read(length: Int) {
        this.currentRead += length.toLong()
        check(this.currentRead <= this.limit) { "Read more than " + this.limit + " bytes from NBT tag" }
    }

    companion object {
        val UNLIMITED: NBTReadLimiter = object : NBTReadLimiter(0L) {
            override fun read(length: Int) {
            }
        }
    }
}
