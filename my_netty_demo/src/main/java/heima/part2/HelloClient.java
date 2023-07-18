package heima.part2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                //添加eventloop
                .group(group)
                //选额客户端channel实现
                .channel(NioSocketChannel.class)
                //添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new StringEncoder());
                    }
                })
                //链接服务器
                .connect(new InetSocketAddress("localhost", 8888))
                .sync()//同步方法会阻塞，等连接建立后才会往下面执行
                .channel();

        //从创建线程处理控制台的输入发给服务器，q则退出服务
        new Thread(()->{
            Scanner sc = new Scanner(System.in);
            while (true){
                String line = sc.nextLine();
                if ("q".equals(line)){
                    channel.close();//这个close方法是异步操作，所以关闭连接之后的操作不能在这后一句执行
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();

        //关闭连接之后的操作需要获得ColosedFutrue对象，同步处理关闭之后的操作
        ChannelFuture closedFure = channel.closeFuture();
        closedFure.sync();
        log.warn("执行关闭连接之后的操作");
        group.shutdownGracefully();
    }
}
