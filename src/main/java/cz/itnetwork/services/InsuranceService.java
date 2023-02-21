package cz.itnetwork.services;

import cz.itnetwork.models.Insurance;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class InsuranceService {

    /**
     * Metoda, která vrací seznam polí, která obsahují data o pojištění i s pojištěncem, jež jsou vypisována
     * na stránku se seznamem pojištění
     * @return
     * @throws SQLException
     */
    public List<String[]> getAllInsurances() throws SQLException {
        List<String[]> insurances = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("SELECT insurance_id, name, amount, subject, first_name, last_name, insurances.ip_id " +
                        "FROM insurances JOIN insured_persons ON insured_persons.ip_id = insurances.ip_id");
                ResultSet resultSet = query.executeQuery();
        ){
            while (resultSet.next()) {
                String insurance_id = resultSet.getString("insurance_id");
                String name = resultSet.getString("name");
                String amount = resultSet.getString("amount");
                String subject = resultSet.getString("subject");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String ipId = resultSet.getString("ip_id");
                String[] row = {name, amount, subject, firstName, lastName, insurance_id, ipId};
                insurances.add(row);
            }
            return insurances;
        }
    }

    /**
     * Metoda, která vrací výsek ze seznamu pojištění, pro zobrazení na stránkované HTML
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws SQLException
     */
    public List<String[]> findPaginated(int pageNumber, int pageSize) throws SQLException {
        List<String[]> insurances = getAllInsurances();
        int totalElements = insurances.size();
        int sublistStartIndex = (pageNumber - 1) * pageSize;
        int sublistEndIndex = totalElements;
        if (totalElements> sublistStartIndex + pageSize)
            sublistEndIndex = sublistStartIndex + pageSize;

        return insurances.subList(sublistStartIndex, sublistEndIndex);
    }

    /**
     * Vrací celkový počet stran pro metodu stránkování
     * @param pageSize
     * @return
     * @throws SQLException
     */
    public int getTotalPages(int pageSize) throws SQLException {
        return (int) Math.ceil((double) getNumberOfElements() / pageSize);
    }

    /**
     * Vrací celkový počet pojištění v databázi
     * @return
     * @throws SQLException
     */
    public int getNumberOfElements() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("SELECT COUNT(*) as number_of_elements FROM insurances");
                ResultSet resultSet = query.executeQuery();
        ){
            resultSet.next();
            return resultSet.getInt("number_of_elements");
        }
    }

    /**
     * Metoda, která do datábaze vkládá nové pojištění
     * @param ipId - ID pojistníka
     * @param insurance
     * @param model
     * @throws SQLException
     */
    public void addInsurance(String ipId, Insurance insurance, Model model) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("INSERT INTO insurances (" +
                        "`name`, `amount`, `subject`, `valid_from`, `valid_until`, `ip_id`) VALUES (?, ?, ?, ?, ?, ?)");
        ) {
            query.setString(1, insurance.getName());
            query.setInt(2, insurance.getAmount());
            query.setString(3, insurance.getSubject());
            query.setDate(4, new Date(insurance.getValidFrom().getTime()));
            query.setDate(5, new Date(insurance.getValidUntil().getTime()));
            query.setString(6, ipId);

            if (insurance.getValidFrom().before(insurance.getValidUntil())) {
                query.executeUpdate();
                model.addAttribute("successMessage", "Bylo přidáno nové pojištění");
            } else {
                model.addAttribute("dangerMessage", "Datum konce platnosti pojištění musí být vyšší než datum vzniku pojištění.");
            }
        }
    }

    /**
     * Metoda, která z databáze odstraňuje pojistění podle ID
     * @param insuranceId - ID pojištění
     * @throws SQLException
     */
    public void deleteInsurance(String insuranceId) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("DELETE FROM insurances WHERE insurance_id=?")
                ) {
            query.setString(1, insuranceId);
            query.executeUpdate();
        }
    }

    /**
     * Metoda, která edituje pojištění podle ID
     * @param insuranceId - ID pojištění
     * @param insurance
     * @param model
     * @throws SQLException
     */
    public void editInsurance(String insuranceId, Insurance insurance, Model model) throws SQLException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("UPDATE insurances SET name=?, amount=?, subject=?, valid_from=?, valid_until=? WHERE insurance_id=?")
                ) {
            query.setString(1, insurance.getName());
            query.setInt(2, insurance.getAmount());
            query.setString(3, insurance.getSubject());
            query.setDate(4, new Date(insurance.getValidFrom().getTime()));
            query.setDate(5, new Date(insurance.getValidUntil().getTime()));
            query.setString(6, insuranceId);

            if (insurance.getValidFrom().before(insurance.getValidUntil())) {
                query.executeUpdate();
                model.addAttribute("successMessage", "Pojištění bylo upraveno");
            } else {
                model.addAttribute("dangerMessage", "Datum konce platnosti pojištění musí být vyšší než datum vzniku pojištění.");
            }
        }
    }

    /**
     * Metoda, která podle ID vyhledá a vrátí pojištění z databáze
     * @param insuranceId - ID pojištění
     * @return
     * @throws SQLException
     * @throws NumberFormatException
     */
    public Insurance getInsuranceByID(String insuranceId) throws SQLException, NumberFormatException {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insurance_basic", "root", "");
                PreparedStatement query = connection.prepareStatement("SELECT * FROM insurances WHERE insurance_id=?")
                ) {
            query.setString(1, insuranceId);
            try (ResultSet resultSet = query.executeQuery()) {
                resultSet.next();
                String name = resultSet.getString("name");
                int amount = resultSet.getInt("amount");
                String subject = resultSet.getString("subject");
                Date validFrom = resultSet.getDate("valid_from");
                Date validUntil = resultSet.getDate("valid_until");
                int ipId = resultSet.getInt("ip_id");
                return new Insurance(Integer.parseInt(insuranceId), name, amount, subject, validFrom, validUntil, ipId);
            }
        }
    }
}
