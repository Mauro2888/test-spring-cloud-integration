package com.test.integration.spring.testspringcloudintegration;

import com.altova.io.StringInput;
import com.altova.io.StringOutput;
import com.test.integration.spring.testspringcloudintegration.Mapping.MappingMapToOutput;
import org.apache.camel.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MappingProcessor {

    public String mapping(@Body String exchange){
        MappingMapToOutput map = new MappingMapToOutput();
        StringInput stringInput = new StringInput(exchange);
        StringOutput stringOutput = new StringOutput();
        try {
            map.run(stringInput,stringOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringOutput.getString().toString();
    }
}
