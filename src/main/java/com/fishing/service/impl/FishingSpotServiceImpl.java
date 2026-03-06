package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.FishingSpotMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.FishingSpotCreateDTO;
import com.fishing.pojo.dto.FishingSpotUpdateDTO;
import com.fishing.pojo.entity.FishingSpotEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.query.FishingSpotPageQuery;
import com.fishing.pojo.vo.FishingSpotManageVO;
import com.fishing.pojo.vo.FishingSpotVO;
import com.fishing.service.DictService;
import com.fishing.service.FishingSpotService;
import com.fishing.utils.MinioUtils;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FishingSpotServiceImpl extends ServiceImpl<FishingSpotMapper, FishingSpotEntity> implements FishingSpotService {

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictService dictService;

    @Value("${file.spot-image-dir:spot_image}")
    private String spotImageDir;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<FishingSpotVO> getSpotList() {
        List<FishingSpotEntity> entities = this.list();
        List<FishingSpotVO> vos = new ArrayList<>();

        for (FishingSpotEntity entity : entities) {
            FishingSpotVO vo = convertToVO(entity);
            vos.add(vo);
        }

        return vos;
    }

    @Override
    public List<FishingSpotVO> getRecommendSpots(int limit, String typeDictItemCode) {
        LambdaQueryWrapper<FishingSpotEntity> wrapper = new LambdaQueryWrapper<>();
        // 如果指定了类型，添加类型筛选
        if (typeDictItemCode != null && !typeDictItemCode.isEmpty()) {
            wrapper.eq(FishingSpotEntity::getTypeDictItemCode, typeDictItemCode);
        }
        
        List<FishingSpotEntity> entities = this.list(wrapper);
        // 随机打乱列表
        Collections.shuffle(entities);
        // 取前limit个
        List<FishingSpotEntity> randomEntities = entities.stream()
                .limit(limit)
                .collect(Collectors.toList());

        List<FishingSpotVO> vos = new ArrayList<>();
        for (FishingSpotEntity entity : randomEntities) {
            FishingSpotVO vo = convertToVO(entity);
            vos.add(vo);
        }

        return vos;
    }

    @Override
    public List<FishingSpotVO> searchSpots(String keyword, String typeDictItemCode) {
        LambdaQueryWrapper<FishingSpotEntity> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词模糊匹配（名称、地址、鱼种）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(FishingSpotEntity::getName, keyword)
                    .or()
                    .like(FishingSpotEntity::getAddress, keyword)
                    .or()
                    .like(FishingSpotEntity::getFishInfoDictItemCodes, keyword)
                    .or()
                    .like(FishingSpotEntity::getProvince, keyword)
                    .or()
                    .like(FishingSpotEntity::getCity, keyword)
            );
        }
        
        // 类型筛选
        if (typeDictItemCode != null && !typeDictItemCode.isEmpty()) {
            wrapper.eq(FishingSpotEntity::getTypeDictItemCode, typeDictItemCode);
        }

        List<FishingSpotEntity> entities = this.list(wrapper);
        List<FishingSpotVO> vos = new ArrayList<>();
        for (FishingSpotEntity entity : entities) {
            FishingSpotVO vo = convertToVO(entity);
            vos.add(vo);
        }

        return vos;
    }

    @Override
    public FishingSpotVO getSpotById(Long id) {
        FishingSpotEntity entity = this.getById(id);
        if (entity == null) {
            return null;
        }

        return convertToVO(entity);
    }

    private FishingSpotVO convertToVO(FishingSpotEntity entity) {
        FishingSpotVO vo = new FishingSpotVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setPrice(entity.getPriceDesc());
        vo.setAiRecommendation(entity.getBestPositionDesc());

        // 使用数据字典项编码设置类型
        String typeDictItemCode = entity.getTypeDictItemCode();
        vo.setType(typeDictItemCode != null ? typeDictItemCode : "wild");

        // 模拟评分和距离
        vo.setRating(4.0 + new Random().nextDouble() * 1.0);
        vo.setDistance(1.0 + new Random().nextDouble() * 20.0);

        // 解析图片列表
        List<String> imageList = parseImages(entity.getImages());
        vo.setImages(imageList);

        // 设置第一张图片作为封面
        if (!imageList.isEmpty()) {
            vo.setImage(imageList.get(0));
        } else {
            vo.setImage("https://images.unsplash.com/photo-1507525428034-b723cf96123e?w=800&auto=format&fit=crop");
        }

        // 解析鱼种列表
        List<String> fishInfoCodes = parseFishInfoCodes(entity.getFishInfoDictItemCodes());
        vo.setFishInfoDictItemCodes(fishInfoCodes);

        return vo;
    }

    /**
     * 解析图片JSON字符串为列表
     */
    private List<String> parseImages(String imagesJson) {
        if (imagesJson == null || imagesJson.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // 尝试解析为JSON数组
            List<String> images = objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});
            // 将相对路径转换为完整URL
            List<String> fullUrls = new ArrayList<>();
            for (String img : images) {
                if (img != null && !img.isEmpty()) {
                    if (img.startsWith("http")) {
                        fullUrls.add(img);
                    } else {
                        fullUrls.add(minioUtils.getFullUrl(img, spotImageDir));
                    }
                }
            }
            return fullUrls;
        } catch (Exception e) {
            // 如果不是JSON格式，尝试按逗号分割
            try {
                List<String> images = Arrays.asList(imagesJson.split(","));
                List<String> fullUrls = new ArrayList<>();
                for (String img : images) {
                    img = img.trim();
                    if (!img.isEmpty()) {
                        if (img.startsWith("http")) {
                            fullUrls.add(img);
                        } else {
                            fullUrls.add(minioUtils.getFullUrl(img, spotImageDir));
                        }
                    }
                }
                return fullUrls;
            } catch (Exception ex) {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public String getAiRecommendation(Long spotId) {
        FishingSpotEntity entity = this.getById(spotId);
        if (entity == null) {
            return "钓点不存在";
        }

        return entity.getBestPositionDesc();
    }

    @Override
    public PageResult<FishingSpotManageVO> page(FishingSpotPageQuery query) {
        Page<FishingSpotEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FishingSpotEntity> wrapper = new LambdaQueryWrapper<>();
        
        // 名称模糊查询
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(FishingSpotEntity::getName, query.getName());
        }
        // 省份查询
        if (StringUtils.hasText(query.getProvince())) {
            wrapper.like(FishingSpotEntity::getProvince, query.getProvince());
        }
        // 城市查询
        if (StringUtils.hasText(query.getCity())) {
            wrapper.like(FishingSpotEntity::getCity, query.getCity());
        }
        // 类型筛选
        if (StringUtils.hasText(query.getTypeDictItemCode())) {
            wrapper.eq(FishingSpotEntity::getTypeDictItemCode, query.getTypeDictItemCode());
        }
        // 状态筛选
        if (StringUtils.hasText(query.getStatusDictItemCode())) {
            wrapper.eq(FishingSpotEntity::getStatusDictItemCode, query.getStatusDictItemCode());
        }
        // 未删除
        wrapper.eq(FishingSpotEntity::getIsDeleted, 0);
        wrapper.orderByDesc(FishingSpotEntity::getCreateTime);

        IPage<FishingSpotEntity> entityPage = this.page(page, wrapper);
        List<FishingSpotManageVO> voList = entityPage.getRecords().stream()
                .map(this::convertToManageVO)
                .collect(Collectors.toList());

        PageResult<FishingSpotManageVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(entityPage.getTotal());
        result.setPageNum(entityPage.getCurrent());
        result.setPageSize(entityPage.getSize());
        return result;
    }

    @Override
    public FishingSpotManageVO getSpotManageById(Long id) {
        FishingSpotEntity entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("钓点不存在");
        }
        return convertToManageVO(entity);
    }

    @Override
    public void createSpot(FishingSpotCreateDTO dto) {
        FishingSpotEntity entity = new FishingSpotEntity();
        BeanUtils.copyProperties(dto, entity);
        
        // 处理图片列表
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                entity.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } catch (Exception e) {
                entity.setImages("");
            }
        }
        
        // 处理鱼种列表
        if (dto.getFishInfoDictItemCodes() != null && !dto.getFishInfoDictItemCodes().isEmpty()) {
            try {
                entity.setFishInfoDictItemCodes(objectMapper.writeValueAsString(dto.getFishInfoDictItemCodes()));
            } catch (Exception e) {
                entity.setFishInfoDictItemCodes("");
            }
        }
        
        // 设置默认值
        entity.setIsDeleted(0);
        entity.setCreatorId(1L); // 默认管理员创建
        
        this.save(entity);
    }

    @Override
    public void updateSpot(Long id, FishingSpotUpdateDTO dto) {
        FishingSpotEntity entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("钓点不存在");
        }
        
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        
        // 处理图片列表
        if (dto.getImages() != null) {
            try {
                entity.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } catch (Exception e) {
                entity.setImages("");
            }
        }
        
        // 处理鱼种列表
        if (dto.getFishInfoDictItemCodes() != null) {
            try {
                entity.setFishInfoDictItemCodes(objectMapper.writeValueAsString(dto.getFishInfoDictItemCodes()));
            } catch (Exception e) {
                entity.setFishInfoDictItemCodes("");
            }
        }
        
        this.updateById(entity);
    }

    @Override
    public void deleteSpot(Long id) {
        FishingSpotEntity entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("钓点不存在");
        }
        
        entity.setIsDeleted(1);
        this.updateById(entity);
    }

    private FishingSpotManageVO convertToManageVO(FishingSpotEntity entity) {
        FishingSpotManageVO vo = new FishingSpotManageVO();
        BeanUtils.copyProperties(entity, vo);

        // 设置类型名称
        if (StringUtils.hasText(entity.getTypeDictItemCode())) {
            vo.setTypeDictItemName(dictService.getItemName(entity.getTypeDictTypeCode(), entity.getTypeDictItemCode()));
        }

        // 设置状态名称
        if (StringUtils.hasText(entity.getStatusDictItemCode())) {
            vo.setStatusDictItemName(dictService.getItemName(entity.getStatusDictTypeCode(), entity.getStatusDictItemCode()));
        }

        // 解析图片列表
        vo.setImages(parseImages(entity.getImages()));

        // 解析鱼种列表
        List<String> fishInfoCodes = parseFishInfoCodes(entity.getFishInfoDictItemCodes());
        vo.setFishInfoDictItemCodes(fishInfoCodes);

        // 设置鱼种名称列表
        if (fishInfoCodes != null && !fishInfoCodes.isEmpty()) {
            log.info("解析鱼种列表: {}, 鱼种类型编码: {}", fishInfoCodes.toString(), entity.getFishInfoDictTypeCode());
            List<String> fishInfoNames = fishInfoCodes.stream()
                    .map(code -> {
                        String name = dictService.getItemName(entity.getFishInfoDictTypeCode(), code);
                        log.info("鱼种代码: {}, 名称: {}", code, name);
                        return name;
                    })
                    .collect(Collectors.toList());
            vo.setFishInfoDictItemNames(fishInfoNames);
            log.info("最终鱼种名称列表: {}", fishInfoNames.toString());
        } else {
            log.warn("鱼种列表为空: {}", fishInfoCodes != null ? fishInfoCodes.toString() : "null");
        }

        // 获取创建者名称
        if (entity.getCreatorId() != null) {
            UserEntity user = userMapper.selectById(entity.getCreatorId());
            if (user != null) {
                vo.setCreatorName(user.getNickname());
            }
        }

        return vo;
    }

    /**
     * 解析鱼种编码JSON字符串为列表
     */
    private List<String> parseFishInfoCodes(String fishInfoJson) {
        if (fishInfoJson == null || fishInfoJson.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(fishInfoJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // 如果不是JSON格式，尝试按逗号分割
            return Arrays.stream(fishInfoJson.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }
}