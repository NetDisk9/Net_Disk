package com.net.user.pojo.vo;

import lombok.Data;

import java.util.List;
@Data
public class PageVO<T> {
    private Integer tot;
    private Integer len;
    private List<T> list;
}
