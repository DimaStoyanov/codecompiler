package ru.tsystems.tchallenge.contest.codemaster.api.controller.bean;

import org.springframework.beans.factory.annotation.Autowired;
import ru.tsystems.tchallenge.contest.codemaster.api.model.OperationResult;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationResultResponseBuilder;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationResultStatusMapper;

import static ru.tsystems.tchallenge.contest.codemaster.reliability.OperationResultStatus.*;

/**
 * Generic API controller implementation.
 */
abstract class GenericApiControllerBean  {

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

