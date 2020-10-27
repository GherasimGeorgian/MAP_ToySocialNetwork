package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;


public class PrietenieValidator implements Validator<Prietenie> {

    private Repository<Long,Utilizator> repo;
    public PrietenieValidator(Repository<Long,Utilizator> repo){
        this.repo =repo;
    }

    @Override
    public void  validate(Prietenie entity) throws ValidationException {
        if(entity.getId().getLeft().toString().equals(entity.getId().getRight().toString())){
            throw new  ValidationException("Aceleasi id-uri ale unei prietenii");

       }
        if(entity.getId().getLeft() == null){
            throw new  ValidationException("Left is null");
        }
        if(entity.getId().getRight() == null){
            throw new  ValidationException("Right is null");
        }
        // verificam daca exista useri unei prietenii

        if(repo.findOne(entity.getId().getLeft()) == null){
            throw new  ValidationException("Userul cu id-ul " + entity.getId().getLeft() + " nu exista!");
        }
        if(repo.findOne(entity.getId().getRight()) == null){
            throw new  ValidationException("Userul cu id-ul " + entity.getId().getRight() + " nu exista!");
        }

    }

}
