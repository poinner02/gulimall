package com.merchen.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.merchen.common.to.SkuHasStockTO;
import com.merchen.common.utils.R;
import com.merchen.gulimall.product.entity.SkuImagesEntity;
import com.merchen.gulimall.product.entity.SpuInfoDescEntity;
import com.merchen.gulimall.product.feign.SecKillFeignService;
import com.merchen.gulimall.product.feign.WareFeignService;
import com.merchen.gulimall.product.service.*;
import com.merchen.gulimall.product.vo.SecKillSkuInfo;
import com.merchen.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.SkuInfoDao;
import com.merchen.gulimall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private SecKillFeignService secKillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        baseMapper.insert(skuInfoEntity);
    }

    //todo ?????????????????????
    //http://localhost:88/api/product/skuinfo/list?t=1656339397441&page=1&limit=10&key=1&catelogId=225&brandId=7&min=4&max=13
    @Override
    public PageUtils queryPageOnCondiction(Map<String, Object> params) {
        Query<SkuInfoEntity> skuInfoEntityQuery = new Query<>();
        IPage<SkuInfoEntity> page = skuInfoEntityQuery.getPage(params);
        String key = (String) params.get("key");
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(object -> {
                object.eq("sku_id", key)
                        .or().like("sku_name", key);
            });
        }
        if (!StringUtils.isEmpty(params.get("catelogId"))) {
            String catelogId = (String) params.get("catelogId");
            if (!catelogId.equals("0")) {
                wrapper.eq("catalog_id", catelogId);
            }
        }
        if (!StringUtils.isEmpty(params.get("brandId"))) {
            String brand_id = (String) params.get("brandId");
            if (!brand_id.equals("0")) {
                wrapper.eq("brand_id", brand_id);
            }
        }
        //??????bug ??????????????????????????????0~0????????????????????????????????????????????????
        if (!StringUtils.isEmpty(params.get("min")) && !StringUtils.isEmpty(params.get("max"))) {
            String min = (String) params.get("min");
            String max = (String) params.get("max");
            if("0".equals(min) && "0".equals(max)){
                //do nothing
            }else{
                //?????????????????????????????????
                wrapper.between("price", min, max);
            }
        }
        IPage<SkuInfoEntity> skuInfoEntityIPage = this.baseMapper.selectPage(page, wrapper);
        return new PageUtils(skuInfoEntityIPage);
    }

    /**
     * ????????????????????????
     * @param skuId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    //????????????????????????   ??????1???2???6 ???????????? ??????3???4???5??????1???????????????
    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1???sku ????????????  pms_sku_info
            SkuInfoEntity skuInfoEntity = baseMapper.selectById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync(res -> {
            //3???spu ??????????????????
            List<SkuItemVo.ItemSaleItemAttrsVo> saleAttrsVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttrs(saleAttrsVos);
        }, executor);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync(res -> {
            //4???spu ???????????? pms_spu_info_desc
            SpuInfoDescEntity descEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(descEntity);
        }, executor);

        CompletableFuture<Void> groupFuture = infoFuture.thenAcceptAsync((res) -> {
            //5???spu ??????????????????
            List<SkuItemVo.SpuItemAttrGroupVo> spuItemAttrGroupVo = attrGroupService.getAttrGroupWithAttrsBySpuIdAndCateGoryId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGoupAttrs(spuItemAttrGroupVo);
        }, executor);

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2???sku ????????????  pms_sku_images
            List<SkuImagesEntity> images = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            skuItemVo.setImages(images);
        }, executor);

        //6?????????????????????
        CompletableFuture<Void> hasStockFuture = CompletableFuture.runAsync(() -> {
            R r = wareFeignService.getSkuHasStock(Arrays.asList(skuId));
            List<SkuHasStockTO> list = r.getData("data", new TypeReference<List<SkuHasStockTO>>() {
            });
            if (list.size() > 0 && list != null) {
                SkuHasStockTO skuHasStockTO = list.get(0);
                skuItemVo.setHas_stock(skuHasStockTO.getStock() > 0);
            }
        }, executor);

        //??????????????????????????????
        CompletableFuture<Void> secKillFuture = CompletableFuture.runAsync(() -> {
            R secKillSkuInfo = secKillFeignService.getSecKillSkuInfo(skuId);
            if (secKillSkuInfo.getCode() == 0) {
                SecKillSkuInfo secKillSkuInfoData = secKillSkuInfo.getData("data", new TypeReference<SecKillSkuInfo>() {
                });
                skuItemVo.setSecKillSkuInfo(secKillSkuInfoData);
            }
        }, executor);

        //?????????????????????
        CompletableFuture.allOf(imageFuture,saleAttrFuture,descFuture,groupFuture,hasStockFuture,secKillFuture).get();
        return skuItemVo;
    }

}