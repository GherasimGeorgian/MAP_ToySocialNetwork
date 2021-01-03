package socialnetwork.repository.database;

import socialnetwork.domain.Account;
import socialnetwork.domain.Eveniment;import socialnetwork.domain.validators.Validator;

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



            account = new Account(idutilizator,ldt,email,parola,typeACC);


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
        return new String("INSERT INTO account(id_utilizator,date_create_account,email,parola,type_account) values(?,?,?,?,?)");
    }
    @Override
    public void abstractInsertParameters(PreparedStatement stmt, Account account){
        try{
            stmt.setLong(1, account.getId());
            stmt.setTimestamp(2,Timestamp.valueOf(account.getData_creeare_cont()));
            stmt.setString(3,account.getEmail());
            stmt.setString(4,account.getParola());
            stmt.setString(5,account.getTipCont());
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
        //TODO
        return new String("");
    }
    public void abstractUpdateParameters(PreparedStatement stmt,Account entity){
        //TODO
    }
}
