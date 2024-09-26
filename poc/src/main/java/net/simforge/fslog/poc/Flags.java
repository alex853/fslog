package net.simforge.fslog.poc;

public class Flags {
    private Network network;
    private Boolean fse;
    private String airline;

    private Flags() {
    }

    public Network getNetwork() {
        return network;
    }

    public Boolean getFse() {
        return fse;
    }

    public String getAirline() {
        return airline;
    }

    public static class Builder {
        private Flags flags = new Flags();

        public Builder() {
        }

        public Builder(Flags flags) {
            this.flags = copy(flags);
        }

        public Builder setNetwork(Network network) {
            this.flags.network = network;
            return this;
        }

        public Builder setFse(Boolean fse) {
            this.flags.fse = fse;
            return this;
        }

        public Builder setAirline(String airline) {
            this.flags.airline = airline;
            return this;
        }

        public Flags build() {
            return copy(flags);
        }

        private Flags copy(Flags source) {
            Flags copy = new Flags();
            copy.network = source.network;
            copy.fse = source.fse;
            copy.airline = source.airline;
            return copy;
        }
    }

    public enum Network {
        Offline("Offline"),
        VATSIM("VATSIM"),
        IVAO("IVAO"),
        PilotEdge("PilotEdge");

        private String code;

        Network(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        public static Network byCode(String code) {
            for (Network network : values()) {
                if (network.code.equalsIgnoreCase(code)) {
                    return network;
                }
            }
            return null;
        }
    }
}
