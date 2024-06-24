package io.dolphin.ptl.table;

import com.deepoove.poi.expression.Name;
import lombok.Data;

/**
 * @author dolphin
 * @date 2024年02月06日 11:18
 * @description
 */
@Data
public class ContractAssembleData {
    @Name("linehaulPrices")
    private ContractRenderData renderData;
}
