package lark.db.sql;

import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cuigh
 */
public enum DatabaseType {
    //    DERBY("Apache Derby"),
//    DB2("DB2"),
//    DB2VSE("DB2VSE"),
//    DB2ZOS("DB2ZOS"),
//    DB2AS400("DB2AS400"),
//    HSQL("HSQL Database Engine"),
//    ORACLE("Oracle"),
//    POSTGRES("PostgreSQL"),
//    SYBASE("Sybase"),
//    H2("H2"),
    SQLITE("SQLite"),
    MYSQL("MySQL"),
    SQLSERVER("Microsoft SQL Server");

    private static final Map<String, DatabaseType> NAME_MAP = new HashMap<>();

    static {
        for (DatabaseType type : values()) {
            NAME_MAP.put(type.getProductName(), type);
        }
    }

    private final String productName;

    DatabaseType(String productName) {
        this.productName = productName;
    }

    static DatabaseType fromMetaData(DataSource dataSource) {
        try {
            String productName = JdbcUtils.extractDatabaseMetaData(dataSource, "getDatabaseProductName").toString();
            if (StringUtils.hasText(productName) && productName.startsWith("DB2")) {
                String productVersion = JdbcUtils.extractDatabaseMetaData(dataSource, "getDatabaseProductVersion").toString();
                if (productVersion.startsWith("ARI")) {
                    productName = "DB2VSE";
                } else if (productVersion.startsWith("DSN")) {
                    productName = "DB2ZOS";
                } else if (productName.indexOf("AS") != -1 && (productVersion.startsWith("QSQ") ||
                        productVersion.substring(productVersion.indexOf('V')).matches("V\\dR\\d[mM]\\d"))) {
                    productName = "DB2AS400";
                } else {
                    productName = JdbcUtils.commonDatabaseName(productName);
                }
            } else {
                productName = JdbcUtils.commonDatabaseName(productName);
            }
            return fromProductName(productName);
        } catch (MetaDataAccessException e) {
            throw new SqlException(e);
        }
    }

    static DatabaseType fromProductName(String productName) {
        if (!NAME_MAP.containsKey(productName)) {
            throw new IllegalArgumentException("DatabaseType not found for product name: [" +
                    productName + "]");
        } else {
            return NAME_MAP.get(productName);
        }
    }

    public String getProductName() {
        return productName;
    }
}
