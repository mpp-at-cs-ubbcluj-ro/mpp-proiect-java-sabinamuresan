package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Case;
import org.example.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CaseDbRepository implements ICaseRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public CaseDbRepository(Properties props){
        logger.info("Initializing CaseDbRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Case findOne(Integer integer) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Cases WHERE id = ?")){
            ps.setInt(1, integer);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    float sum = rs.getInt("sum");

                    Case myCase = new Case(id, name, sum);
                    return myCase;
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Iterable<Case> getAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Case> cases = new ArrayList<>();
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Cases")){
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    float sum = rs.getFloat("sum");
                    Case myCase = new Case(id, name, sum);
                        cases.add(myCase);
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: " + ex);
        }
        logger.traceExit(cases);
        return cases;
    }

    @Override
    public Case add(Case entity) {
        return null;
    }

    @Override
    public Case delete(Integer integer) {
        return null;
    }

    @Override
    public Case update(Case entity) {
        return null;
    }

    @Override
    public void updateSum(int id, float sum) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try(PreparedStatement ps = con.prepareStatement("UPDATE Cases SET sum=? WHERE id=?")) {
            ps.setFloat(1, sum);
            ps.setInt(2, id);
            int result = ps.executeUpdate();
            logger.trace("Saved {} instances", result);
        }catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }
}
