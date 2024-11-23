package gg.mineral.server.util.icon;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import kotlin.text.Charsets;
import lombok.val;

public class IconUtil {

    public static String ICON;

    static {
        val file = new File("server-icon.png");

        if (file.isFile()) {

            val buf = Unpooled.buffer();
            ByteBuf imageBuf = null;

            try {
                val bufferedimage = ImageIO.read(file);

                if (bufferedimage.getWidth() == 64 || bufferedimage.getHeight() == 64) {
                    ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(buf));
                    imageBuf = Base64.encode(buf);

                    ICON = "data:image/png;base64," + imageBuf.toString(Charsets.UTF_8);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (buf != null && buf.refCnt() > 0)
                    buf.release();

                if (imageBuf != null)
                    imageBuf.release();
            }
        }
    }
}
