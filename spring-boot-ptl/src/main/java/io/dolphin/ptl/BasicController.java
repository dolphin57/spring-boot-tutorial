package io.dolphin.ptl;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dolphin.ptl.table.ContractAssembleData;
import io.dolphin.ptl.table.ContractData;
import io.dolphin.ptl.table.ContractRenderData;
import io.dolphin.ptl.table.DolphinTablePolicy;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dolphin
 * @date 2024年02月05日 17:08
 * @description
 */
@Controller
public class BasicController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    ContractAssembleData datas = new ContractAssembleData();
    private static final String JSON_FILE_PATH = "src/main/resources/data.txt";
    private static final String TEMPLATE_PATH = "src/main/resources/template.docx";
    private static final String OUTPUT_PATH = "contract.docx";

    @GetMapping("/ptl")
    public void ptl() throws IOException {
        // 1. 读取文本文件中的JSON内容
        File jsonFile = new File(JSON_FILE_PATH);
        String jsonData = new String(Files.readAllBytes(jsonFile.toPath()));
        // 2. 将json字符串转换为Java List<ContractData>
        List<ContractData> contractDataList = objectMapper.readValue(jsonData, new TypeReference<List<ContractData>>() {});

        List<RowRenderData> rowDatas = new ArrayList<>();
        ContractRenderData renderData = new ContractRenderData();
        for (ContractData contractData : contractDataList) {
            RowRenderData rowRenderData = Rows.create(contractData.getMainLinePrice1(), contractData.getMainLinePrice2(),
                    contractData.getMainLinePrice3(), contractData.getMainLinePrice4(), contractData.getMainLinePrice5(),
                    contractData.getNum(), contractData.getCustomerInfo(), contractData.getLeadTime(),
                    contractData.getMincharge(), contractData.getSourceAddr(), contractData.getDeptAddr());
            rowDatas.add(rowRenderData);
        }
        renderData.setContractDataList(rowDatas);
        datas.setRenderData(renderData);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("contractTask");

        Configure config = Configure.builder().bind("linehaulPrices", new DolphinTablePolicy()).build();
        XWPFTemplate template = XWPFTemplate.compile(TEMPLATE_PATH, config).render(datas);
        template.writeToFile(OUTPUT_PATH);

        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }
}
