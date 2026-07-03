package org.example;
import java.util.List;
public interface ContactDAO {
    // Add a new contact
    void addContact(Contact contact) throws Exception;

    // Retrieve all contacts
    List<Contact> getAllContacts() throws Exception;

    // Search contacts by name, phone, or email
    List<Contact> searchContacts(String keyword) throws Exception;

    // Update an existing contact
    void updateContact(Contact contact) throws Exception;

    // Delete a contact by ID
    void deleteContact(int id) throws Exception;

    // Import multiple contacts from a CSV file
    void importContactsBatch(List<Contact> contacts) throws Exception;
}