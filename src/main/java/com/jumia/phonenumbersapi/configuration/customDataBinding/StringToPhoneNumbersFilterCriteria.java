package com.jumia.phonenumbersapi.configuration.customDataBinding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jumia.phonenumbersapi.model.PhoneNumbersFilterCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static java.text.MessageFormat.format;

@Slf4j
@Component
public class StringToPhoneNumbersFilterCriteria
implements Converter<String, List<PhoneNumbersFilterCriteria>> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<PhoneNumbersFilterCriteria> convert(@NonNull String source) {
        try {
            ObjectReader reader = mapper.readerFor(new TypeReference<List<PhoneNumbersFilterCriteria>>(){});
            return reader.readValue(mapper.readTree(source));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(
                    format(
                            "#validator Failed to Convert Json object {0} to 'PhoneNumbersFilterCriteria'#validator",
                            source));
        }
    }
}
