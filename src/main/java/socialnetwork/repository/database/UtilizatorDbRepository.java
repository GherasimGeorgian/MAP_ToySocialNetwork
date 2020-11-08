package socialnetwork.repository.database;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryOptional;

import java.sql.*;
import java.util.*;

public class UtilizatorDbRepository implements RepositoryOptional<Long, Utilizator> {
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Iterable<Utilizator> changeEntities(Map<Long,Utilizator> entities){
        return new HashSet<>();
    }

    @Override
    public Optional<Utilizator> findOne(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Id nu poate fi null");
        Utilizator utilizator = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users where id='"+aLong.toString()+"'");
             ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next() == true) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("firstname");
                    String lastName = resultSet.getString("lastname");

                    utilizator = new Utilizator(firstName, lastName);
                    utilizator.setId(id);
                    return Optional.ofNullable(utilizator);
                }

            else{
                return Optional.ofNullable(utilizator);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(utilizator);
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {

        if(entity == null){
            throw new IllegalArgumentException("Entitatea nu poate fi nula");
        }
        validator.validate(entity);


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = connection
                     .prepareStatement("INSERT INTO users(id,firstname,lastname) values(?,?,?)")) {
            stmt.setLong(1, entity.getId());
            stmt.setString(2, entity.getFirstName());
            stmt.setString(3, entity.getLastName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  Optional.ofNullable(entity);

    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        if(aLong == null){
            throw new IllegalArgumentException("Id-ul nu poate fi null!");
        }
        Optional<Utilizator> user = this.findOne(aLong);
        if(user.get() == null){
            throw new IllegalArgumentException("Userul nu exista!");
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = connection
                     .prepareStatement("DELETE FROM users where id='"+aLong.toString()+"'")) {
            int deleted = stmt.executeUpdate();
            if(deleted == 0)
                return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        if(entity == null){
            throw new IllegalArgumentException("Userul nu poate fi null!");
        }
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = connection
                     .prepareStatement("update users SET firstname=?, lastname=? where id='"+entity.getId().toString()+"'")) {
            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());
            stmt.executeUpdate();

            int updated = stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }
}
