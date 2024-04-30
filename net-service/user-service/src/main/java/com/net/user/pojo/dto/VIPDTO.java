package com.net.user.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * VIP参数
 * </p>
 *
 * @author CheeseLongan
 * @since 2024-04-30
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VIPDTO {
    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 花费
     */
    private Integer money;

    /**
     * 持续时间
     */
    private Integer duration;
}
