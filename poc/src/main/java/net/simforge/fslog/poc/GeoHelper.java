package net.simforge.fslog.poc;

import net.simforge.commons.gckls2com.GC;
import net.simforge.commons.gckls2com.GCAirport;
import net.simforge.commons.misc.Geo;

import java.io.IOException;

public class GeoHelper {
    public static double getDistance(String icao1, String icao2) {
        Geo.Coords coords1 = getCoords(icao1);
        Geo.Coords coords2 = getCoords(icao2);
        return Geo.distance(coords1, coords2);
    }

    private static Geo.Coords getCoords(String icao) {
        GCAirport airport;
        try {
            airport = GC.findAirport(icao);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Geo.Coords(airport.getLat(), airport.getLon());
    }
}
