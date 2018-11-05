package ru.tsystems.tchallenge.codemaster.service.validator;

public interface GenericValidator<D> {

    void validateOnGet(final String id);
    void validateOnGetResult(final D existingObject);
    void validateOnCreate(final D invoiceObject);
    void validateOnUpdate(final D invoiceObject, final D existingObject);
    void validateOnDelete(final String idToDelete);

}
