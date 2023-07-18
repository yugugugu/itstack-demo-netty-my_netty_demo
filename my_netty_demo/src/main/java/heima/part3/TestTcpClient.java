package heima.part3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

@Slf4j
public class TestTcpClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(worker);
        ChannelFuture connect = bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
                ChannelPipeline pipeline = sc.pipeline();
                pipeline.addLast(new ChannelInboundHandlerAdapter(){
                    //连接服务器成功后触发active事件
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        for (int i = 0; i < 10; i++) {
                            ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
                            //发生粘包现象，不是一次发16个字节，而是一次性发了160个字节
                            ctx.writeAndFlush(buffer);
                            ctx.close();
                        }
                    }
                });
            }
        }).connect("localhost",8080);
        ChannelFuture sync = connect.sync();
        sync.channel().closeFuture().sync();
        worker.shutdownGracefully();

    }
}
