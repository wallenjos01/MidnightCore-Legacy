package org.wallentines.midnightcore.api.module.messaging;

import io.netty.buffer.ByteBuf;


public interface LoginMessageHandler {
    void handle(ByteBuf data);

}
