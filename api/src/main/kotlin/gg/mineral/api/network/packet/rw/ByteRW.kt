package gg.mineral.api.network.packet.rw

interface ByteRW {
    fun getVarIntSize(value: Int): Int {
        for (position in 1..4) if ((value and (-1 shl position * 7)) == 0) return position
        return 5
    }
}
