package org.wallentines.midnightcore.fabric.module.messaging;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.wallentines.mdcfg.codec.JSONCodec;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.midnightcore.api.module.messaging.LoginMessageHandler;
import org.wallentines.midnightcore.api.module.messaging.LoginNegotiator;
import org.wallentines.midnightcore.fabric.util.ConversionUtil;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.midnightlib.registry.Identifier;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class FabricLoginNegotiator implements LoginNegotiator {


    private final AtomicInteger transactionId = new AtomicInteger(1000);
    private final HashMap<Integer, TransactionInfo> currentTransactions = new HashMap<>();
    private final ServerLoginPacketListenerImpl packetListener;
    private final Connection connection;
    private final UUID uuid;


    public FabricLoginNegotiator(ServerLoginPacketListenerImpl packetListener, Connection connection, UUID uuid) {
        this.packetListener = packetListener;
        this.connection = connection;
        this.uuid = uuid;
    }

    @Override
    public String getPlayerUsername() {
        return packetListener.getUserName();
    }

    public void sendMessage(Identifier id, FriendlyByteBuf data, LoginMessageHandler response) {

        data.resetReaderIndex();
        TransactionInfo inf = new TransactionInfo(connection, response);
        int tid = transactionId.getAndIncrement();
        currentTransactions.put(tid, inf);

        connection.send(new ClientboundCustomQueryPacket(tid, ConversionUtil.toResourceLocation(id), data));
    }

    @Override
    public void sendMessage(Identifier id, ConfigSection data, LoginMessageHandler response) {

        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeUtf(JSONCodec.minified().encodeToString(ConfigContext.INSTANCE, data));

        sendMessage(id, buffer, response);
    }

    @Override
    public void sendRawMessage(Identifier id, ByteBuf data, LoginMessageHandler response) {

        sendMessage(id, new FriendlyByteBuf(data), response);
    }

    public void handlePacket(ServerboundCustomQueryPacket packet) {

        TransactionInfo info = currentTransactions.get(packet.getTransactionId());
        if(info != null) {

            info.onResponse.handle(packet.getData());
            currentTransactions.remove(packet.getTransactionId());
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public int itemsInQueue() {
        return currentTransactions.size();
    }
    private static class TransactionInfo {
        Connection conn;
        LoginMessageHandler onResponse;

        public TransactionInfo(Connection conn, LoginMessageHandler onResponse) {
            this.conn = conn;
            this.onResponse = onResponse;
        }
    }
}
