package com.net.common.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.net.common.dto.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResultVO<T> {
    private Integer tot;
    private Integer len;
    private List<T> list;
    public static <T> ResponseResult convertPageInfoToPageVO(Page<T> pageInfo) {
        PageResultVO<T> pageResultVO = new PageResultVO<>();
        pageResultVO.setList(pageInfo.getRecords());
        pageResultVO.setLen((int) pageInfo.getSize());
        pageResultVO.setTot((int) pageInfo.getTotal());
        return ResponseResult.okResult(pageResultVO);
    }
    public static <T> ResponseResult convertListToPageVO(List<T> list, int size, int total) {
        PageResultVO<T> pageResultVO = new PageResultVO<>();
        pageResultVO.setList(list);
        pageResultVO.setLen(size);
        pageResultVO.setTot(total);
        return ResponseResult.okResult(pageResultVO);
    }

}
