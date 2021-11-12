package lark.core.codec;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * @author cuigh
 */
public class JsonCodecTest {
    public static class Message {
        private long id;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime time;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }

    @Test
    public void encodeTest() {
        Message msg = new Message();
        msg.time = LocalDateTime.now();
        String s = JsonCodec.encode(msg);
        System.out.println(s);
        msg = JsonCodec.decode(s, Message.class);
    }
}