package com.hbwl.utils.ExcelUtil.Converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExcelStringJavaStringListConverter implements Converter<List<String>> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public List<String> convertToJavaData(ReadConverterContext<?> context) {
        String str = context.getReadCellData().getStringValue();
        if (str != null && str.contains(";")){
            return Arrays.asList(str.split(";"));
        }
        else{
            return Collections.singletonList(str);
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<List<String>> context) throws Exception {
        List<String> list = context.getValue();
        return new WriteCellData<>(String.join(";", list));
    }
}
