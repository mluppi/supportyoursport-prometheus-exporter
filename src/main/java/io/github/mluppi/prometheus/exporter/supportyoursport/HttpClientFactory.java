/*
Copyright 2020-2021 M. Luppi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package io.github.mluppi.prometheus.exporter.supportyoursport;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;

public class HttpClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

    /**
     * Returns a {@link CloseableHttpClient} which does not verify the TLS/SSL certificate.
     * <p>
     * The queried API is unfortunately not properly configured and returns an incomplete certificate chain.
     * Depending on the environment, this would lead to failed requests. Therefore, it was decided to drop
     * verification of the certificate. The security implications were deemed acceptable as only get-requests
     * will be made, the application is non-critical and no SLA needs to be upheld (only POC deployment).
     * <p>
     */
    public static CloseableHttpClient getCustomHttpClient(final int socketTimeoutInSeconds) {
        CloseableHttpClient httpClient = null;
        try {
            // TODO: verify certificate
            httpClient = HttpClients.custom()
                    .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15")
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setSocketTimeout(socketTimeoutInSeconds * 1000)
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSSLContext(new SSLContextBuilder()
                            .loadTrustMaterial(null, new TrustStrategy() {
                                public boolean isTrusted(X509Certificate[] chain, String authType) {
                                    return true;
                                }
                            }).build()
                    ).build();
        } catch (Exception e) {
            logger.error("Could not create HTTP client", e);
        }
        return httpClient;
    }

    private HttpClientFactory() {
    }
}
