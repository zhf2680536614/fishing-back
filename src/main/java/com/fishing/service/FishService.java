package com.fishing.service;

import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.FishEncyclopediaDTO;
import com.fishing.pojo.entity.FishEncyclopediaEntity;
import com.fishing.pojo.query.FishEncyclopediaPageQuery;

public interface FishService {
    PageResult<FishEncyclopediaEntity> pageFish(FishEncyclopediaPageQuery query);
    FishEncyclopediaEntity getFishById(Long id);
    void createFish(FishEncyclopediaDTO dto);
    void updateFish(Long id, FishEncyclopediaDTO dto);
    void deleteFish(Long id);
}
