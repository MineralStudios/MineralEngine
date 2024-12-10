package gg.mineral.api.nbt

open class NBTReadLimiter(val limit: Long) {
    private var currentRead: Long = 0

    open fun read(length: Int) {
        this.currentRead += length.toLong()
        check(this.currentRead <= this.limit) { "Read more than " + this.limit + " bytes from NBT tag" }
    }

    companion object {
        @JvmField
        val UNLIMITED: NBTReadLimiter = object : NBTReadLimiter(0L) {
            override fun read(length: Int) {}
        }
    }
}
