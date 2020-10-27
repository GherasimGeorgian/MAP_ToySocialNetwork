package socialnetwork.domain.validators;

import java.util.Map;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
