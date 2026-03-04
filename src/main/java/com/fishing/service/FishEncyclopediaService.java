package com.fishing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fishing.pojo.entity.FishEncyclopediaEntity;
import com.fishing.pojo.vo.FishVO;
import org.springframework.web.multipart.MultipartFile;

public interface FishEncyclopediaService extends IService<FishEncyclopediaEntity> {

    FishVO identifyFish(MultipartFile image);
}
