package group;

import com.friday.common.bean.token.Token;
import com.friday.common.protobuf.Message.*;
import com.friday.common.utils.JsonHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import single.ClientFrom;

import java.io.IOException;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-31:10:57
 */
@Slf4j
@Component
public class Group1Handler extends SimpleChannelInboundHandler<FridayMessage> {

    private ChannelHandlerContext messageConnectionCtx;

    private String uid = "invitee1";
    private String secret = "123456789";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        messageConnectionCtx = ctx;
        sendLogin(ctx, uid);
    }

    private void sendLogin(ChannelHandlerContext ctx, String uid) {
        String token = new Token(uid, secret).getToken(secret);
        log.info("token{}" + token);
        Login login = Login.newBuilder()
                .setUid(uid)
                .setToken(token)
                .setId(ClientFrom.snowFlake.nextId())
                .build();
        FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.Login).setLogin(login)
                .build();
        ctx.writeAndFlush(ravenMessage);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FridayMessage message) throws Exception {
        if (message.getType() == FridayMessage.Type.LoginAck) {
            LoginAck loginAck = message.getLoginAck();
            log.info("login ack:{}", loginAck.toString());
            if (loginAck.getCode() == Code.SUCCESS) {
                log.info("login success. {} waiting for messages", uid);
            }
        } else if (message.getType() == FridayMessage.Type.MessageAck) {
            MessageAck messageAck = message.getMessageAck();
            log.info("receive message ack:{}", JsonHelper.toJsonString(messageAck));
        } else if (message.getType() == FridayMessage.Type.UpDownMessage) {
            UpDownMessage upDownMessage = message.getUpDownMessage();
            log.info("receive down message:{}", JsonHelper.toJsonString(upDownMessage));
            MessageContent content = MessageContent.newBuilder()
                    .setId(GroupOwner.snowFlake.nextId())
                    .setUid(uid)
                    .setTime(System.currentTimeMillis())
                    .setType(MessageType.TEXT)
                    .setContent("hello world.")
                    .build();
            UpDownMessage msg = UpDownMessage.newBuilder()
                    .setCid(GroupOwner.snowFlake.nextId())
                    .setFromUid(uid)
                    .setConverId(upDownMessage.getConverId())
                    .setConverType(ConverType.GROUP)
                    .setContent(content)
                    .build();
            FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.UpDownMessage)
                    .setUpDownMessage(msg).build();
            ctx.writeAndFlush(ravenMessage);
        } else if (message.getType() == FridayMessage.Type.HeartBeat) {
            HeartBeat heartBeat = message.getHeartBeat();
            log.info("receive hearbeat :{}", JsonHelper.toJsonString(heartBeat));
            if (heartBeat.getHeartBeatType() == HeartBeatType.PING) {
                HeartBeat heartBeatAck = HeartBeat.newBuilder()
                        .setId(heartBeat.getId())
                        .setHeartBeatType(HeartBeatType.PONG)
                        .build();
                FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.HeartBeat)
                        .setHeartBeat(heartBeatAck).build();
                ctx.writeAndFlush(ravenMessage);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if ("Connection reset by peer".equals(cause.getMessage())) {
            return;
        }
        log.error(cause.getMessage(), cause);
    }
}
