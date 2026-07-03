
package org.example;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AddressBookApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ContactDAO dao = new ContactDAOImpl();

        while (true) {

            System.out.println("\n===== ADDRESS BOOK MENU =====");
            System.out.println("1. Add Contact");
            System.out.println("2. View All Contacts");
            System.out.println("3. Search Contact");
            System.out.println("4. Update Contact");
            System.out.println("5. Delete Contact");
            System.out.println("6. Import Contacts from CSV");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try {

                switch (choice) {

                    case 1:

                        Contact contact = new Contact();

                        System.out.print("First Name: ");
                        contact.setFirstName(sc.nextLine());

                        System.out.print("Last Name: ");
                        contact.setLastName(sc.nextLine());

                        System.out.print("Phone: ");
                        contact.setPhone(sc.nextLine());

                        System.out.print("Email: ");
                        contact.setEmail(sc.nextLine());

                        System.out.print("Address: ");
                        contact.setAddress(sc.nextLine());

                        dao.addContact(contact);

                        System.out.println("Contact Added Successfully.");
                        break;

                    case 2:

                        List<Contact> contacts = dao.getAllContacts();

                        for (Contact c : contacts) {
                            System.out.println(c);
                        }

                        break;

                    case 3:

                        System.out.print("Enter keyword: ");
                        String keyword = sc.nextLine();

                        List<Contact> result = dao.searchContacts(keyword);

                        for (Contact c : result) {
                            System.out.println(c);
                        }

                        break;

                    case 4:

                        Contact update = new Contact();

                        System.out.print("Enter ID: ");
                        update.setId(sc.nextInt());
                        sc.nextLine();

                        System.out.print("First Name: ");
                        update.setFirstName(sc.nextLine());

                        System.out.print("Last Name: ");
                        update.setLastName(sc.nextLine());

                        System.out.print("Phone: ");
                        update.setPhone(sc.nextLine());

                        System.out.print("Email: ");
                        update.setEmail(sc.nextLine());

                        System.out.print("Address: ");
                        update.setAddress(sc.nextLine());

                        dao.updateContact(update);

                        System.out.println("Contact Updated.");
                        break;

                    case 5:

                        System.out.print("Enter Contact ID: ");
                        int id = sc.nextInt();

                        dao.deleteContact(id);

                        System.out.println("Contact Deleted.");
                        break;

                    case 6:

                        System.out.println("1. Import contacts.csv");
                        System.out.println("2. Import contacts_invalid.csv");
                        System.out.print("Choose file: ");

                        int fileChoice = sc.nextInt();
                        sc.nextLine();

                        String fileName;

                        if (fileChoice == 1) {
                            fileName = "resources/contacts.csv";
                        } else {
                            fileName = "resources/contacts_invalid.csv";
                        }

                        List<Contact> csvContacts = CsvParser.parseContacts(fileName);

                        dao.importContactsBatch(csvContacts);

                        System.out.println(csvContacts.size() + " Contacts Imported Successfully.");

                        break;

                    case 7:

                        System.out.println("Thank You!");
                        sc.close();
                        System.exit(0);

                    default:

                        System.out.println("Invalid Choice.");

                }

            } catch (SQLException e) {

                System.out.println("Database Error : " + e.getMessage());

            } catch (Exception e) {

                System.out.println("Error : " + e.getMessage());
            }
        }
    }
}
