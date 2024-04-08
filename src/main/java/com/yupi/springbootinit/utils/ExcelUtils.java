package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * excel相关工具类
 */
@Slf4j
public class ExcelUtils {
    /**
     * 数据压缩为csv,返回字符串
     */
    public static String excelToCsv(MultipartFile multipartFile){
        //读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误");
        }
        System.out.println(list);
        //如果数据为空
        if(CollUtil.isEmpty(list)){
            return "";
        }
        //转换为csv (数据压缩)
        StrBuilder strBuilder = new StrBuilder();
        //过滤表头数据
        //todo 问为什么要用LinkedHashMap呢?
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap<Integer, String>) list.get(0);
        List<String> headValueList = headMap.values().stream().filter(item -> ObjectUtil.isNotEmpty(item)).collect(Collectors.toList());
        String head = StringUtils.join(headValueList, ",");
        System.out.println(head);
        strBuilder.append(head).append("\n");
        //过滤表数据
        for (int i = 1; i < list.size(); i ++){
            LinkedHashMap<Integer, String> dataListMap = (LinkedHashMap)list.get(i);
            List<String> dataList = dataListMap.values().stream().filter(item -> ObjectUtil.isNotEmpty(item)).collect(Collectors.toList());
            String data = StringUtils.join(dataList, ",");
            System.out.println(data);
            strBuilder.append(data).append("\n");
        }
        return strBuilder.toString();
    }

    /**
     * test
     * @param args
     */
    public static void main(String[] args) {
        excelToCsv(null);
    }
}
