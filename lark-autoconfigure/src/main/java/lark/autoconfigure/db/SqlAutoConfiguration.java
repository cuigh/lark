package lark.autoconfigure.db;

import lark.db.sql.SqlQuery;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for SQL database.
 *
 * @author cuigh
 */
@Configuration
@ConditionalOnClass({SqlQuery.class, DataSource.class})
public class SqlAutoConfiguration {
    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean
    public SqlQuery sqlQuery(DataSource source) {
        return new SqlQuery(source);
    }
}
