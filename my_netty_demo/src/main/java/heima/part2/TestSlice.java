package heima.part2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.lang.invoke.VarHandle;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i'});
        log(buffer);

        //在切片过程中没有发生数据复制
        ByteBuf f1 = buffer.slice(0, 5);
        ByteBuf f2 = buffer.slice(5, 5);
        log(f1);
        log(f2);

        //我改变f1内容buffer的也会变，证明没有发生数据复制
        f1.setByte(0,'g');
        log(buffer);
        log(f1);

        //释放内存后就不能使用了,但是前面可以用retain进行保留
        buffer.retain();
        buffer.release();
        log(buffer);
    }

    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
