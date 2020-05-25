import com.friday.common.protobuf.Message;
import com.friday.common.utils.SnowFlake;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-25:11:31
 */
@Slf4j
public class ClientTo {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;
    private static final int clientNum = 10;
    public static SnowFlake snowFlake = new SnowFlake(1, 2);

    public static void main(String[] args) throws Exception {
        beginTest();
    }

    public static void beginTest() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(10, 10, 15));
                        pipeline.addLast(new ProtobufVarint32FrameDecoder());
                        pipeline.addLast(new ProtobufDecoder(Message.FridayMessage.getDefaultInstance()));
                        // 对protobuf协议的消息头上加上一个长度为32的整形字段
                        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                        pipeline.addLast(new ProtobufEncoder());
                        pipeline.addLast(new ClientToHandler());
                    }
                });
        b.connect(HOST, PORT);
    }

    private static void startConnection(Bootstrap b, int index) {
        b.connect(HOST, PORT).addListener(future -> {
            if (future.isSuccess()) {
                //init registry
                log.info("ClientFrom:{} connected MessageServer Successed...", index);
            } else {
                log.error("ClientFrom:{} connected MessageServer Failed", index);
            }
        });
        b.connect(HOST, PORT);
    }
}
