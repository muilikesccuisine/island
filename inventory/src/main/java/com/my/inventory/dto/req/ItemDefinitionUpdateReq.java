package com.my.inventory.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDefinitionUpdateReq {

    @NotNull
    private Long id;

    @NotBlank(message = "物品名称不能为空")
    private String name;

    @NotBlank(message = "物品代码不能为空")
    private String code;

    @NotNull(message = "物品类型不能为空")
    private Integer type;

    private String unit;

    private String description;

}
