package com.merchen.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.merchen.common.constant.ProductConstant;
import com.merchen.common.to.SkuHasStockTO;
import com.merchen.common.to.SpuBoundsTO;
import com.merchen.common.to.SkuReductionTO;
import com.merchen.common.to.WareSkuTO;
import com.merchen.common.to.elasticSearch.EsModel;
import com.merchen.common.utils.R;
import com.merchen.gulimall.product.entity.*;
import com.merchen.gulimall.product.feign.CouponFeignService;
import com.merchen.gulimall.product.feign.ElasticSearchFeignService;
import com.merchen.gulimall.product.feign.WareFeignService;
import com.merchen.gulimall.product.service.*;

import com.merchen.gulimall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    WareFeignService wareFeignService;

    /**
     * 优惠远程服务
     */
    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ElasticSearchFeignService elasticSearchFeignService;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    //分布式事务 todo
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVO saveVO) {

        // 保存pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(saveVO, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        // 保存pms_spu_info_desc
        List<String> decript = saveVO.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //保存pms_spu_images
        List<String> images = saveVO.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //保存pms_product_attr_value
        List<BaseAttrs> baseAttrs = saveVO.getBaseAttrs();
        productAttrValueService.saveBaseAttr(baseAttrs, spuInfoEntity.getId());

        //保存spu的积分信息
        Bounds bounds = saveVO.getBounds();
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        BeanUtils.copyProperties(bounds, spuBoundsTO);
        spuBoundsTO.setSpuId(spuInfoEntity.getId());
        couponFeignService.saveSpuBounds(spuBoundsTO);
        //保存pms_sku_info
        List<Skus> skus = saveVO.getSkus();
        if (skus != null || skus.size() > 0) {
            skus.forEach(item -> {
                //设置默认图片
                String defaultImage = "";
                List<Images> imageList = item.getImages();
                for (Images image : imageList) {
                    if (image.getDefaultImg() == 1) {
                        defaultImage = image.getImgUrl();
                    }
                }
//            private String skuName;
//            private BigDecimal price;
//            private String skuTitle;
//            private String skuSubtitle;
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                //初始销售量
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImage);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                //保存pms_sku_images
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream().filter((entity) -> {
                    // 没有默认图片的路劲，则不需要保存
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);
                //保存  pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //sku 优惠和减满
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(item, skuReductionTO);
                skuReductionTO.setSkuId(skuInfoEntity.getSkuId());
                if (skuReductionTO.getFullCount() > 0 || skuReductionTO.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    couponFeignService.saveReductionInfo(skuReductionTO);
                }

            });
        }


    }

    @Override
    public PageUtils queryPageOnCondiction(Map<String, Object> params) {
        String key = (String) params.get("key");
        Query<SpuInfoEntity> spuInfoEntityQuery = new Query<>();
        IPage<SpuInfoEntity> page = spuInfoEntityQuery.getPage(params);
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((object -> {
                object.eq("id", key)
                        .or().like("spu_name", key);
            }));
        }
        if (!StringUtils.isEmpty(params.get("status"))) {
            String status = (String) params.get("status");
            wrapper.eq("publish_status", Integer.parseInt(status));
        }
        if (!StringUtils.isEmpty(params.get("brandId"))) {
            Long brandId = Long.parseLong((String) params.get("brandId"));
            if (brandId != 0L) {
                wrapper.eq("brand_id", brandId);
            }

        }
        if (!StringUtils.isEmpty(params.get("catelogId"))) {
            Long catalog_id = Long.parseLong((String) params.get("catelogId"));
            if (catalog_id != 0L) {
                wrapper.eq("catalog_id", catalog_id);
            }
        }
        IPage<SpuInfoEntity> entityIPage = this.baseMapper.selectPage(page, wrapper);
        return new PageUtils(entityIPage);
    }

    @Transactional
    @Override
    public void upProduct(Long spuId) {

        //获取EsModel.Attr集合
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.getList(spuId);
        //未过滤的attrId
        List<Long> nofilerIds = productAttrValueEntities.stream().map(item -> {
            Long attrId = item.getAttrId();
            return attrId;
        }).collect(Collectors.toList());
        //过滤好的attId
        List<Long> hasFilerIds = attrService.getFilerIds(nofilerIds);
        //处理集合包含关系转成set
        Set idSet = new HashSet(hasFilerIds);
        List<EsModel.Attr> attrList = productAttrValueEntities.stream().filter(item -> {
            Long attrId = item.getAttrId();
            return idSet.contains(attrId);
        }).map(o -> {
            EsModel.Attr attr = new EsModel.Attr();
            BeanUtils.copyProperties(o, attr);
            return attr;
        }).collect(Collectors.toList());

        //获取sku list
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        //查询是否有库存
        List<Long> ids = skuInfoEntities.stream().map(sku -> {
            Long skuId = sku.getSkuId();
            return skuId;
        }).collect(Collectors.toList());

        Map<Long, Boolean> stockMap = null;
        try {
            //todo 有bug问题无法读取到泛型数据处理远程服务波动
            R r = wareFeignService.getSkuHasStock(ids);
            //泛型 完成
            TypeReference<List<SkuHasStockTO>> typeReference = new TypeReference<List<SkuHasStockTO>>() {
            };
            stockMap =r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockTO::getSkuId, item -> item.getStock() > 0));
        } catch (Exception e) {
            log.error("库存 服务异常：原因{}", e);
        }

        //属性对拷
        Map<Long, Boolean> finalStockMap = stockMap;
        List<EsModel> collect = skuInfoEntities.stream().map(item -> {
            EsModel esModel = new EsModel();
            BeanUtils.copyProperties(item, esModel);
            //get skuPrice
            esModel.setSkuPrice(item.getPrice());
            //get skuImg
            esModel.setSkuImg(item.getSkuDefaultImg());
            //get brandImg,brandName
            Long brandId = item.getBrandId();
            BrandEntity brandEntity = brandService.getById(brandId);
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());
            //get catalogName
            Long catalogId = item.getCatalogId();
            CategoryEntity categoryEntity = categoryService.getById(catalogId);
            esModel.setCatalogName(categoryEntity.getName());
            //get attrs
            esModel.setAttrs(attrList);
            //get hasStock
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(item.getSkuId()));
            }
            //todo 热度默认值设置成0,或者根据客户的要求vip等提供不同的热度值
            esModel.setHotScore(0L);
            return esModel;
        }).collect(Collectors.toList());

        //储存到elasticSearch中
        //可能有网络波动阻塞
        try {
            R r = elasticSearchFeignService.saveEs(collect);
            Integer code = (Integer) r.get("code");
            if(code == 0){
                SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
                spuInfoEntity.setId(spuId);
                spuInfoEntity.setPublishStatus(ProductConstant.Status.SPU_UP.getCode());
                this.baseMapper.updateById(spuInfoEntity);
            }else{
                //日志
                log.error("商品上架出现错误:{}",r.get("msg"));
            }
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("com.merchen.gulimall.product.service.impl.SpuInfoServiceImpl.upProduct的ElasticSearch远程接口出错：{}",e);
        }


    }


    private void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}