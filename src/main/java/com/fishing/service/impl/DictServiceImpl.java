package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fishing.mapper.DictItemMapper;
import com.fishing.mapper.DictTypeMapper;
import com.fishing.pojo.entity.DictItemEntity;
import com.fishing.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DictServiceImpl implements DictService {
    private final DictItemMapper dictItemMapper;
    private final DictTypeMapper dictTypeMapper;

    public DictServiceImpl(DictItemMapper dictItemMapper, DictTypeMapper dictTypeMapper) {
        this.dictItemMapper = dictItemMapper;
        this.dictTypeMapper = dictTypeMapper;
    }

    @Override
    public List<DictItemEntity> getDictItemsByCode(String dictCode) {
        log.info("获取字典项，字典编码：{}", dictCode);
        return dictItemMapper.selectByDictCode(dictCode);
    }
}
