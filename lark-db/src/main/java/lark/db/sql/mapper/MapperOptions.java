package lark.db.sql.mapper;

import lark.db.sql.NamingStyle;

/**
 * @author cuigh
 */
public class MapperOptions {
    NamingStyle tableNaming = NamingStyle.LOWER;
    NamingStyle columnNaming = NamingStyle.LOWER;

    public void setTableNaming(NamingStyle tableNaming) {
        this.tableNaming = tableNaming;
    }

    public void setColumnNaming(NamingStyle columnNaming) {
        this.columnNaming = columnNaming;
    }
}
