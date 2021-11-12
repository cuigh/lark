package lark.db.sql;

import lark.db.sql.clause.SelectEndClause;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Id;
import java.util.Date;

import static lark.db.sql.SqlHelper.f;
import static lark.db.sql.SqlHelper.t;

/**
 * @author cuigh
 */
public class MssqlTest {
    @Autowired
    private SqlQuery db;

    @Test
    public void testInsert() {
        // 插入记录
//        Date time = new Date();
//        InsertEndClause clause = db.insert("ActionLog")
//                .columns("ActionObjType", "ActionObjID", "ActionUser", "ActionTime", "Description")
//                .values(0, 0, 0, time, "111");
//        BuildResult br = clause.print();
//        println(br);
//        InsertResult ir = clause.result(true);
//        println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));
//
//        // 清理测试创建的记录
//        List<Object> keys = ir.getKeys();
//        if (keys.size() > 0) {
//            SimpleResult sr = db.delete("ActionLog").where(f("ActionLogID", keys.get(0))).result();
//            println(String.format("{affectedRows: %d}", sr.getAffectedRows()));
//        }
    }

    @Test
    public void testUpdate() {
//        Updaters values = u("Description", new Date().toString());
//        Filter f = f("ActionLogID", 1466).or(f("ActionLogID", 1467));   // 更新条件: ActionLogID = 1466 OR ActionLogID = 1467
//        UpdateEndClause clause = db.update("ActionLog").set(values).where(f);
//        BuildResult br = clause.print();
//        println(br);
//        SimpleResult sr = clause.result();
//        println(String.format("{affectedRows: %d}", sr.getAffectedRows()));
    }

    @Test
    public void testDelete() {
//        DeleteEndClause clause = db.delete("ActionLog").where(f("ActionLogID", 1));
//        BuildResult br = clause.print();
//        println(br);
//        SimpleResult sr = clause.result();
//        println(String.format("{affectedRows: %d}", sr.getAffectedRows()));
    }

    @Test
    public void testSelect() {
        // 复杂查询
        Table t1 = t("t1");
        Filter on = f("id", 1);
        Filter where = f("name", "test");
        Filter having = f("value", "vvv");
        Groupers groupers = t1.groupers("c", "d");
        Sorters sorters = t1.desc("e");
        SelectEndClause clause = db.select("a", "b").from(t1).join("t2", on).where(where).groupBy(groupers)
                .having(having).orderBy(sorters).limit(10, 10);
//
//        clause = db.select("ActionLogID", "ActionTime", "Description").from("ActionLog").where(f("ActionLogID", 1466));
//        // one
//        SelectResult sr = clause.result();
//        println(sr.one());
//
//        // one
//        sr = clause.result();
//        ActionLog p = sr.one(ActionLog.class);
//        if (p != null) {
//            println(String.format("{id: %d, desc: %s}", p.getActionLogID(), p.getDescription()));
//        }
//
//        // each
//        clause = db.select("ActionLogID", "ActionTime", "Description").from("ActionLog").where(f("ActionLogID", FilterType.IN, "1466,1467"));
//        sr = clause.result();
//        sr.each(reader -> println("each: " + reader.getInt(1)));
//
//        // all
//        sr = clause.result();
//        List<ActionLog> persons = sr.all(ActionLog.class);
//        println("all: " + persons.size());
    }

    @Test
    public void testTransaction() throws Exception {
//        Transaction tx = db.begin();
//        try {
//            SimpleResult sr = tx.update("ActionLog").set(u("Description", new Date().toString())).where(f("ActionLogID", 1466)).result();
//            println(sr.getAffectedRows());
//
//            Map<String, Object> map = tx.select("ActionLogID", "Description").from("ActionLog").where(f("ActionLogID", 1466)).result().one();
//            println(map);
//
//            tx.commit();
//        } catch (Exception e) {
//            tx.rollback();
//            e.printStackTrace();
//        } finally {
//            tx.close();
//        }
    }

//    @Test
//    public void testCall() throws Exception {
//        // test sql builder
//        CallParams params = new CallParams(4);
//        params.add(1, 123);
//        params.addOut(3, JDBCType.INTEGER);
//        params.addInOut(4, JDBCType.INTEGER, "abc");
//        params.addReturn(JDBCType.INTEGER);
//        BuildResult br = db.call("test").with(params).print();
//        println(br.toString());
//
//        // test exec
//        params = new CallParams(7);
//        params.add(1, 2);
//        params.add(2, "http://www.paycheckmovie.com/");
//        params.add(3, "paycheckmovie");
//        params.add(4, null);
//        params.add(5, 100049);
//        params.addOut(6, JDBCType.INTEGER);
//        params.addOut(7, JDBCType.INTEGER);
//        params.addReturn(JDBCType.INTEGER);
//        CallResult cr = db.call("sp_UpdateLink").with(params).result();
//        int outValue1 = cr.getParam(6, int.class);
//        Integer outValue2 = cr.getParam("newid", Integer.class);
//        int returnValue = cr.getReturn(int.class);
//        System.out.printf("{%d, %d, %d}", outValue1, outValue2, returnValue);
//    }

    private void println(Object obj) {
        System.out.println(obj);
    }

    /**
     * 测试用
     */
    @javax.persistence.Table(name = "ActionLog")
    public static class ActionLog {
        @Getter
        @Setter
        @Id
//        @Column(name = "ActionLogID")
        private int actionLogID;
        @Getter
        @Setter
        private String description;
        @Getter
        @Setter
        private Date actionTime;
    }
}
