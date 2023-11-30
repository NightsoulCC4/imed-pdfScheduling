package com.main.pdfScheduling.Repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class PDFRepository {

    @Value("${jdbcurl}")
    private String jdbcurl;

    @Value("${db_user}")
    private String db_user;

    @Value("${password}")
    private String password;

    // Search patient id from database which they are active.
    public String searchPatientIdByHNCode(String hncode){

        String data = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT patient_id " +
                       "FROM public.patient " +
                       "WHERE hncode = ? " +
                       "AND active = '1';";

        try{
            Class.forName("org.postgresql.Driver");

            con = DriverManager.getConnection(jdbcurl, db_user, password);
            ps = con.prepareStatement(query);
            ps.setString(1, hncode);

            rs = ps.executeQuery();

            for (; rs.next();) 
                data = rs.getString("patient_id");

            rs.close();
            rs = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
