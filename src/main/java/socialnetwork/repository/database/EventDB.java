package socialnetwork.repository.database;

import socialnetwork.domain.Eveniment;
import socialnetwork.domain.Invite;
import socialnetwork.domain.InviteStatus;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepositoryOptional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;



public class EventDB extends AbstractDbRepository<Long, Eveniment>{

    public EventDB(String url, String username, String password, Validator<Eveniment> validator) {
        super(url, username,password,validator);
    }
    @Override
    public Eveniment extractEntity(ResultSet rs){
        Eveniment event = null;
        try{
            Long idevent = rs.getLong("idevent");

            String eventName = rs.getString("event_name");

            Timestamp ldtStr = rs.getTimestamp("date_event");
            LocalDateTime ldt = ldtStr.toLocalDateTime(); //toLdt(ldtStr);

            event = new Eveniment(idevent,eventName,ldt);


        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return event;
    }
    @Override
    public String abstractSelect(){
        return new String("select * from eveniment");
    }
    @Override
    public String abstractInsert(){
        return new String("INSERT INTO eveniment(idevent,event_name,date_event) values(?,?,?)");
    }
    @Override
    public void abstractInsertParameters(PreparedStatement stmt, Eveniment event){
        try{
            stmt.setLong(1, event.getId());
            stmt.setString(2,event.getNameEvent());
            stmt.setTimestamp(3,Timestamp.valueOf(event.getDataEvent()));
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String abstractDelete(Long id){
        return new String("DELETE FROM eveniment where idevent='" + id.toString() + "'");
    }

    @Override
    public String abstractUpdate(){
        //TODO
        return new String("");
    }
    public void abstractUpdateParameters(PreparedStatement stmt,Eveniment entity){
        //TODO
    }
}
