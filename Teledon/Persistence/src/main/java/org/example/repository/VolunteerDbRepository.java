package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Case;
import org.example.model.Volunteer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class VolunteerDbRepository implements IVolunteerRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public VolunteerDbRepository(Properties props) {
        logger.info("Initializing VolunteerDbRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Volunteer findOne(Integer integer) {
        return null;
    }

    @Override
    public Iterable<Volunteer> getAll() {
        return null;
    }

    @Override
    public Volunteer add(Volunteer entity) {
        return null;
    }

    @Override
    public Volunteer delete(Integer integer) {
        return null;
    }

    @Override
    public Volunteer update(Volunteer entity) {
        return null;
    }

    @Override
    public Volunteer findAccount(String username, String password) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Volunteers WHERE username = ? AND password = ?")){
            ps.setString(1, username);
            ps.setString(2, password);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    int id = rs.getInt("id");
                    String volunteerUsername = rs.getString("username");
                    String volunteerPassword = rs.getString("password");

                    Volunteer volunteer = new Volunteer(id, volunteerUsername, volunteerPassword);
                    return volunteer;
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return null;
    }
}
