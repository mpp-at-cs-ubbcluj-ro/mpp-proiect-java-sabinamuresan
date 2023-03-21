package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Case;
import org.example.model.Donation;
import org.example.model.Donor;
import org.example.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DonorDbRepository implements IDonorRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public DonorDbRepository(Properties props){
        logger.info("Initializing DonorDbRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Donor findOne(Integer integer) {
        return null;
    }

    @Override
    public Iterable<Donor> getAll() {
        return null;
    }

    @Override
    public Donor add(Donor entity) {
        logger.traceEntry("saving donor {} ", entity);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement ps = con.prepareStatement("INSERT INTO Donors (name, address, phone_number) values (?,?,?)")) {
            ps.setString(1, entity.getDonorName());
            ps.setString(2, entity.getDonorAddress());
            ps.setString(3, entity.getDonorPhoneNumber());
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
    public Donor delete(Integer integer) {
        return null;
    }

    @Override
    public Donor update(Donor entity) {
        return null;
    }

    @Override
    public Iterable<Donor> getDonorsForCase(Case entity) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Donor> donors = new ArrayList<>();
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Donors INNER JOIN Donations D ON D.id_donor = Donors.id INNER JOIN Cases C ON D.id_case = C.id and C.id = ?")){
            ps.setInt(1,entity.getId());
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    String phoneNumber = rs.getString("phone_number");
                    Donor donor = new Donor(id, name, address, phoneNumber);
                    donors.add(donor);
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: " + ex);
        }
        logger.traceExit(donors);
        return donors;
    }
}
