package com.ebike.mapper;

import com.ebike.entity.Station;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StationMapper {
    List<Station> selectAll();
    Station selectById(Long id);
    int insert(Station station);
    int update(Station station);
    int deleteById(Long id);
    Station selectRandomStation();
}
