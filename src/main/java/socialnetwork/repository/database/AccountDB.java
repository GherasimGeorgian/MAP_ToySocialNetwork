package socialnetwork.repository.database;

import socialnetwork.domain.Account;
import socialnetwork.domain.Eveniment;
import socialnetwork.domain.InviteStatus;
import socialnetwork.domain.validators.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AccountDB  extends AbstractDbRepository<Long, Account>{

    public AccountDB(String url, String username, String password, Validator<Account> validator) {
        super(url, username,password,validator);
    }
    @Override
    public Account extractEntity(ResultSet rs){
        Account account = null;
        try{
            Long idutilizator = rs.getLong("id_utilizator");

            Timestamp ldtStr = rs.getTimestamp("date_create_account");
            LocalDateTime ldt = ldtStr.toLocalDateTime(); //toLdt(ldtStr);

            String email = rs.getString("email");
            String parola = rs.getString("parola");
            String typeACC = rs.getString("type_account");
            String url_photo = rs.getString("photo_url");


            account = new Account(idutilizator,ldt,email,parola,typeACC,url_photo);


        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
    @Override
    public String abstractSelect(){
        return new String("select * from account");
    }
    @Override
    public String abstractInsert(){
        return new String("INSERT INTO account(id_utilizator,date_create_account,email,parola,type_account,photo_url) values(?,?,?,?,?,?)");
    }
    @Override
    public void abstractInsertParameters(PreparedStatement stmt, Account account){
        try{
            stmt.setLong(1, account.getId());
            stmt.setTimestamp(2,Timestamp.valueOf(account.getData_creeare_cont()));
            stmt.setString(3,account.getEmail());
            stmt.setString(4,account.getParola());
            stmt.setString(5,account.getTipCont());
            stmt.setString(6,account.getUrl_photo());
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String abstractDelete(Long id){
        return new String("DELETE FROM account where id_utilizator='" + id.toString() + "'");
    }

    @Override
    public String abstractUpdate(){
        return new String("UPDATE account SET id_utilizator = ?," +
                "date_create_account = ?," +
                "email = ?," +
                "parola = ?," +
                "type_account= ?," +
                "photo_url= ?" +
                " WHERE id_utilizator = ?");
    }
    public void abstractUpdateParameters(PreparedStatement stmt,Account entity){
        try{
            stmt.setLong(1, entity.getId());
            stmt.setTimestamp(2,Timestamp.valueOf(entity.getData_creeare_cont()));
            stmt.setString(3, entity.getEmail());
            stmt.setString(4, entity.getParola());
            stmt.setString(5, entity.getTipCont());
            stmt.setString(6, entity.getUrl_photo());
            stmt.setLong(7,entity.getId());
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }
}
