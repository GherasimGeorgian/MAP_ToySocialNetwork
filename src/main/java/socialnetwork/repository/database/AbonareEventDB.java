package socialnetwork.repository.database;

import socialnetwork.domain.AbonareEveniment;
import socialnetwork.domain.Invite;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;

public class AbonareEventDB extends AbstractDbRepository<Tuple<Long,Long>, AbonareEveniment> {

    public AbonareEventDB(String url, String username, String password, Validator<AbonareEveniment> validator) {
        super(url, username, password, validator);
    }
    static public LocalDateTime toLdt(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        ZonedDateTime zdt = cal.toZonedDateTime();
        return zdt.toLocalDateTime();
    }

    @Override
    public AbonareEveniment extractEntity(ResultSet rs){
        AbonareEveniment abonare = null;
        try{
            Long id_abonare = rs.getLong("id_abonare");
            Long id_utilizator = rs.getLong("id_utilizator");
            Long id_eveniment = rs.getLong("id_eveniment");
            Date ldtStr = rs.getDate("data_abonare");
            LocalDateTime ldt = toLdt(ldtStr);
            abonare = new AbonareEveniment(id_abonare,id_utilizator,id_eveniment,ldt);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return abonare;
    }
    @Override
    public String abstractSelect(){
        return new String("select * from abonatievenimente");
    }
    @Override
    public String abstractInsert(){
        return new String("INSERT INTO abonatievenimente(id_abonare,id_utilizator,id_eveniment,data_abonare) values(?,?,?,?)");
    }
    @Override
    public void abstractInsertParameters(PreparedStatement stmt, AbonareEveniment abonare){
        try{
            stmt.setLong(1, abonare.getIdAbonare());
            stmt.setLong(2, abonare.getIdUtilizator());
            stmt.setLong(3, abonare.getIdEveniment());
            stmt.setTimestamp(4, Timestamp.valueOf(abonare.getDataAbonare()));
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String abstractDelete(Tuple<Long,Long> id){
        return new String("DELETE FROM abonatievenimente where id_utilizator='" + id.getLeft().toString() + "' and id_eveniment='" + id.getRight().toString() + "'");
    }
    @Override
    public String abstractUpdate(){
        return new String("");
    }
    public void abstractUpdateParameters(PreparedStatement stmt, AbonareEveniment entity){
        //TODO
    }
}