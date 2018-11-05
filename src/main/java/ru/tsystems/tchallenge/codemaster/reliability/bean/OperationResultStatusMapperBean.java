package ru.tsystems.tchallenge.codemaster.reliability.bean;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus;
import ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatusMapper;

@Service
public class OperationResultStatusMapperBean implements OperationResultStatusMapper {

    @Override
    public HttpStatus apply(final OperationResultStatus operationResultStatus) {

        switch (operationResultStatus) {

            case SUCCESS_RETRIEVED:
            case SUCCESS_UPDATED:
            case SUCCESS_DELETED:

                return HttpStatus.OK;

            case SUCCESS_CREATED:

                return HttpStatus.CREATED;

            case FAILURE_VALIDATION:
            case FAILURE_LOGIC_SEARCH_CRITERIA:
            case FAILURE_LOGIC_DATA_CONSISTENCY:
            case FAILURE_LOGIC_RECORD_NOT_FOUND:
                return HttpStatus.BAD_REQUEST;
            case FAILURE_LOGIC_NOT_AUTHORIZED:
                return HttpStatus.FORBIDDEN;
            default:

                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
