package com.test.integration.spring.testspringcloudintegration;

import com.altova.io.StringInput;
import com.altova.io.StringOutput;
import com.test.integration.spring.testspringcloudintegration.Mapping.MappingMapToOutput;
import org.apache.camel.Body;


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
