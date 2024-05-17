package com.net.common.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class SortUtils {
    /**
     * 分页排序
     * @param page 分页
     * @param sortField 排序字段
     * @param sortOrder 顺序
     */
    public static void setOrderPage(Page<?> page, String sortField, String sortOrder) {
        if (StrUtil.isNotBlank(sortField) && StrUtil.isNotBlank(sortOrder)) {
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn(StrUtil.toUnderlineCase(sortField));
            orderItem.setAsc("descending".equalsIgnoreCase(sortOrder));
            page.addOrder(orderItem);
        }
    }
}