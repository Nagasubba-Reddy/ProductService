package com.productservice.productservice.services;

import com.productservice.productservice.dtos.FakeStoreProductDto;
import com.productservice.productservice.dtos.GenericProductDto;
import com.productservice.productservice.exceptions.ProductNotFoundException;
import com.productservice.productservice.thirdPartyClients.fakestoreclient.FakeStoreClient;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class FakeStoreProductService implements ProductService {
    private final FakeStoreClient fakeStoreAdapter;
    private RedisTemplate<String, FakeStoreProductDto> redisTemplate;

    FakeStoreProductService(FakeStoreClient fakeStoreAdapter, RedisTemplate redisTemplate) {
        this.fakeStoreAdapter = fakeStoreAdapter;
        this.redisTemplate = redisTemplate;
    }

    private static GenericProductDto convertToGenericProductDto(FakeStoreProductDto fakeStoreProductDto) {
        GenericProductDto genericProductDto = new GenericProductDto();
        genericProductDto.setId(fakeStoreProductDto.getId());
        genericProductDto.setImage(fakeStoreProductDto.getImage());
        genericProductDto.setCategory(fakeStoreProductDto.getCategory());
        genericProductDto.setDescription(fakeStoreProductDto.getDescription());
        genericProductDto.setTitle(fakeStoreProductDto.getTitle());
        genericProductDto.setPrice(fakeStoreProductDto.getPrice());

        return genericProductDto;
    }

    @Override
    public GenericProductDto getProductById(Long id) throws ProductNotFoundException {
        //Integrate the FakeStore API.
       FakeStoreProductDto fakeStoreProductDto = (FakeStoreProductDto) redisTemplate.opsForHash().get("PRODUCTS", id);

        if (fakeStoreProductDto != null) {
            return convertToGenericProductDto(fakeStoreProductDto);
        }

        fakeStoreProductDto = fakeStoreAdapter.getProductById(id);
        redisTemplate.opsForHash().put("PRODUCTS", id, fakeStoreProductDto);
        return convertToGenericProductDto(fakeStoreProductDto);
    }

    @Override
    public List<GenericProductDto> getAllProducts() {
        List<FakeStoreProductDto> fakeStoreProductDtos = fakeStoreAdapter.getAllProducts();

        List<GenericProductDto> genericProductDtos = new ArrayList<>();
        for (FakeStoreProductDto fakeStoreProductDto : fakeStoreProductDtos) {
            genericProductDtos.add(convertToGenericProductDto(fakeStoreProductDto));
        }
        return genericProductDtos;
    }

    @Override
    public GenericProductDto deleteProductById(Long id) {
        return convertToGenericProductDto(fakeStoreAdapter.deleteProductById(id));
    }

    @Override
    public GenericProductDto createProduct(GenericProductDto genericProductDto) {
        return convertToGenericProductDto(fakeStoreAdapter.createProduct(genericProductDto));
    }
}
