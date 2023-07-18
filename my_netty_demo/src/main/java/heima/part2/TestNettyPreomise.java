package heima.part2;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyPreomise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        //promise当成一个结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(()->{
            //计算，然后向promise填充结果
           log.debug("开始计算-----");
            try {
                Thread.sleep(10000);
                promise.setSuccess(100);
            } catch (InterruptedException e) {
                promise.setFailure(e);
            }
        }).start();

        log.debug("等待结果----");
        log.debug("结果是：{}",promise.get());
    }
}
