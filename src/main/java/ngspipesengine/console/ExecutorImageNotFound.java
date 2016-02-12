package ngspipesengine.exceptions;

/**
 * Created by jsimao on 12/02/2016.
 */
public class ExecutorImageNotFound extends EngineException {
    public ExecutorImageNotFound() {
        super("Executor image not found");
    }
    public ExecutorImageNotFound(Throwable cause) {
        super("Executor image not found", cause);
    }
}
