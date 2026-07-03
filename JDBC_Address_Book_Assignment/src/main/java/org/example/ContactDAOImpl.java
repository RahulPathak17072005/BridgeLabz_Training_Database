package org.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ContactDAOImpl implements ContactDAO {
    @Override
    public void addContact(Contact contact) throws SQLException {

        String sql = "INSERT INTO contacts(first_name,last_name,phone,email,address) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getLastName());
            ps.setString(3, contact.getPhone());
            ps.setString(4, contact.getEmail());
            ps.setString(5, contact.getAddress());

            ps.executeUpdate();
        }
    }

    @Override
    public List<Contact> getAllContacts() throws SQLException {

        List<Contact> contacts = new ArrayList<>();

        String sql = "SELECT * FROM contacts ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Contact contact = new Contact();

                contact.setId(rs.getInt("id"));
                contact.setFirstName(rs.getString("first_name"));
                contact.setLastName(rs.getString("last_name"));
                contact.setPhone(rs.getString("phone"));
                contact.setEmail(rs.getString("email"));
                contact.setAddress(rs.getString("address"));

                contacts.add(contact);
            }
        }

        return contacts;
    }

    @Override
    public List<Contact> searchContacts(String keyword) throws SQLException {

        List<Contact> contacts = new ArrayList<>();

        String sql = "SELECT * FROM contacts WHERE first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Contact contact = new Contact();

                    contact.setId(rs.getInt("id"));
                    contact.setFirstName(rs.getString("first_name"));
                    contact.setLastName(rs.getString("last_name"));
                    contact.setPhone(rs.getString("phone"));
                    contact.setEmail(rs.getString("email"));
                    contact.setAddress(rs.getString("address"));

                    contacts.add(contact);
                }
            }
        }

        return contacts;
    }

    @Override
    public void updateContact(Contact contact) throws SQLException {

        String sql = "UPDATE contacts SET first_name=?, last_name=?, phone=?, email=?, address=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getLastName());
            ps.setString(3, contact.getPhone());
            ps.setString(4, contact.getEmail());
            ps.setString(5, contact.getAddress());
            ps.setInt(6, contact.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void deleteContact(int id) throws SQLException {

        String sql = "DELETE FROM contacts WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void importContactsBatch(List<Contact> contacts) throws SQLException {

        String sql = "INSERT INTO contacts(first_name,last_name,phone,email,address) VALUES(?,?,?,?,?)";

        Connection conn = null;

        try {

            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                for (Contact contact : contacts) {

                    ps.setString(1, contact.getFirstName());
                    ps.setString(2, contact.getLastName());
                    ps.setString(3, contact.getPhone());
                    ps.setString(4, contact.getEmail());
                    ps.setString(5, contact.getAddress());

                    ps.addBatch();
                }

                ps.executeBatch();
                conn.commit();
            }

        } catch (SQLException e) {

            if (conn != null) {
                conn.rollback();
            }

            throw e;

        } finally {

            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}