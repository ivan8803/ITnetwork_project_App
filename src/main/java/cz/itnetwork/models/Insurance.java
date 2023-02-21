package cz.itnetwork.models;


import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;


public class Insurance {
    private int insuranceId;
    private String name;
    private int amount;
    private String subject;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date validFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date validUntil;
    private int ipId;


    public Insurance() {}


    public Insurance(int insuranceId, String name, int amount, String subject, Date validFrom, Date validUntil, int ipId) {
        this.insuranceId = insuranceId;
        this.name = name;
        this.amount = amount;
        this.subject = subject;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.ipId = ipId;
    }


    public int getInsuranceId() {
        return insuranceId;
    }
    public void setInsuranceId(int insurance_id) {
        this.insuranceId = insuranceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public Date getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
    public Date getValidUntil() {
        return validUntil;
    }
    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }
    public int getIpId() {
        return ipId;
    }
    public void setIpId(int ipId) {
        this.ipId = ipId;
    }
}
