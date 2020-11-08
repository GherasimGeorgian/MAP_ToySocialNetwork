package socialnetwork.repository.database;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.file.AbstractFileRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UtilizatorDB extends AbstractDbRepository<Long, Utilizator> {

    public UtilizatorDB(String url, String username, String password, Validator<Utilizator> validator) {
        super(url, username,password,validator);
    }

    @Override
    public Utilizator extractEntity(ResultSet rs){
        Utilizator utilizator = null;
        try{
        Long id = rs.getLong("id");
        String firstName = rs.getString("firstname");
        String lastName = rs.getString("lastname");
        utilizator = new Utilizator(firstName, lastName);
        utilizator.setId(id);
       } catch (
       SQLException e) {
            e.printStackTrace();
        }
        return utilizator;
    }
    @Override
    public String abstractSelect(){
        return new String("select * from users");
    }
    @Override
    public String abstractInsert(){
        return new String("INSERT INTO users(id,firstname,lastname) values(?,?,?)");
    }
    @Override
    public void abstractInsertParameters(PreparedStatement stmt,Utilizator utilizator){
        try{
        stmt.setLong(1, utilizator.getId());
        stmt.setString(2, utilizator.getFirstName());
        stmt.setString(3, utilizator.getLastName());
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String abstractDelete(Long id){
        return new String("DELETE FROM users where id='" + id.toString() + "'");
    }
}
