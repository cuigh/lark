package lark.core.data;

import lark.core.util.Strings;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ported from xid(https://github.com/rs/xid)
 *
 * @author cuigh
 */
public class Guid {
    private static final int STRING_LENGTH = 20;
    private static final int BINARY_LENGTH = 12;
    private static final byte[] ENCODING_BYTES = "0123456789abcdefghijklmnopqrstuv".getBytes();
    private static final byte[] DECODING_BYTES = new byte[256];
    private static final byte[] MACHINE_ID = buildMachineId();
    private static final int PID = buildProcessId();
    private static final AtomicInteger COUNTER = buildCounter();

    static {
        Arrays.fill(DECODING_BYTES, (byte) 0xFF);
        for (int i = 0; i < ENCODING_BYTES.length; i++) {
            DECODING_BYTES[ENCODING_BYTES[i]] = (byte) i;
        }
    }

    private final byte[] id;

    public Guid() {
        this(new Date());
    }

    public Guid(Date time) {
        id = new byte[BINARY_LENGTH];

        // Timestamp, 4 bytes, big endian
        long ts = time.getTime() / 1000;
        id[0] = (byte) ((ts >> 24) & 0xFF);
        id[1] = (byte) ((ts >> 16) & 0xFF);
        id[2] = (byte) ((ts >> 8) & 0xFF);
        id[3] = (byte) (ts & 0xFF);

        // Machine, first 3 bytes of md5(hostname)
        id[4] = MACHINE_ID[0];
        id[5] = MACHINE_ID[1];
        id[6] = MACHINE_ID[2];

        // Pid, 2 bytes, specs don't specify endianness, but we use big endian.
        id[7] = (byte) (PID >> 8);
        id[8] = (byte) PID;

        // Increment, 3 bytes, big endian
        int i = COUNTER.incrementAndGet();
        id[9] = (byte) (i >> 16);
        id[10] = (byte) (i >> 8);
        id[11] = (byte) i;
    }

    private Guid(byte[] id) {
        this.id = id;
    }

    private static byte[] buildMachineId() {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            if (!Strings.isEmpty(hostName)) {
                byte[] bytes = DigestUtils.md5Digest(hostName.getBytes(StandardCharsets.UTF_8));
                return Arrays.copyOf(bytes, 3);
            }
        } catch (UnknownHostException e) {
            // Ignore exception
        }

        byte[] id = new byte[3];
        new Random().nextBytes(id);
        return id;
    }

    private static AtomicInteger buildCounter() {
        byte[] bytes = new byte[3];
        new Random().nextBytes(bytes);
        int start = ((int) bytes[0]) << 16 | ((int) bytes[1]) << 8 | ((int) bytes[2]);
        return new AtomicInteger(Math.abs(start));
    }

    private static int buildProcessId() {
        try {
            // likely works on most platforms
            String id = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            return Integer.parseInt(id);
        } catch (final Exception e) {
            try {
                // try a Linux-specific way
                String name = new File("/proc/self").getCanonicalFile().getName();
                return Integer.parseInt(name);
            } catch (final IOException ignoredUseDefault) {
                // Ignore exception.
            }
        }
        return 0;
    }

    public static Guid parse(String s) {
        byte[] src = s.getBytes();
        byte[] id = new byte[BINARY_LENGTH];
        id[0] = (byte) (DECODING_BYTES[src[0]] << 3 | DECODING_BYTES[src[1]] >> 2);
        id[1] = (byte) (DECODING_BYTES[src[1]] << 6 | DECODING_BYTES[src[2]] << 1 | DECODING_BYTES[src[3]] >> 4);
        id[2] = (byte) (DECODING_BYTES[src[3]] << 4 | DECODING_BYTES[src[4]] >> 1);
        id[3] = (byte) (DECODING_BYTES[src[4]] << 7 | DECODING_BYTES[src[5]] << 2 | DECODING_BYTES[src[6]] >> 3);
        id[4] = (byte) (DECODING_BYTES[src[6]] << 5 | DECODING_BYTES[src[7]] & 0XFF);
        id[5] = (byte) (DECODING_BYTES[src[8]] << 3 | DECODING_BYTES[src[9]] >> 2);
        id[6] = (byte) (DECODING_BYTES[src[9]] << 6 | DECODING_BYTES[src[10]] << 1 | DECODING_BYTES[src[11]] >> 4);
        id[7] = (byte) (DECODING_BYTES[src[11]] << 4 | DECODING_BYTES[src[12]] >> 1);
        id[8] = (byte) (DECODING_BYTES[src[12]] << 7 | DECODING_BYTES[src[13]] << 2 | DECODING_BYTES[src[14]] >> 3);
        id[9] = (byte) (DECODING_BYTES[src[14]] << 5 | DECODING_BYTES[src[15]] & 0XFF);
        id[10] = (byte) (DECODING_BYTES[src[16]] << 3 | DECODING_BYTES[src[17]] >> 2);
        id[11] = (byte) (DECODING_BYTES[src[17]] << 6 | DECODING_BYTES[src[18]] << 1 | DECODING_BYTES[src[19]] >> 4);

        return new Guid(id);
    }

    @Override
    public String toString() {
        byte[] bytes = new byte[STRING_LENGTH];
        bytes[0] = ENCODING_BYTES[unsigned(id[0]) >> 3];
        bytes[1] = ENCODING_BYTES[(unsigned(id[1]) >> 6) & 0x1F | (unsigned(id[0]) << 2) & 0x1F];
        bytes[2] = ENCODING_BYTES[(unsigned(id[1]) >> 1) & 0x1F];
        bytes[3] = ENCODING_BYTES[(unsigned(id[2]) >> 4) & 0x1F | (unsigned(id[1]) << 4) & 0x1F];
        bytes[4] = ENCODING_BYTES[unsigned(id[3]) >> 7 | (unsigned(id[2]) << 1) & 0x1F];
        bytes[5] = ENCODING_BYTES[(unsigned(id[3]) >> 2) & 0x1F];
        bytes[6] = ENCODING_BYTES[unsigned(id[4]) >> 5 | (unsigned(id[3]) << 3) & 0x1F];
        bytes[7] = ENCODING_BYTES[unsigned(id[4]) & 0x1F];
        bytes[8] = ENCODING_BYTES[unsigned(id[5]) >> 3];
        bytes[9] = ENCODING_BYTES[(unsigned(id[6]) >> 6) & 0x1F | (unsigned(id[5]) << 2) & 0x1F];
        bytes[10] = ENCODING_BYTES[(unsigned(id[6]) >> 1) & 0x1F];
        bytes[11] = ENCODING_BYTES[(unsigned(id[7]) >> 4) & 0x1F | (unsigned(id[6]) << 4) & 0x1F];
        bytes[12] = ENCODING_BYTES[unsigned(id[8]) >> 7 | (unsigned(id[7]) << 1) & 0x1F];
        bytes[13] = ENCODING_BYTES[(unsigned(id[8]) >> 2) & 0x1F];
        bytes[14] = ENCODING_BYTES[(unsigned(id[9]) >> 5) | (unsigned(id[8]) << 3) & 0x1F];
        bytes[15] = ENCODING_BYTES[unsigned(id[9]) & 0x1F];
        bytes[16] = ENCODING_BYTES[unsigned(id[10]) >> 3];
        bytes[17] = ENCODING_BYTES[(unsigned(id[11]) >> 6) & 0x1F | (unsigned(id[10]) << 2) & 0x1F];
        bytes[18] = ENCODING_BYTES[(unsigned(id[11]) >> 1) & 0x1F];
        bytes[19] = ENCODING_BYTES[(unsigned(id[11]) << 4) & 0x1F];
        return new String(bytes);
    }

    public int getCount() {
        return unsigned(id[11]) | unsigned(id[10]) << 8 | unsigned(id[9]) << 16;
    }

    public byte[] getMachineId() {
        byte[] mid = new byte[3];
        mid[0] = id[4];
        mid[1] = id[5];
        mid[2] = id[6];
        return mid;
    }

    public int getProcessId() {
        return unsigned(id[8]) | unsigned(id[7]) << 8;
    }

    public Date getTime() {
        int seconds = unsigned(id[3]) | unsigned(id[2]) << 8 | unsigned(id[1]) << 16 | unsigned(id[0]) << 24;
        return new Date(seconds * 1000L);
    }

    private int unsigned(int i) {
        return i & 0xFF;
    }
}
