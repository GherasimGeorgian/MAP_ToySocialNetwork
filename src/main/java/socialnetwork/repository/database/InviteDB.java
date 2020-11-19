package socialnetwork.repository.database;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepositoryOptional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InviteDB extends AbstractDbRepository<Long, Invite>{

    public RepositoryOptional<Long,Utilizator> userDateBase;

    public InviteDB(String url, String username, String password, Validator<Invite> validator) {
        super(url, username,password,validator);
    }
    @Override
    public Invite extractEntity(ResultSet rs){
        userDateBase = new UtilizatorDB(url,username,password,new UtilizatorValidator());
        Invite invite = null;
        try{
            Long id = rs.getLong("id");

            Long fromInviteId = rs.getLong("frominvite");
            Long toInviteId = rs.getLong("toinvite");

            Timestamp ldtStr = rs.getTimestamp("date_invite");
            LocalDateTime ldt = ldtStr.toLocalDateTime(); //toLdt(ldtStr);

            String status = rs.getString("status");

            InviteStatus statusStr = InviteStatus.valueOf(status);
            if(InviteStatus.APPROVED.equals(statusStr)){
                invite = new Invite(id,userDateBase.findOne(fromInviteId).get(),userDateBase.findOne(toInviteId).get(),ldt,InviteStatus.APPROVED);
            }
            if(InviteStatus.PENDING.equals(statusStr)){
                invite = new Invite(id,userDateBase.findOne(fromInviteId).get(),userDateBase.findOne(toInviteId).get(),ldt,InviteStatus.PENDING);
            }
            if(InviteStatus.REJECTED.equals(statusStr)){
                invite = new Invite(id,userDateBase.findOne(fromInviteId).get(),userDateBase.findOne(toInviteId).get(),ldt,InviteStatus.REJECTED);
            }

        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return invite;
    }
    @Override
    public String abstractSelect(){
        return new String("select * from invitations");
    }
    @Override
    public String abstractInsert(){
        return new String("INSERT INTO invitations(id,frominvite,toinvite,date_invite,status) values(?,?,?,?,?)");
    }
    @Override
    public void abstractInsertParameters(PreparedStatement stmt, Invite invite){
        try{
           stmt.setLong(1, invite.getId());
           stmt.setLong(2,invite.getFromInvite().getId());
           stmt.setLong(3,invite.getToInvite().getId());
           stmt.setTimestamp(4,Timestamp.valueOf(invite.getDateInvite()));
           String status = new String();
            if(InviteStatus.APPROVED.equals(invite.getStatus())){
                status = InviteStatus.APPROVED.toString();
            }
            if(InviteStatus.PENDING.equals(invite.getStatus())){
                status = InviteStatus.PENDING.toString();
            }
            if(InviteStatus.REJECTED.equals(invite.getStatus())){
                status = InviteStatus.REJECTED.toString();
            }
            stmt.setString(5,status);
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String abstractDelete(Long id){
        return new String("DELETE FROM invitations where id='" + id.toString() + "'");
    }

    @Override
    public String abstractUpdate(){
        return new String("UPDATE invitations SET id = ?," +
                " frominvite = ?," +
                "toinvite = ?," +
                "date_invite=?," +
                "status=?" +
                " WHERE id = ?");
    }
    public void abstractUpdateParameters(PreparedStatement stmt,Invite entity){
        try{
            stmt.setLong(1, entity.getId());
            stmt.setLong(2,entity.getFromInvite().getId());
            stmt.setLong(3,entity.getToInvite().getId());
            stmt.setTimestamp(4,Timestamp.valueOf(entity.getDateInvite()));
            String status = new String();
            if(InviteStatus.APPROVED.equals(entity.getStatus())){
                status = InviteStatus.APPROVED.toString();
            }
            if(InviteStatus.PENDING.equals(entity.getStatus())){
                status = InviteStatus.PENDING.toString();
            }
            if(InviteStatus.REJECTED.equals(entity.getStatus())){
                status = InviteStatus.REJECTED.toString();
            }
            stmt.setString(5,status);
            stmt.setLong(6,entity.getId());
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }
}
