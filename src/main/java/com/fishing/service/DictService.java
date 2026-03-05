package com.fishing.service;

import com.fishing.pojo.entity.DictItemEntity;

import java.util.List;

public interface DictService {
    List<DictItemEntity> getDictItemsByCode(String dictCode);
}
