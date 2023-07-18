package heima.part2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加处理器，pipeline中默认有head和tail两个处理器，添加的处理器都在head和tail之间
                        ch.pipeline().addLast(new StringDecoder());//解码器，将bytebuf转为字符串
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1:{}",msg);
                                super.channelRead(ctx, msg);
                            }
                        });

                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("2:{}",msg);
                                super.channelRead(ctx, msg);
                            }
                        });

                        pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("3:{}",msg);
                                ch.writeAndFlush("222");
                                super.channelRead(ctx, msg);
                            }
                        });

                        //添加出栈处理器,在往channel里面写入数据的时候触发，而且出栈是反过来的，先执行5然后执行4
                        pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4：{}",msg);
                                super.write(ctx, msg, promise);
                            }
                        });

                        pipeline.addLast("h5",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("5：{}",msg);
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                }).bind(8888);
    }
}
