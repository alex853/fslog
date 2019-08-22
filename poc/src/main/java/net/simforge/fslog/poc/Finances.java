package net.simforge.fslog.poc;

public class Finances {
    private SponsorType sponsorType;
    private String sponsorName;
    private Integer fseEarnings;

    public enum SponsorType {
        SELF,
        AIRLINE
    }
}
