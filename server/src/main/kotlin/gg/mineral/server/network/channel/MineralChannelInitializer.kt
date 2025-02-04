package gg.mineral.server.network.channel

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer

abstract class MineralChannelInitializer : ChannelInitializer<Channel>() {
    public abstract override fun initChannel(channel: Channel)
}