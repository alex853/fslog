package net.simforge.fslog.poc;

public class Finances {
    private SponsorType sponsorType;
    private String sponsorName;
    private Integer fseEarnings;

    private Finances() {
    }

    public SponsorType getSponsorType() {
        return sponsorType;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public Integer getFseEarnings() {
        return fseEarnings;
    }

    public static class Builder {
        private Finances finances = new Finances();

        public Builder() {
        }

        public Builder(Finances finances) {
            this.finances = copy(finances);
        }

        public Builder setSponsorType(SponsorType sponsorType) {
            this.finances.sponsorType = sponsorType;
            return this;
        }

        public Builder setSponsorName(String sponsorName) {
            this.finances.sponsorName = sponsorName;
            return this;
        }

        public Builder setFseEarnings(Integer fseEarnings) {
            this.finances.fseEarnings = fseEarnings;
            return this;
        }

        public Finances build() {
            return copy(finances);
        }

        private Finances copy(Finances source) {
            Finances copy = new Finances();
            copy.sponsorType = source.sponsorType;
            copy.sponsorName = source.sponsorName;
            copy.fseEarnings = source.fseEarnings;
            return copy;
        }

    }

    public enum SponsorType {
        SELF("self"),
        AIRLINE("airline");

        private String code;

        SponsorType(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        public static SponsorType byCode(String code) {
            for (SponsorType sponsorType : values()) {
                if (sponsorType.code.equalsIgnoreCase(code)) {
                    return sponsorType;
                }
            }
            return null;
        }
    }
}
