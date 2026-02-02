package com.scnsoft.eldermark.matcher.dao;

import com.scnsoft.eldermark.matcher.MpiMergedResidents;
import no.priv.garshol.duke.Record;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author knetkachou
 * @author phomal
 * Created on 1/25/2017.
 */
public class MpiDao {

    //todo implement connection pooling, mb use below
    //<dependency>
    //    <groupId>org.apache.commons</groupId>
    //    <artifactId>commons-dbcp2</artifactId>
    //    <version>2.8.0</version>
    //</dependency>

    private String url;
    private String username;
    private String password;
    private String driverClassName;

    boolean wasError = false;

    private static final String PROPERTY_FILE = "/datasource.properties";

    public MpiDao() {
        InputStream input = null;
        try {

            Properties props = new Properties();
            input = getClass().getResourceAsStream(PROPERTY_FILE);
            props.load(input);

            url = props.getProperty("datasource.url");
            username = props.getProperty("datasource.username");
            password = props.getProperty("datasource.password");
            driverClassName = props.getProperty("datasource.driverClassName");

        } catch (IOException ex) {
            ex.printStackTrace();
            wasError = true;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void insertMergedRecords(Record r1, Record r2, boolean maybe, double confidence) {
//        if ("select count(1) from MPI_merged_residents where surviving_resident_id = r1.id">0) {
//            if ("select count(1) from MPI_merged_residents where surviving_resident_id = r1.id and merged_resident_id = r2.id ">0) {
//                return; // либо они не мержатся, либо такая запись уже есть
//            }
//            insertMergedRecords(r1,r2);
//        }
//        else {
//            if ("select count(1) from MPI_merged_residents where surviving_resident_id = r2.id and merged_resident_id = r1.id "==0) {
//                insertMergedRecords(r2,r1);
//            }
//        }

        //todo review and validate logic

        Long id1 = Long.parseLong(r1.getValue("id"));
        Long id2 = Long.parseLong(r2.getValue("id"));

        boolean doNotInsert = true;
        boolean deleteMerges = false;

        List<MpiMergedResidents> existMerges = execFindExistingMerges(id1, id2);
        if (existMerges.isEmpty()) {
            doNotInsert = false;
        }
        for (MpiMergedResidents mergedResult : existMerges) {
            if (mergedResult.isMergedManually()) {
                continue;
            }
            if ((mergedResult.isMerged() && maybe) || (mergedResult.isProbablyMatched() && !maybe)
                    && mergedResult.isMergedAutomatically()) {
                deleteMerges = true;
                doNotInsert = false;
            }
        }
        if (isManuallyMismatched(id1, id2)) {
            doNotInsert = true; //if unmerged manually, it should have priority over automatic merges
        }

        if (doNotInsert && !deleteMerges) {
            return;
        }

        boolean pushUpdate = false;

        if (deleteMerges) {
            execDeleteMerges(id1, id2);
            pushUpdate = true;
        }
        if (!doNotInsert) {
            execInsertMerges(id1, id2, maybe, confidence);
            pushUpdate = pushUpdate || !maybe; //!maybe is merged=1
        }

        if (pushUpdate) {
            execPushResidentMergeUpdate(id1, id2);
        }
    }

    private int execDeleteMerges(Long id1, Long id2) {
        String sb = "delete from [MPI_merged_residents] " +
                "where ([surviving_resident_id] = ? and [merged_resident_id] = ?) " +
                "or ([surviving_resident_id] = ? and [merged_resident_id] = ?)";

        try (Connection connection = getDBConnection();
             PreparedStatement statement = connection.prepareStatement(sb)) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            System.out.println(sb);
            return statement.executeUpdate();
        } catch (SQLException e) {
            processCatch(e);
        }
        return 0;
    }

    private void execInsertMerges(Long id1, Long id2, boolean maybe, double confidence) {
        String insertTableSQL = "INSERT INTO [MPI_merged_residents] " +
                "([surviving_resident_id],[merged_resident_id],[merged],[probably_matched],[merged_automatically],[merged_manually],[duke_confidence]) " +
                "VALUES(?,?,?,?,1,0,?)";
        try (Connection connection = getDBConnection();
             PreparedStatement statement = connection.prepareStatement(insertTableSQL)) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setBoolean(3, !maybe);
            statement.setBoolean(4, maybe);
            statement.setDouble(5, confidence);
            System.out.println(insertTableSQL);

            // execute insert SQL statement
            statement.executeUpdate();

            System.out.println("Records inserted into MPI_merged_residents table!");
        } catch (SQLException exception) {
            processCatch(exception);
        }
    }

