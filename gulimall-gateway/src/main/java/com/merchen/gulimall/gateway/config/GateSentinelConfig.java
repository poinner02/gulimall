package com.merchen.gulimall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.merchen.common.exception.BizCodeEnume;
import com.merchen.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author MrChen
 * @create 2022-10-27 21:25
 */
@Configuration
public class GateSentinelConfig {
    public GateSentinelConfig(){
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            //网关限流
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                R r = R.error(BizCodeEnume.TOO_MANY_RESQUEST.getCode(), BizCodeEnume.TOO_MANY_RESQUEST.getMessage()) ;
                String jsonString = JSON.toJSONString(r);
                Mono<ServerResponse> mono = ServerResponse.ok().body(Mono.just(jsonString), String.class);
                return mono;
            }
        });
    }
}
