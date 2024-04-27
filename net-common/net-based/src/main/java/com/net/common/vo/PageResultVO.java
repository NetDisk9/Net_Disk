package com.net.common.vo;

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
}