    private List<MpiMergedResidents> execFindExistingMerges(Long id1, Long id2) {
        String query = "select id,probably_matched,merged,merged_automatically,merged_manually from [MPI_merged_residents] " +
                "where ([surviving_resident_id] = ? and [merged_resident_id] = ?) " +
                "or ([surviving_resident_id] = ? and [merged_resident_id] = ?)";

        List<MpiMergedResidents> results = new ArrayList<MpiMergedResidents>();

        try (Connection dbConnection = getDBConnection();
             PreparedStatement statement = dbConnection.prepareStatement(query)) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    MpiMergedResidents mpiMergedResidents = new MpiMergedResidents();
                    mpiMergedResidents.setId(rs.getLong(1));
                    mpiMergedResidents.setProbablyMatched(rs.getBoolean(2));
                    mpiMergedResidents.setMerged(rs.getBoolean(3));
                    mpiMergedResidents.setMergedAutomatically(rs.getBoolean(4));
                    mpiMergedResidents.setMergedManually(rs.getBoolean(5));
                    results.add(mpiMergedResidents);
                }
            }
        } catch (SQLException e) {
            processCatch(e);
        }

        return results;
    }

    private boolean isManuallyMismatched(Long id1, Long id2) {
        String query = "SELECT 1 FROM [MPI_unmerged_residents] " +
                "WHERE ([first_resident_id] = ? AND [second_resident_id] = ?) " +
                "OR ([second_resident_id] = ? AND [first_resident_id] = ?)";

        try (Connection dbConnection = getDBConnection();
             PreparedStatement statement = dbConnection.prepareStatement(query)) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();   // returns false if there are no rows in ResultSet
            }

        } catch (SQLException e) {
            processCatch(e);
        }

        return false;
    }

    private Connection getDBConnection() throws SQLException {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            processCatch(e);
        }
        return DriverManager.getConnection(url, username, password);
    }

    public void updateMpiLog() {
        if (wasError) {
            wasError = false;
            return;
        }
        String insertTableSQL = "update MPI_log set last_matched = GETDATE()";

        try (Connection connection = getDBConnection();
             PreparedStatement statement = connection.prepareStatement(insertTableSQL)) {

            System.out.println(insertTableSQL);
            statement.executeUpdate();
        } catch (SQLException e) {
            processCatch(e);
        }
    }

    public boolean isFirstTimeMerge() {
        String query = "select count(1) from MPI_Log ml where last_matched is not null";

        try (Connection connection = getDBConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            System.out.println(query);
            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    return res.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            processCatch(e);
        }
        return true;
    }

    private void execPushResidentMergeUpdate(Long id1, Long id2) {
        final String query = "INSERT INTO ResidentUpdateQueue (resident_id, update_type, update_time) VALUES " +
                "(?, 'RESIDENT_MERGE', GETDATE()), (?, 'RESIDENT_MERGE', GETDATE())";

        try (Connection connection = getDBConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            System.out.println(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            processCatch(e);
        }
    }

    private void processCatch(Exception e) {
        e.printStackTrace();

        final Throwable[] suppressed = e.getSuppressed();
        for (int i = 0; i < suppressed.length; ++i) {
            System.out.println("Suppressed exception:");
            suppressed[i].printStackTrace();
        }

        wasError = true;
    }
}
