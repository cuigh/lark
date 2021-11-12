package lark.db.sql.builder;

import lark.core.util.Arrays;
import lark.db.sql.converter.ConverterManager;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
public final class BuildBuffer {
    private List<Object> args;
    private StringBuilder builder = new StringBuilder();

    String getSql() {
        return builder.toString();
    }

    BuildBuffer addSql(String sql) {
        builder.append(sql);
        return this;
    }

    BuildBuffer addSql(String fmt, Object... args) {
        builder.append(String.format(fmt, args));
        return this;
    }

    BuildBuffer addSql(int repeat, String str, String delimiter) {
        for (int i = 0; i < repeat; i++) {
            if (i > 0) {
                builder.append(delimiter);
            }
            builder.append(str);
        }
        return this;
    }

//    BuildBuffer insertSql(int index, String sql) {
//        builder.insert(index, sql);
//        return this;
//    }

    BuildBuffer addArg(Object arg) {
        if (args == null) {
            args = new ArrayList<>();
        }
        args.add(ConverterManager.j2d(arg));
        return this;
    }

    BuildBuffer addArg(Object... args) {
        if (this.args == null) {
            this.args = new ArrayList<>();
        }
        if (!Arrays.isEmpty(args)) {
            for (Object arg : args) {
                this.args.add(ConverterManager.j2d(arg));
            }
        }
        return this;
    }

    BuildBuffer addArg(List<Object> args) {
        if (this.args == null) {
            this.args = new ArrayList<>();
        }
        if (!CollectionUtils.isEmpty(args)) {
            for (Object arg : args) {
                this.args.add(ConverterManager.j2d(arg));
            }
        }
        return this;
    }

    BuildResult getResult() {
        if (builder.length() == 0) {
            return null;
        }
        return new BuildResult(builder.toString(), args);
    }
}
