package com.test.integration.spring.testspringcloudintegration;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CamelRoute extends RouteBuilder {

    @Value("${valore}")
    String splitName = "valore";

    MappingProcessor mappingProcessor = new MappingProcessor();

    private Processor setNameXml = exchange -> {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String fileName = "Test_XML_" + timeStamp + ".xml";
        System.out.println("XML");
        exchange.getIn().setHeader("CamelFileName",fileName);
    };

    private Processor setNameTxt = exchange -> {
      String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
      String fileName = "Test_TXT_" + timeStamp + ".txt";
        System.out.println("TXT");
      exchange.getIn().setHeader("CamelFileName",fileName);

    };

    @Override
    public void configure() throws Exception {
        from("{{test.integration.inputlocal}}")
                .choice()
                .when(header(Exchange.FILE_NAME_CONSUMED).endsWith(".txt"))
                    .to("direct:TxtRoute")
                .when(header(Exchange.FILE_NAME_CONSUMED).endsWith(".xml"))
                    .to("direct:XmlRoute")
                .otherwise()
                    .to("direct:OthersFilesRoute")
                .end();


        from("direct:XmlRoute")
                .log("Start validator")
                .to("validator:test_file.xsd")
                .bean(mappingProcessor)
                .log("Start Mapping")
                .process(setNameXml)
                .log("End Mapping")
                .log("Sending to xml folder")
                .to("{{test.integration.outputlocalXml}}");

        from("direct:TxtRoute")
                .process(setNameTxt)
                .log("Sending to Txt folder")
                .to("{{test.integration.outputlocalTxt}}");

        from("direct:OthersFilesRoute")
                .to("{{test.integration.outputOthersFiles}}");
    }

}
