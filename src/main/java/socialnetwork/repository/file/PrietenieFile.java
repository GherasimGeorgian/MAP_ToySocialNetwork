

package socialnetwork.repository.file;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrietenieFile extends AbstractFileRepository<Tuple<Long,Long>, Prietenie> {

    public PrietenieFile(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    public Prietenie extractEntity(List<String> attributes) {
       //TODO: implement method
        String stringLdt = attributes.get(2);
        LocalDateTime ldt = LocalDateTime.parse(stringLdt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm"));
        Prietenie prietenie = new Prietenie(Long.parseLong(attributes.get(0)),Long.parseLong(attributes.get(1)),ldt);
        validator.validate(prietenie);

        return prietenie;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId().getLeft()+";"+entity.getId().getRight() + ";" + entity.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm"));
    }
}
