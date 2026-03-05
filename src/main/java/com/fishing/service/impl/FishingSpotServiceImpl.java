package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.mapper.FishingSpotMapper;
import com.fishing.pojo.entity.FishingSpotEntity;
import com.fishing.pojo.vo.FishingSpotVO;
import com.fishing.service.FishingSpotService;
import com.fishing.utils.MinioUtils;
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
public class FishingSpotServiceImpl extends ServiceImpl<FishingSpotMapper, FishingSpotEntity> implements FishingSpotService {

    @Autowired
    private MinioUtils minioUtils;

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
}