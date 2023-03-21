package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Case;
import org.example.model.Donation;
import org.example.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DonationDbRepository implements IDonationRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public DonationDbRepository(Properties props){
        logger.info("Initializing DonationDbRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }
    @Override
    public Donation findOne(Integer integer) {
        return null;
    }

    @Override
    public Iterable<Donation> getAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Donation> donations = new ArrayList<>();
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Donations")){
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    int id = rs.getInt("id");
                    float amount = rs.getFloat("amount");
                    int idCase = rs.getInt("id_case");
                    int idDonor = rs.getInt("id_donor");
                    Donation donation = new Donation(id, idCase, idDonor, amount);
                    donations.add(donation);
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: " + ex);
        }
        logger.traceExit(donations);
        return donations;
    }

    @Override
    public Donation add(Donation entity) {
        logger.traceEntry("saving donation {} ", entity);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement ps = con.prepareStatement("INSERT INTO Donations (amount, id_case, id_donor) values (?,?,?)")) {
            ps.setFloat(1, entity.getAmount());
            ps.setInt(2, entity.getIdCase());
            ps.setInt(3, entity.getIdDonor());
            int result = ps.executeUpdate();
            logger.trace("Saved {} instances", result);
        }catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return entity;
    }

    @Override
    public Donation delete(Integer integer) {
        return null;
    }

    @Override
    public Donation update(Donation entity) {
        return null;
    }
}
