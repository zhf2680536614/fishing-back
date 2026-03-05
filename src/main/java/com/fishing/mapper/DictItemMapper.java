package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.DictItemEntity;

import java.util.List;

public interface DictItemMapper extends BaseMapper<DictItemEntity> {
    List<DictItemEntity> selectByDictCode(String dictCode);
}
