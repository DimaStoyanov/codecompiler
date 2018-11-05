package ru.tsystems.tchallenge.codemaster.api.controller.bean;

import org.springframework.beans.factory.annotation.Autowired;
import ru.tsystems.tchallenge.codemaster.api.model.OperationResult;
import ru.tsystems.tchallenge.codemaster.reliability.OperationResultResponseBuilder;
import ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatusMapper;
import ru.tsystems.tchallenge.codemaster.utils.component.GenericComponent;

import static ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus.*;

/**
 * Generic API controller implementation.
 */
abstract class GenericApiControllerBean extends GenericComponent {

    @Autowired
    private OperationResultStatusMapper operationResultStatusMapper;

    /**
     * Creates a new response entity builder for an operation result.
     *
     * @return new response entity builder.
     */
    protected OperationResultResponseBuilder responseBuilder() {
        return new OperationResultResponseBuilder().statusMapper(operationResultStatusMapper);
    }

    /**
     * A shortcut for {@link #responseBuilder()} for a successful data creation.
     *
     * @return new response entity builder.
     */
    protected OperationResultResponseBuilder created() {
        return responseBuilder().status(SUCCESS_CREATED);
    }

    /**
     * A shortcut for {@link #responseBuilder()} for a successful data deletion.
     *
     * @return new response entity builder.
     */
    protected OperationResultResponseBuilder deleted() {
        return responseBuilder().status(SUCCESS_DELETED).type(OperationResult.class);
    }

    /**
     * A shortcut for {@link #responseBuilder()} for a successful data retrieval.
     *
     * @return new response entity builder.
     */
    protected OperationResultResponseBuilder retrieved() {
        return responseBuilder().status(SUCCESS_RETRIEVED);
    }

    /**
     * A shortcut for {@link #responseBuilder()} for a successful data update.
     *
     * @return new response entity builder.
     */
    protected OperationResultResponseBuilder updated() {
        return responseBuilder().status(SUCCESS_UPDATED);
    }
}

