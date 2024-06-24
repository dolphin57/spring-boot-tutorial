package io.dolphin.ptl.table;

import com.deepoove.poi.data.RowRenderData;
import lombok.Data;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年02月06日 11:07
 * @description
 */
@Data
public class ContractRenderData {
    // 合同数据
    private List<RowRenderData> contractDataList;
}
