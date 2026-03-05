package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.entity.DictItemEntity;
import com.fishing.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dict")
@CrossOrigin(origins = "*")
@Slf4j
public class DictController {
    private final DictService dictService;

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    /**
     * 根据字典编码获取字典项
     */
    @GetMapping("/items/{dictCode}")
    public Result<List<DictItemEntity>> getDictItems(@PathVariable String dictCode) {
        log.info("获取字典项，字典编码：{}", dictCode);
        List<DictItemEntity> items = dictService.getDictItemsByCode(dictCode);
        return Result.success(items);
    }
}
