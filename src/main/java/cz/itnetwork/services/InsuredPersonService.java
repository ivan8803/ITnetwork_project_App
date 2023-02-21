package cz.itnetwork.services;

import cz.itnetwork.models.Insurance;
import cz.itnetwork.models.InsuredPerson;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class InsuredPersonService {

    /**
     * Metoda, která vrací všechny pojištěnce v databázi
     * @return
     * @throws SQLException
     */
    public List<InsuredPerson> getAllInsuredPersons() throws SQLException {
        List<InsuredPerson> insuredPersons = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("SELECT * FROM insured_persons");
                ResultSet resultSet = query.executeQuery();
                ){
            while (resultSet.next()) {
                int ip_id = resultSet.getInt("ip_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String street = resultSet.getString("street");
                String city = resultSet.getString("city");
                String postcode = resultSet.getString("postcode");
                insuredPersons.add(new InsuredPerson(ip_id, firstName, lastName, email, phone, street, city, postcode));
            }
            return insuredPersons;
        }
    }

    /**
     * Vrací počet všech pojistníků v databázi
     * @return
     * @throws SQLException
     */
    public int getNumberOfElements() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("SELECT COUNT(*) as number_of_elements FROM insured_persons");
                ResultSet resultSet = query.executeQuery();
        ){
            resultSet.next();
            return resultSet.getInt("number_of_elements");
        }
    }

    /**
     * Metoda, která vrací výsek z databáze pro zobrazení na dané stránce HTML stránkovaného seznamu pojistníků
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws SQLException
     */
    public List<InsuredPerson> findPaginated(int pageNumber, int pageSize) throws SQLException {
        List<InsuredPerson> insuredPersons = getAllInsuredPersons();
        int totalElements = insuredPersons.size();
        int sublistStartIndex = (pageNumber - 1) * pageSize;
        int sublistEndIndex = totalElements;
        if (totalElements> sublistStartIndex + pageSize)
            sublistEndIndex = sublistStartIndex + pageSize;

        return insuredPersons.subList(sublistStartIndex, sublistEndIndex);
    }

    /**
     * Metoda, která vrací celkový počet stran stránkované HTML
     * @param pageSize
     * @return
     * @throws SQLException
     */
    public int getTotalPages(int pageSize) throws SQLException {
        return (int) Math.ceil((double) getNumberOfElements() / pageSize);
    }

    /**
     * Metoda, která vyhledá a vrátí pojistníka z databáze podle ID
     * @param ipId - ID pojistníka
     * @return
     * @throws SQLException
     */
    public InsuredPerson getInsuredPersonByID(String ipId) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("SELECT * FROM insured_persons WHERE ip_id=?")
                ){
            query.setString(1, ipId);
            try (ResultSet resultSet = query.executeQuery()) {
                resultSet.next();
                int ip_id = resultSet.getInt("ip_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String street = resultSet.getString("street");
                String city = resultSet.getString("city");
                String postcode = resultSet.getString("postcode");
                return new InsuredPerson(ip_id, firstName, lastName, email, phone, street, city, postcode);
            }
        }
    }

    /**
     * Metoda, která vrací všechna pojištění pojistníka s daným ID.
     * @param ipId - ID pojistníka
     * @return
     * @throws SQLException
     */
    public List<Insurance> getIPInsurances(String ipId) throws SQLException {
        List<Insurance> insurances = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("SELECT * FROM insurances WHERE ip_id=?");
                ) {
            query.setString(1, ipId);
            try (ResultSet resultSet = query.executeQuery()) {
                while (resultSet.next()) {
                    int insuranceId = resultSet.getInt("insurance_id");
                    String name = resultSet.getString("name");
                    int amount = resultSet.getInt("amount");
                    String subject = resultSet.getString("subject");
                    Date validFrom = resultSet.getDate("valid_from");
                    Date validUntil = resultSet.getDate("valid_until");
                    insurances.add(new Insurance(insuranceId, name, amount, subject, validFrom, validUntil, Integer.parseInt(ipId)));
                }
                return insurances;
            }
        }
    }

    /**
     * Metoda, která uloží pojistníka do databáze
     * @param insuredPerson
     * @throws SQLException
     */
    public void addInsuredPerson(InsuredPerson insuredPerson) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("INSERT INTO insured_persons " +
                        "(first_name, last_name, email, phone, street, city, postcode) VALUES (?, ?, ?, ?, ?, ?, ?)");
                ) {
            query.setString(1, insuredPerson.getFirstName());
            query.setString(2, insuredPerson.getLastName());
            query.setString(3, insuredPerson.getEmail());
            query.setString(4, insuredPerson.getPhone().replace(" ", ""));
            query.setString(5, insuredPerson.getStreet());
            query.setString(6, insuredPerson.getCity());
            query.setString(7, insuredPerson.getPostcode().replace(" ", ""));
            query.executeUpdate();
        }
    }

    /**
     * Metoda, která edituje pojistníka se zadaným ID
     * @param ipId - ID pojistníka
     * @param insuredPerson
     * @throws SQLException
     */
    public void editInsuredPerson(String ipId, InsuredPerson insuredPerson) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("UPDATE insured_persons SET " +
                        "first_name=?, last_name=?, email=?, phone=?, street=?, city=?, postcode=? WHERE ip_id=?");
        ) {
            query.setString(1, insuredPerson.getFirstName());
            query.setString(2, insuredPerson.getLastName());
            query.setString(3, insuredPerson.getEmail());
            query.setString(4, insuredPerson.getPhone().replace(" ", ""));
            query.setString(5, insuredPerson.getStreet());
            query.setString(6, insuredPerson.getCity());
            query.setString(7, insuredPerson.getPostcode().replace(" ", ""));
            query.setString(8, ipId);
            query.executeUpdate();
        }
    }

    /**
     * Metoda, která odstraní pojistníka s daným ID z databáze
     * @param ipId - ID pojistníka
     * @throws SQLException
     */
    public void deleteInsuredPerson(String ipId) throws SQLException {
        try (
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
            PreparedStatement query = connection.prepareStatement("DELETE FROM insured_persons WHERE ip_id=?");
        ) {
            query.setString(1, ipId);
            query.executeUpdate();
        }
    }


}
