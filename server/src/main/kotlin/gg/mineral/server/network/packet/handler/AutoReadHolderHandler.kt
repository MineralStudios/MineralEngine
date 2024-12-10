/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gg.mineral.server.network.packet.handler

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.util.ReferenceCountUtil
import java.util.*

/**
 * A variation on [io.netty.handler.flow.FlowControlHandler] that
 * explicitly holds messages
 * on `channelRead` and only releases them on an explicit read operation.
 */
class AutoReadHolderHandler : ChannelDuplexHandler() {
    private val queuedMessages: Queue<Any> = ArrayDeque()

    @Throws(Exception::class)
    override fun read(ctx: ChannelHandlerContext) {
        drainQueuedMessages(ctx)
        ctx.read()
    }

    private fun drainQueuedMessages(ctx: ChannelHandlerContext) {
        if (!queuedMessages.isEmpty()) {
            var queued = queuedMessages.poll()
            while (queued != null) {
                ctx.fireChannelRead(queued)
                queued = queuedMessages.poll()
            }

            ctx.fireChannelReadComplete()
        }
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (ctx.channel().config().isAutoRead) ctx.fireChannelRead(msg)
        else queuedMessages.add(msg)
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        if (ctx.channel().config().isAutoRead) {
            if (!queuedMessages.isEmpty()) {
                this.drainQueuedMessages(ctx) // will also call fireChannelReadComplete()
            } else {
                ctx.fireChannelReadComplete()
            }
        }
    }

    @Throws(Exception::class)
    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        for (message in this.queuedMessages) ReferenceCountUtil.release(message)

        queuedMessages.clear()
    }
}