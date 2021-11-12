package lark.core.app;

import org.springframework.boot.ExitCodeGenerator;

/**
 * @author cuigh
 */
public class FatalException extends RuntimeException implements ExitCodeGenerator {
    private final int exitCode;

    public FatalException(Throwable e) {
        super(e);
        if (e instanceof ExitCodeGenerator) {
            this.exitCode = ((ExitCodeGenerator) e).getExitCode();
        } else {
            this.exitCode = 1;
        }
    }

    public FatalException(int exitCode, Throwable e) {
        super(e);
        this.exitCode = exitCode;
    }

    @Override
    public int getExitCode() {
        return this.exitCode;
    }
}
