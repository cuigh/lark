package lark.pb;

import lark.core.codec.JsonCodec;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by noname on 15/12/29.
 */
public class EncodeTest {
//    @Test
//    public void testPerf() throws Exception {
//        Codec<ProtoBasic1> codec1 = ProtobufProxy.create(ProtoBasic1.class);
//        Codec<ProtoBasic1> codec2 = CodecFactory.get(ProtoBasic1.class);
//
//        ProtoBasic1 pb1;
//        pb1 = new ProtoBasic1();
//        pb1.Date = new Date();
//        pb1.LocalDateTime = LocalDateTime.now();
//
//        // warm
//        codec1.encode(pb1);
//        codec2.encode(pb1);
//
//        long start;
//
//        start = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {
//            pb1 = new ProtoBasic1();
//            pb1.Date = new Date();
//            pb1.LocalDateTime = LocalDateTime.now();
//            codec1.encode(pb1);
//        }
//        System.out.println("JDK: " + (System.currentTimeMillis() - start) + " ms");
//
//        start = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {
//            pb1 = new ProtoBasic1();
//            pb1.Date = new Date();
//            pb1.LocalDateTime = LocalDateTime.now();
//            codec2.encode(pb1);
//        }
//        System.out.println("ASM: " + (System.currentTimeMillis() - start) + " ms");
//    }

    @Test
    public void testAll() throws Exception {
        ProtoAll pb1, pb2;
        byte[] bytes;
        Codec<ProtoAll> codec = CodecFactory.get(ProtoAll.class);

        pb1 = ProtoAll.getSample();
        bytes = codec.encode(pb1);
        pb2 = codec.decode(bytes);

        String s1 = JsonCodec.encode(pb1);
        String s2 = JsonCodec.encode(pb2);
        Assert.assertEquals(s1, s2);
    }

    @Test
    public void testBasic1() throws Exception {
        ProtoBasic1 pb1, pb2;
        byte[] bytes;

        pb1 = new ProtoBasic1();
        Codec<ProtoBasic1> codec = CodecFactory.get(ProtoBasic1.class);
        bytes = codec.encode(pb1);
        pb2 = codec.decode(bytes);

        String s1 = JsonCodec.encode(pb1);
        String s2 = JsonCodec.encode(pb2);
        Assert.assertEquals(s1, s2);
        Assert.assertTrue(s1.equals(s2));
    }

    @Test
    public void testBasic2() throws Exception {
        ProtoBasic2 pb1, pb2;
        byte[] bytes;

        pb1 = new ProtoBasic2();
        Codec<ProtoBasic2> codec = CodecFactory.get(ProtoBasic2.class);
        bytes = codec.encode(pb1);
        pb2 = codec.decode(bytes);

        String s1 = JsonCodec.encode(pb1);
        String s2 = JsonCodec.encode(pb2);
        Assert.assertEquals(s1, s2);
        Assert.assertTrue(s1.equals(s2));
    }

    @Test
    public void testList1() throws Exception {
        ProtoList1 pb1, pb2;
        byte[] bytes;

        pb1 = new ProtoList1();
        pb1.DoubleList.add(1.0);
        pb1.FloatList.add(1.0F);
        pb1.LongList.add(1L);
        pb1.IntegerList.add(1);
        pb1.BooleanList.add(true);
        pb1.StringList.add("1");
        pb1.BytesList.add(new byte[]{1});
        pb1.EnumList.add(ProtoList1.ObjectStatus.VALID);

        Codec<ProtoList1> codec = CodecFactory.get(ProtoList1.class);

        bytes = codec.encode(pb1);
        pb2 = codec.decode(bytes);

        String s1 = JsonCodec.encode(pb1);
        String s2 = JsonCodec.encode(pb2);
        Assert.assertEquals(s1, s2);
        Assert.assertTrue(s1.equals(s2));
    }

    @Test
    public void testList2() throws Exception {
        ProtoList2 pb1, pb2;
        byte[] bytes;

        pb1 = new ProtoList2();
        ArrayList<Double> doubles = new ArrayList<>();
        doubles.add(1.0);
        pb1.setDoubleList(doubles);
        pb1.getFloatList().add(1.0F);
        pb1.getLongList().add(1L);
        pb1.getIntegerList().add(1);
        pb1.getBooleanList().add(true);
        pb1.getStringList().add("1");
        pb1.getBytesList().add(new byte[]{1});

        Codec<ProtoList2> codec = CodecFactory.get(ProtoList2.class);

        bytes = codec.encode(pb1);
        pb2 = codec.decode(bytes);

        String s1 = JsonCodec.encode(pb1);
        String s2 = JsonCodec.encode(pb2);
        Assert.assertEquals(s1, s2);
        Assert.assertTrue(s1.equals(s2));
    }

    @Test
    public void testObject1() throws Exception {
        ProtoObject1 pb1, pb2;
        byte[] bytes;

        pb1 = new ProtoObject1();
        pb1.Status = ProtoObject1.ObjectStatus.INVALID;

        ProtoObject1.ObjectItem.ObjectSubItem subItem = new ProtoObject1.ObjectItem.ObjectSubItem();
        subItem.Name = "ObjectSubItem";
        ProtoObject1.ObjectItem item = new ProtoObject1.ObjectItem();
        item.Name = "ObjectItem";
        item.SubItems.add(subItem);
        pb1.Items.add(item);

        Codec<ProtoObject1> codec = CodecFactory.get(ProtoObject1.class);
        bytes = codec.encode(pb1);
        pb2 = codec.decode(bytes);

        String s1 = JsonCodec.encode(pb1);
        String s2 = JsonCodec.encode(pb2);
        Assert.assertEquals(s1, s2);
        Assert.assertTrue(s1.equals(s2));
    }

    @Test
    public void testObject2() throws Exception {
        ProtoObject2 pb1, pb2;
        byte[] bytes;

        pb1 = new ProtoObject2();
        pb1.setStatus(ProtoObject2.ObjectStatus.INVALID);

        // set items
        ProtoObject2.ObjectItem.ObjectSubItem subItem = new ProtoObject2.ObjectItem.ObjectSubItem();
        subItem.setName("ObjectSubItem");

        ProtoObject2.ObjectItem item = new ProtoObject2.ObjectItem();
        item.setName("ObjectItem");
        item.getSubItems().add(subItem);

        ArrayList<ProtoObject2.ObjectItem> items = new ArrayList<>();
        items.add(item);

        pb1.setItems(items);

        Codec<ProtoObject2> codec = CodecFactory.get(ProtoObject2.class);
        bytes = codec.encode(pb1);
        pb2 = codec.decode(bytes);

        String s1 = JsonCodec.encode(pb1);
        String s2 = JsonCodec.encode(pb2);
        Assert.assertEquals(s1, s2);
        Assert.assertTrue(s1.equals(s2));
    }

    @Test
    public void testLombok() throws Exception {
        ProtoLombok pb1, pb2;
        byte[] bytes;

        pb1 = ProtoLombok.getSample();

        Codec<ProtoLombok> codec = CodecFactory.get(ProtoLombok.class);
        bytes = codec.encode(pb1);
        pb2 = codec.decode(bytes);

        String s1 = JsonCodec.encode(pb1);
        String s2 = JsonCodec.encode(pb2);
        Assert.assertEquals(s1, s2);
        Assert.assertTrue(s1.equals(s2));
    }
}
