package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.DictItemDTO;
import com.fishing.pojo.dto.DictTypeDTO;
import com.fishing.pojo.entity.DictItemEntity;
import com.fishing.pojo.entity.DictTypeEntity;
import com.fishing.pojo.query.DictItemPageQuery;
import com.fishing.pojo.query.DictTypePageQuery;
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

    // ==================== 字典类型管理 ====================

    /**
     * 分页查询字典类型列表（管理后台）
     */
    @PostMapping("/type/manage/page")
    public Result<PageResult<DictTypeEntity>> pageDictTypes(@RequestBody DictTypePageQuery query) {
        log.info("分页查询字典类型列表（管理后台）：{}", query);
        PageResult<DictTypeEntity> pageResult = dictService.pageDictTypes(query);
        return Result.success(pageResult);
    }

    /**
     * 获取所有字典类型（用于下拉选择）
     */
    @GetMapping("/type/all")
    public Result<List<DictTypeEntity>> getAllDictTypes() {
        log.info("获取所有字典类型");
        List<DictTypeEntity> types = dictService.getAllDictTypes();
        return Result.success(types);
    }

    /**
     * 根据ID获取字典类型详情（管理后台）
     */
    @GetMapping("/type/manage/{id}")
    public Result<DictTypeEntity> getDictTypeById(@PathVariable Long id) {
        log.info("获取字典类型详情（管理后台）：{}", id);
        DictTypeEntity type = dictService.getDictTypeById(id);
        if (type == null) {
            return Result.error("字典类型不存在");
        }
        return Result.success(type);
    }

    /**
     * 创建字典类型（管理后台）
     */
    @PostMapping("/type/manage")
    public Result<Void> createDictType(@RequestBody DictTypeDTO dto) {
        log.info("创建字典类型（管理后台）：{}", dto);
        dictService.createDictType(dto);
        return Result.success();
    }

    /**
     * 更新字典类型（管理后台）
     */
    @PutMapping("/type/manage/{id}")
    public Result<Void> updateDictType(@PathVariable Long id, @RequestBody DictTypeDTO dto) {
        log.info("更新字典类型（管理后台）：{}，数据：{}", id, dto);
        dictService.updateDictType(id, dto);
        return Result.success();
    }

    /**
     * 删除字典类型（管理后台）
     */
    @DeleteMapping("/type/manage/{id}")
    public Result<Void> deleteDictType(@PathVariable Long id) {
        log.info("删除字典类型（管理后台）：{}", id);
        dictService.deleteDictType(id);
        return Result.success();
    }

    // ==================== 字典项管理 ====================

    /**
     * 分页查询字典项列表（管理后台）
     */
    @PostMapping("/item/manage/page")
    public Result<PageResult<DictItemEntity>> pageDictItems(@RequestBody DictItemPageQuery query) {
        log.info("分页查询字典项列表（管理后台）：{}", query);
        PageResult<DictItemEntity> pageResult = dictService.pageDictItems(query);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取字典项详情（管理后台）
     */
    @GetMapping("/item/manage/{id}")
    public Result<DictItemEntity> getDictItemById(@PathVariable Long id) {
        log.info("获取字典项详情（管理后台）：{}", id);
        DictItemEntity item = dictService.getDictItemById(id);
        if (item == null) {
            return Result.error("字典项不存在");
        }
        return Result.success(item);
    }

    /**
     * 创建字典项（管理后台）
     */
    @PostMapping("/item/manage")
    public Result<Void> createDictItem(@RequestBody DictItemDTO dto) {
        log.info("创建字典项（管理后台）：{}", dto);
        dictService.createDictItem(dto);
        return Result.success();
    }

    /**
     * 更新字典项（管理后台）
     */
    @PutMapping("/item/manage/{id}")
    public Result<Void> updateDictItem(@PathVariable Long id, @RequestBody DictItemDTO dto) {
        log.info("更新字典项（管理后台）：{}，数据：{}", id, dto);
        dictService.updateDictItem(id, dto);
        return Result.success();
    }

    /**
     * 删除字典项（管理后台）
     */
    @DeleteMapping("/item/manage/{id}")
    public Result<Void> deleteDictItem(@PathVariable Long id) {
        log.info("删除字典项（管理后台）：{}", id);
        dictService.deleteDictItem(id);
        return Result.success();
    }
}
