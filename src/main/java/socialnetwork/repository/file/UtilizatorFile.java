package socialnetwork.repository.file;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UtilizatorFile extends AbstractFileRepository<Long, Utilizator> {



    Utilizator user;
    String fileNamePrietenie;
    public UtilizatorFile(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
        this.fileNamePrietenie = fileNamePrietenie;

    }

    @Override
    public Utilizator extractEntity(List<String> attributes) {
        //TODO: implement method
        user = new Utilizator(attributes.get(1),attributes.get(2));
        user.setId(Long.parseLong(attributes.get(0)));
        return user;
    }

    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }

}
