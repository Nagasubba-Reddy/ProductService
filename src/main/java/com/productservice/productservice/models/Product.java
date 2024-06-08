package com.productservice.productservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseModel {
    private String title;
    private String description;
    private String image;

    private Category category;

    private Price price;
    private int inventoryCount;
}
