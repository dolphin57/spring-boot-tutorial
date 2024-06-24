package io.dolphin.ptl.table;

import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.TableRenderPolicy;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;

/**
 * @author dolphin
 * @date 2024年02月06日 11:21
 * @description
 */
public class DolphinTablePolicy extends DynamicTableRenderPolicy {
    // 合同填充数据所在行数
    int contractStartRow = 1;

    @Override
    public void render(XWPFTable table, Object data) throws Exception {
        if (null == data) return;
        ContractRenderData detailData = (ContractRenderData) data;
        List<RowRenderData> contractDataList = detailData.getContractDataList();
        if (null != contractDataList) {
            table.removeRow(contractStartRow);
            for (int i = 0; i < contractDataList.size(); i++) {
                XWPFTableRow insertNewTableRow = table.insertNewTableRow(contractStartRow);
                for (int j = 0; j < 11; j++) {
                    insertNewTableRow.createCell();
                }
                TableRenderPolicy.Helper.renderRow(table.getRow(contractStartRow), contractDataList.get(i));
            }
        }
    }
}
