package socialnetwork.repository.repo_validators;

import socialnetwork.domain.validators.ValidationException;

public interface RepoValidator<T>{
    void validate() throws ValidationException;
}

