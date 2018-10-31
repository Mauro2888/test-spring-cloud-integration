package com.test.integration.spring.testspringcloudintegration;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CamelRoute extends RouteBuilder {

    @Value("${valore}")
    private String splitName;

    @Autowired
    MappingProcessor mappingProcessor;

    private Processor setNameXml = exchange -> {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String fileName = "Test_XML_" + timeStamp + ".xml";
        System.out.println("Route XML");
        exchange.getIn().setHeader("CamelFileName",fileName);
    };

    private Processor setNameTxt = exchange -> {
      String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
      String fileName = "Test_TXT_" + timeStamp + ".txt";
        System.out.println("Route TXT");
      exchange.getIn().setHeader("CamelFileName",fileName);

    };

    private Processor setSplitName = exchange -> {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String fileName = "Split_Test_File_"+ timeStamp + ".xml";
        System.out.println("Route SPLIT");
        exchange.getIn().setHeader("CamelFileName",fileName);
    };

    @Override
    public void configure() throws Exception {
        from("{{test.integration.inputlocal}}")
                .choice()
                .when(header(Exchange.FILE_NAME_CONSUMED).endsWith(".txt"))
                    .to("direct:TxtRoute")
                .when(header(Exchange.FILE_NAME_CONSUMED).contains("MAPPED"))
                    .to("direct:XmlRoute")
                .when(header(Exchange.FILE_NAME_CONSUMED).contains("AP"))
                    .to("direct:XmlSplitRoute")
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

        from("direct:XmlSplitRoute")
                .split().tokenizeXML(splitName)
                .log("Splitting")
                .process(setSplitName)
                .to("{{test.integration.outputlocalSplit}}");


        from("direct:OthersFilesRoute")
                .to("{{test.integration.outputOthersFiles}}");
    }

}
