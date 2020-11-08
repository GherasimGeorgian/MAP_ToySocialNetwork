package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryOptional;

import java.util.Optional;

public class PrietenieValidatorDb implements Validator<Prietenie>{
    private RepositoryOptional<Long, Utilizator> repo;
    public PrietenieValidatorDb(RepositoryOptional<Long,Utilizator> repo){
        this.repo =repo;
    }

    @Override
    public void  validate(Prietenie entity) throws ValidationException {
        //verificam daca useri din prietenie exista
        Optional<Utilizator> userDreapta = repo.findOne(entity.getId().getRight());

        if(userDreapta.isEmpty()){
            throw new  ValidationException("Eroare citire fisier user cu id " + entity.getId().getRight());
        }
        Optional<Utilizator> userStanga = repo.findOne(entity.getId().getLeft());
        if(userStanga.isEmpty()){
            throw new  ValidationException("Eroare citire fisier user cu id " + entity.getId().getLeft());
        }


        if(entity.getId().getLeft().toString().equals(entity.getId().getRight().toString())){
            throw new  ValidationException("Aceleasi id-uri ale unei prietenii");

        }
        if(entity.getId().getLeft() == null){
            throw new  ValidationException("Left is null");
        }
        if(entity.getId().getRight() == null){
            throw new  ValidationException("Right is null");
        }

    }
}
