package com.cyanrocks.ui.utils.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:/httpClient.properties")
public class HttpClientConfig {
    @Value("${http.maxTotal}")
    private Integer maxTotal; // 最大连接数

    @Value("${http.defaultMaxPerRoute}")
    private Integer defaultMaxPerRoute; // 最大并发链接数

    @Value("${http.connectTimeout}")
    private Integer connectTimeout; // 创建链接的最大时间

    @Value("${http.connectionRequestTimeout}")
    private Integer connectionRequestTimeout; // 链接获取超时时间

    @Value("${http.socketTimeout}")
    private Integer socketTimeout; // 数据传输最长时间

    @Value("${http.staleConnectionCheckEnabled}")
    private boolean staleConnectionCheckEnabled; // 提交时检查链接是否可用

    // 定义httpClient链接池
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(maxTotal); // 设定最大链接数
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute); // 设定并发链接数
        // if (StringUtils.isNotBlank(maxPerRouteStr)) {
        // String[] split = maxPerRouteStr.split("#");
        // for (String single : split) {
        // String[] singleSplit = single.split(";");
        // manager.setMaxPerRoute(new HttpRoute(), Integer.valueOf(singleSplit[2]));
        // }
        // }
        return manager;
    }

    // 定义HttpClient

    /**
     * 实例化连接池，设置连接池管理器。 这里需要以参数形式注入上面实例化的连接池管理器
     *
     * @Qualifier 指定bean标签进行注入
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(
        @Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager,
        @Qualifier("requestConfig") RequestConfig requestConfig) {

        // HttpClientBuilder中的构造方法被protected修饰，所以这里不能直接使用new来实例化一个HttpClientBuilder,可以使用HttpClientBuilder提供的静态方法create()来获取HttpClientBuilder对象
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler());
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        return httpClientBuilder;
    }

    /**
     * 注入连接池，用于获取httpClient
     *
     * @param httpClientBuilder
     * @return
     */
    @Bean(name = "defaultHttpClient")
    public CloseableHttpClient
        getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    /**
     * Builder是RequestConfig的一个内部类 通过RequestConfig的custom方法来获取到一个Builder对象 设置builder的连接信息
     *
     * @return
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout)
            .setSocketTimeout(socketTimeout).setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);
    }

    /**
     * 使用builder构建一个RequestConfig对象
     *
     * @param builder
     * @return
     */
    @Bean(name = "requestConfig")
    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
        return builder.build();
    }
}