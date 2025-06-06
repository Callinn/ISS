package mpp.iss_iteratia1_.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;

}
