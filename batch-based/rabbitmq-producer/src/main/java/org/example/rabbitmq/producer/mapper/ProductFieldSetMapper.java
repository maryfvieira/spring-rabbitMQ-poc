package org.example.rabbitmq.producer.mapper;


import org.example.rabbitmq.producer.dto.ProductDTO;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class ProductFieldSetMapper implements FieldSetMapper<ProductDTO> {

    @Override
    public ProductDTO mapFieldSet(FieldSet fieldSet) throws BindException {
        return new ProductDTO(
                fieldSet.readLong("productId"),
                fieldSet.readString("productName"),
                fieldSet.readString("productBrand"),
                fieldSet.readDouble("price"),
                fieldSet.readString("description"));
    }
}