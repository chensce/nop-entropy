package io.nop.http.api.client.rpc;

import io.nop.api.core.ApiConstants;
import io.nop.api.core.beans.ApiRequest;
import io.nop.api.core.util.ApiHeaders;
import io.nop.api.core.util.ApiStringHelper;

public class DefaultRpcUrlBuilder implements IRpcUrlBuilder {
    private final String baseUrl;

    public DefaultRpcUrlBuilder(String baseUrl) {
        this.baseUrl = normalize(baseUrl);
    }

    static String normalize(String baseUrl) {
        if (baseUrl == null) return "";
        if (baseUrl.endsWith("/")) return baseUrl.substring(0, baseUrl.length() - 1);
        return baseUrl;
    }

    @Override
    public String toHttpHeader(String name) {
        if (ApiConstants.HEADER_HTTP_METHOD.equals(name)) return null;
        if (ApiConstants.HEADER_HTTP_URL.equals(name)) return null;
        return name;
    }

    @Override
    public String buildUrl(ApiRequest<?> req, String serviceMethod) {
        String url = ApiHeaders.getHttpUrl(req);
        if (url == null) {
            if (serviceMethod.startsWith("/")) {
                url = baseUrl + serviceMethod;
            } else {
                url = baseUrl + "/r/" + serviceMethod;
            }
        } else {
            url = baseUrl + url;
        }
        url = appendSelection(url, req);
        return url;
    }

    private String appendSelection(String url, ApiRequest<?> req) {
        if (req.getSelection() != null) {
            String selection = req.getSelection().toString();
            if (!ApiStringHelper.isEmpty(selection)) {
                url = ApiStringHelper.appendQuery(url, ApiConstants.SYS_PARAM_SELECTION + "=" + ApiStringHelper.encodeURL(selection));
            }
        }
        return url;
    }
}