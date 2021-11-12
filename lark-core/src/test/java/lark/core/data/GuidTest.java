package lark.core.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * @author cuigh
 */
public class GuidTest {
    @Test
    public void lengthTest() {
        Assert.assertEquals(new Guid().toString().length(), 20);
    }

    @Test
    public void parseTest() {
        String id = new Guid().toString();
        Assert.assertEquals(id, Guid.parse(id).toString());
    }

    @Test
    public void getTimeTest() {
        long seconds = System.currentTimeMillis() / 1000;
        Date date = new Date(seconds * 1000);
        Guid id = new Guid();
        Assert.assertEquals(date, id.getTime());
    }
}