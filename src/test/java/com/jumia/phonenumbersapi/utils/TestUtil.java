package com.jumia.phonenumbersapi.utils;

import lombok.experimental.UtilityClass;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

@UtilityClass
public class TestUtil {
    public static Boolean isEven(Integer Number) {
        return Number%2 == 0;
    }

    public static MockHttpServletRequestBuilder prepareQueryParams(MockHttpServletRequestBuilder requestBuilder, List<String> keyValuePairList) {
        Assert.isTrue(isEven(keyValuePairList.size()), "[Assertion failed] - keyValuePairs length must be even");
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        String key,value;
        while(!keyValuePairList.isEmpty()) {
            if(isEven(keyValuePairList.size())) {
                value = keyValuePairList.remove(keyValuePairList.size() - 1);
                key = keyValuePairList.remove(keyValuePairList.size() - 1);
                queryParams.add(key, value);
            }
        }
        requestBuilder.queryParams(queryParams);
        return requestBuilder;
    }
}
