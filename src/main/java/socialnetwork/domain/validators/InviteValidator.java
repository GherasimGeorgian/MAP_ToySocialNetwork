package socialnetwork.domain.validators;

import socialnetwork.domain.Invite;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.RepositoryOptional;

public class InviteValidator implements Validator<Invite> {
    private RepositoryOptional<Long, Utilizator> repoUser;
    public InviteValidator(RepositoryOptional<Long,Utilizator> repo){
        this.repoUser =repo;
    }

    @Override
    public void validate(Invite entity) throws ValidationException {
        if(repoUser.findOne(entity.getFromInvite().getId()).isEmpty()){
            throw  new ValidationException("Invitatia nu are un emitator!");
        }
        if(repoUser.findOne(entity.getToInvite().getId()).isEmpty()){
            throw  new ValidationException("Invitatia nu are un receptor!");
        }
        if(entity.getId() < 0){
            throw  new ValidationException("Id-ul invitatiei nu poate fi negativ!");
        }
    }
}
