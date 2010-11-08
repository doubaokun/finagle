package com.twitter.finagle.http

import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.http._

import com.twitter.finagle.channel.PartialUpstreamMessageEvent

private[http] trait RequestLifecycleSpyBehavior <: SimpleChannelUpstreamHandler {
  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    val upstreamMessage =
      e.getMessage match {
        case response: HttpResponse
        if response.isChunked =>
          PartialUpstreamMessageEvent(e)
        case c: HttpChunkTrailer => e
        case c: HttpChunk =>
          PartialUpstreamMessageEvent(e)
        case _ => e
      }

    super.messageReceived(ctx, upstreamMessage)
  }
}

object RequestLifecycleSpy extends SimpleChannelUpstreamHandler
  with RequestLifecycleSpyBehavior
