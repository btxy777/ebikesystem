package com.ebike.controller;

import com.ebike.common.Response;
import com.ebike.entity.Station;
import com.ebike.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/list")
    public Response<Map<String, Object>> getStationList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Station> stations = stationService.getAllStations();
        int total = stations.size();
        Map<String, Object> result = new HashMap<>();
        if (total == 0) {
            result.put("data", stations);
            result.put("total", 0);
            result.put("page", page);
            result.put("size", size);
            return Response.success(result);
        }
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            start = 0;
        }
        List<Station> pageData = stations.subList(start, end);
        result.put("data", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return Response.success(result);
    }

    @GetMapping("/{id}")
    public Response<Station> getStationById(@PathVariable Long id) {
        Station station = stationService.getStationById(id);
        if (station != null) {
            return Response.success(station);
        }
        return Response.error("站点不存在");
    }

    @PostMapping("/add")
    public Response<String> addStation(@RequestBody Station station) {
        boolean result = stationService.addStation(station);
        if (result) {
            return Response.success("添加成功");
        }
        return Response.error("添加失败");
    }

    @PutMapping("/update")
    public Response<String> updateStation(@RequestBody Station station) {
        boolean result = stationService.updateStation(station);
        if (result) {
            return Response.success("更新成功");
        }
        return Response.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteStation(@PathVariable Long id) {
        boolean result = stationService.deleteStation(id);
        if (result) {
            return Response.success("删除成功");
        }
        return Response.error("删除失败");
    }
}
