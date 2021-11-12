package lark.db.sql;

import lark.core.lang.EnumValuable;
import lark.db.sql.clause.InsertEndClause;
import lark.db.sql.clause.SelectEndClause;
import lark.db.sql.clause.UpdateEndClause;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static lark.db.sql.SqlHelper.*;

/**
 * @author cuigh
 */
public class MysqlTest {
    @Autowired
    private SqlQuery db;

//    @Test
//    public void testShardTable() {
//        Person person = new Person(2, "test");
//        // 方式1
//        TableQuery query = db.table("person", person.getId());
//        // 方式2
////        TableQuery query = db.table(person);
//        BuildResult result;
//
//        result = query.insert(person).print();
//        println(result);
//
//        result = query.delete(f("id", person.getId())).print();
//        println(result);
//        result = query.delete(person).print();
//        println(result);
//
//        result = query.update(person).print();
//        println(result);
//        result = query.update(person, "name").print();
//        println(result);
//        result = query.update(u("name", "test")).print();
//        println(result);
//
//        result = query.select("id", "name").where(f("id", person.getId())).print();
//        println(result);
//        result = query.select(Person.class).where(f("id", person.getId())).print();
//        println(result);
//        result = query.select(c("count(*)", "count")).print();
//        println(result);
//    }

    @Test
    public void testInsert() {
        InsertEndClause clause;
        InsertResult ir;

        // 先删除要创建的记录
        int affectedRows = db.delete("person", f().in("id", new int[]{1, 2}));
        println(String.format("{affectedRows: %d}", affectedRows));

        // 指定主键
        clause = db.insert("person").columns("id", "name").values(1, "n1").values(2, "n2");
        ir = clause.result();
        println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));

        // 插入对象
        ir = db.insert(new Person(3, "n3"));
        println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));

        // 批量插入对象
        ArrayList<Person> persons = new ArrayList<>();
        persons.add(new Person(4, "n4"));
        persons.add(new Person(5, "n5"));
        ir = db.insert(persons);
        println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));

        // 自增主键
//        br = db.insert("person").values(1, "n1").values(2, "n2").print();
//        println(br);
//
//        // 返回自增主键值
//        ir = db.insert("person").columns("name").values("n3").values("n4").result(true);
//        if (ir != null) {
//            println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));
//        }
    }

    @Test
    public void testUpdate() {
        Updaters values = u("name", "new name");
        Filter f = Filter.or(f("id", 1), f("id", 2));   // 更新条件: id = 1 OR id = 2
        UpdateEndClause clause = db.update("person").set(values).where(f);
        int affectedRows = clause.submit();
        println(String.format("{affectedRows: %d}", affectedRows));

        Person p = new Person(22, "abc");
        affectedRows = db.update(p, "name");
        println(String.format("{affectedRows: %d}", affectedRows));
    }

    @Test
    public void testDelete() {
        int affectedRows = db.delete("person", f("id", 1));
//        db.delete(new Person(1, "test"));
        println(String.format("{affectedRows: %d}", affectedRows));
    }

    @Test
    public void testSelect() {
        SelectEndClause clause = db.select("Id", "Name", "Age").from("person").where(f("id", 22));

        // value
        int value = clause.value(int.class);
        println(value);

        // one
        println(clause.one());

        // one
        Person p = clause.one(Person.class);
        if (p != null) {
            println(String.format("{id: %d, name: %s}", p.getId(), p.getName()));
        }

        // all
        List<Person> persons = clause.list(Person.class);
        println("all: " + persons.size());

        // select by class
        p = db.select(Person.class).where(f("id", 22)).one(Person.class);
        if (p != null) {
            println(String.format("{id: %d, name: %s, age: %d, status: %s, time: %s}",
                    p.getId(), p.getName(), p.getAge(), p.getStatus(), p.getTime()));
        }

        // select by object
        p = db.select(new Person(22, null)).one(Person.class);
        println(p);
    }

    @Test
    public void testSelect1() throws Exception {
        Table t1 = t("Activity", "A");
        Table t2 = t("ActivityCategory", "AC");
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2014-11-20");
        SelectEndClause clause = db.select(c(t2, "ID", "NAME_CN").add("COUNT(*)", "Count"))
                .from(t1).join(t2, f(t1, "ActivityCategory", t2, "ID"))
                .where(f().lt(t1, "ENTER_TIME", date))
                .groupBy(g(t2, "ID", "NAME_CN"))
                .having(f().gt("Count", 0))
                .orderBy(asc(t1, "ActivityCategory"))
                .limit(0, 10);
    }

    @Test
    public void testExecute() {
        Person person = db.execute("select * from person where id = ?", 1).one(Person.class);
        println(String.format("{id: %d, name: %s}", person.getId(), person.getName()));

        List<Person> persons = db.execute("select * from person where id<?", 100).list(Person.class);
        println(String.format("total persons: %d", persons.size()));

        int affectedRows = db.execute("update person set age = 23 where id=?", 23).submit();
        println(String.format("affected rows: %d", affectedRows));
    }

    @Test
    public void testCall() {
//        db.call("GetUser").with(null).result();
    }

    @Test
    public void testPrepare() {
//        db.call("GetUser").with(null).result();
    }

    @Test
    public void TestMap() {
//        ArrayList<Integer> list = new ArrayList<>();
//        list.add(1);
//        Type[] types = list.getClass().getGenericInterfaces();
//        println(list.getClass() == List.class);
    }

    private void println(Object obj) {
        System.out.println(obj);
    }

    public enum PersonStatus implements EnumValuable {
        VALID(1), INVALID(2);

        private int value;

        PersonStatus(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return value;
        }
    }

    /**
     * 测试用
     */
    @Getter
    @Setter
    @SqlTable(shardKeys = {"id"})
    public static class Person {
        @Id
        @GeneratedValue
        private int id;
        private String name;
        private int age;
        private PersonStatus status;
        private LocalDateTime time;

        public Person() {
        }

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
