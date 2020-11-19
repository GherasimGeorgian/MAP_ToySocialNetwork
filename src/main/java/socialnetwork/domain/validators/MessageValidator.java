package socialnetwork.domain.validators;

import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.RepositoryOptional;

public class MessageValidator implements Validator<Message> {
    private RepositoryOptional<Long, Utilizator> repoUser;
    public MessageValidator(RepositoryOptional<Long,Utilizator> repo){
        this.repoUser =repo;
    }
    @Override
    public void validate(Message entity) throws ValidationException {
        if(repoUser.findOne(entity.getFrom().getId()).isEmpty()){
            throw new ValidationException("Nu exista userFrom la mesaj");
        }
        if(entity.getTo().isEmpty()){
            throw new ValidationException("Nu exista userTo la mesaj");
        }
        if(entity.getDate() == null){
            throw new ValidationException("Nu exista data la mesaj");
        }
        if(entity.getMessage().isEmpty()){
            throw new ValidationException("Nu exista mesaj la mesaj");
        }

    }
}
