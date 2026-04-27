package com.ebike.service.impl;

import com.ebike.entity.Station;
import com.ebike.mapper.StationMapper;
import com.ebike.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public List<Station> getAllStations() {
        return stationMapper.selectAll();
    }

    @Override
    public Station getStationById(Long id) {
        return stationMapper.selectById(id);
    }

    @Override
    public boolean addStation(Station station) {
        station.setCreateTime(new Date());
        return stationMapper.insert(station) > 0;
    }

    @Override
    public boolean updateStation(Station station) {
        return stationMapper.update(station) > 0;
    }

    @Override
    public boolean deleteStation(Long id) {
        return stationMapper.deleteById(id) > 0;
    }
}
