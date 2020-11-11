package socialnetwork.repository.database;

import jdk.jshell.execution.Util;
import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepositoryOptional;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Date;

public class MessageDB  extends AbstractDbRepository<Long, Message>{


    public RepositoryOptional<Long,Utilizator> userDateBase;

    public MessageDB(String url, String username, String password, Validator<Message> validator) {
        super(url, username,password,validator);
    }

    static public LocalDateTime toLdt(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        ZonedDateTime zdt = cal.toZonedDateTime();
        return zdt.toLocalDateTime();
    }

    @Override
    public Message extractEntity(ResultSet rs){
        userDateBase = new UtilizatorDB(url,username,password,new UtilizatorValidator());
        Message message = null;
        try{
            Long id = rs.getLong("id");
            Long fromUserId = rs.getLong("from_user");
            String toUsers = rs.getString("to_user");
            String msg = rs.getString("message");
            Timestamp ldtStr = rs.getTimestamp("date");
            Long replyMsg = rs.getLong("reply");
            LocalDateTime ldt = ldtStr.toLocalDateTime(); //toLdt(ldtStr);


            String[] toUsersOneByOne = toUsers.split(",");
            List<Utilizator> toUsersList = new ArrayList<>();

            for(String user: toUsersOneByOne){
                toUsersList.add(userDateBase.findOne(Long.parseLong(user)).get());
            }

            if(replyMsg == 0) {
                message = new Message(id, userDateBase.findOne(fromUserId).get(), toUsersList, msg, ldt);
            }
            else
            {
                Message mesajRaspuns=super.findOne(replyMsg).get();
                Message mesajBaza = new Message(id, userDateBase.findOne(fromUserId).get(), toUsersList, msg, ldt);
                message = new ReplyMessage(mesajBaza,mesajRaspuns.getId(),mesajRaspuns.getFrom(),mesajRaspuns.getTo(),mesajRaspuns.getMessage(),mesajRaspuns.getDate());
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return message;
    }
    @Override
    public String abstractSelect(){
        return new String("select * from message");
    }
    @Override
    public String abstractInsert(){
        return new String("INSERT INTO public.message(id, from_user, to_user, message, date, reply)VALUES (?,?,?,?,?,?)");
    }
    @Override
    public void abstractInsertParameters(PreparedStatement stmt, Message message){
        try{
            if(message instanceof Message){
                stmt.setLong(1, message.getId());
                stmt.setLong(2, message.getFrom().getId());

                String toUsers = new String();
                int i=0;
                for(Utilizator user: message.getTo()){
                    if(i==0){
                        toUsers = user.getId().toString();
                    }
                    else {
                        toUsers += toUsers + "," + user.getId().toString();
                    }
                    i++;
                }

                stmt.setString(3, toUsers);
                stmt.setString(4, message.getMessage());
                stmt.setTimestamp(5,Timestamp.valueOf(message.getDate()));
                stmt.setNull(6, Types.BIGINT);
            }
            if(message instanceof ReplyMessage){
                ReplyMessage rplmsg = (ReplyMessage)message;

                stmt.setLong(1, message.getId());
                stmt.setLong(2, message.getFrom().getId());

                String toUsers = new String();
                int i=0;
                for(Utilizator user: message.getTo()){
                    if(i==0){
                        toUsers = user.getId().toString();
                    }
                    else {
                        toUsers += toUsers + "," + user.getId().toString();
                    }
                    i++;
                }
                stmt.setString(3, toUsers);
                stmt.setString(4, message.getMessage());
                stmt.setTimestamp(5,Timestamp.valueOf(message.getDate()));
                stmt.setLong(6, rplmsg.getReplyMessage().getId());
            }

        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String abstractDelete(Long id){
        return new String("DELETE FROM message where id='" + id.toString() + "'");
    }


}
