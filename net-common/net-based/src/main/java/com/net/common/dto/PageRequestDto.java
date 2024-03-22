package com.net.common.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
/**
 * <p>
 * 分页参数
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-21
 */
@Data
@Slf4j
public class PageRequestDto {

    protected Integer size;
    protected Integer page;

    public void checkParam() {
        if (this.page == null || this.page < 0) {
            setPage(1);
        }
        if (this.size == null || this.size < 0 || this.size > 100) {
            setSize(10);
        }
    }
}
