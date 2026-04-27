package com.ebike.service;

import com.ebike.entity.Station;
import java.util.List;

public interface StationService {
    List<Station> getAllStations();
    Station getStationById(Long id);
    boolean addStation(Station station);
    boolean updateStation(Station station);
    boolean deleteStation(Long id);
}
