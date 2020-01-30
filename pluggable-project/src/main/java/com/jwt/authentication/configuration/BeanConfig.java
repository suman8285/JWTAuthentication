package com.jwt.authentication.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;



@Configuration
public class BeanConfig {

    @Value("${request.readTimeout}")
    private int timeout;

    @Value("${request.connectionTimeout}")
    private int connectionTimeout;
    
    @Value("${auth.key}")
	private String publicKeypath;

//    @Value("${pns.sslTrustStore}")
//    private String trustStore;
    
    private  String TRUSTSTORE_LOCATION = "classpath:jwt/suman.jks";

    @Bean
    @Qualifier("restTemplate")
    public RestTemplate getRestTemplate() {

        RestTemplate restClient = new RestTemplate();

        // Trust own CA and all child certs
        Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
        socketFactoryRegistry = getConnectionSocketFactoryRegistry(socketFactoryRegistry);

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(500);
        connectionManager.setDefaultMaxPerRoute(25);

        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .disableCookieManagement()
                .disableAuthCaching()
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(timeout);
        //restClient.getInterceptors().add(new BasicAuthorizationInterceptor("1345634", "pass123"));
        restClient.setRequestFactory(factory);
        
       /* MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(
            Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));

        restClient.setMessageConverters(Arrays.asList(converter, new FormHttpMessageConverter()));
*/        return restClient;
    }

    private Registry<ConnectionSocketFactory> getConnectionSocketFactoryRegistry(Registry<ConnectionSocketFactory> socketFactoryRegistry) {
        try {
        	InputStream f1 = BeanConfig.class.getClassLoader().getResourceAsStream("jwt/suman.jks");
        	
        	File tempFile = File.createTempFile("suman", ".jks");
        	tempFile.deleteOnExit();
        	FileOutputStream out = new FileOutputStream(tempFile);
        	IOUtils.copy(f1, out);
        	
        	
            SSLContext sslContext = SSLContexts
                    .custom()
                    .loadTrustMaterial(tempFile)
                    .build();

            // Since only our own certs are trusted, hostname verification is probably safe to bypass
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();
            
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | IOException e) {
            e.printStackTrace();
        }
        return socketFactoryRegistry;
    }

}