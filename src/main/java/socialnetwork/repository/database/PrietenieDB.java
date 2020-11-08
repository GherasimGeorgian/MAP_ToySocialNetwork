package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;

public class PrietenieDB extends AbstractDbRepository<Tuple<Long,Long>, Prietenie>{

    public PrietenieDB(String url, String username, String password, Validator<Prietenie> validator) {
        super(url, username,password,validator);
    }

    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    static public LocalDateTime toLdt(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        ZonedDateTime zdt = cal.toZonedDateTime();
        return zdt.toLocalDateTime();
    }

    static public Date fromLdt(LocalDateTime ldt) {
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        GregorianCalendar cal = GregorianCalendar.from(zdt);
        return cal.getTime();
    }

    @Override
    public Prietenie extractEntity(ResultSet rs){
        Prietenie prietenie = null;
        try{
            Long id_left = rs.getLong("id_left");
            Long id_right = rs.getLong("id_right");
            Date ldtStr = rs.getDate("data_prietenie");
            LocalDateTime ldt = toLdt(ldtStr);
            prietenie = new Prietenie(id_left,id_right,ldt);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenie;
    }
    @Override
    public String abstractSelect(){
        return new String("select * from prietenie");
    }
    @Override
    public String abstractInsert(){
        return new String("INSERT INTO prietenie(id_left,id_right,data_prietenie) values(?,?,?)");
    }
    @Override
    public void abstractInsertParameters(PreparedStatement stmt, Prietenie prietenie){
        try{
            long millis=System.currentTimeMillis();
            java.sql.Date date=new java.sql.Date(millis);
            stmt.setLong(1, prietenie.getId().getLeft());
            stmt.setLong(2, prietenie.getId().getRight());
            stmt.setDate(3, date);
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String abstractDelete(Tuple<Long,Long> id){
        return new String("DELETE FROM prietenie where id_left='" + id.getLeft().toString() + "' and id_right='" + id.getRight().toString() + "'");
    }
}
