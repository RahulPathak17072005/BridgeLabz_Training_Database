package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class CsvParser {

    public static List<Contact> parseContacts(String fileName) throws IOException {

        List<Contact> contacts = new ArrayList<>();

        InputStream inputStream = CsvParser.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IOException("CSV file not found: " + fileName);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            br.readLine();

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length < 5) {
                    continue;
                }

                Contact contact = new Contact();

                contact.setFirstName(data[0].trim());
                contact.setLastName(data[1].trim());
                contact.setPhone(data[2].trim());
                contact.setEmail(data[3].trim());
                contact.setAddress(data[4].trim());

                contacts.add(contact);
            }
        }

        return contacts;
    }
}