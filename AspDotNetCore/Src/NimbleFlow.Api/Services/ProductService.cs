﻿using System.Net;
using Microsoft.EntityFrameworkCore;
using NimbleFlow.Api.Extensions;
using NimbleFlow.Api.Repositories;
using NimbleFlow.Api.Services.Base;
using NimbleFlow.Contracts.DTOs.Products;
using NimbleFlow.Data.Context;
using NimbleFlow.Data.Models;
using NimbleFlow.Data.Partials.DTOs;

namespace NimbleFlow.Api.Services;

public class ProductService : ServiceBase<CreateProductDto, ProductDto, NimbleFlowContext, Product>
{
    private readonly ProductRepository _productRepository;

    public ProductService(ProductRepository productRepository) : base(productRepository)
    {
        _productRepository = productRepository;
    }

    public new async Task<(int totalAmount, IEnumerable<ProductDto>)> GetAllPaginated(
        int page,
        int limit,
        bool includeDeleted
    )
    {
        var (totalAmount, entities) = await _productRepository.GetAllEntitiesPaginated(page, limit, includeDeleted);
        return (totalAmount, entities.Select(x => x.ToDto()));
    }

    public new async Task<(HttpStatusCode, ProductDto?)> Create(CreateProductDto createDto)
    {
        try
        {
            var response = await _productRepository.CreateEntity(createDto.ToModel());
            if (response is null)
                return (HttpStatusCode.InternalServerError, null);

            return (HttpStatusCode.Created, response.ToDto());
        }
        catch (DbUpdateException e)
        {
            if (e.InnerException?.Message.Contains("FOREIGN", StringComparison.InvariantCultureIgnoreCase) ?? false)
                return (HttpStatusCode.BadRequest, null);

            return (HttpStatusCode.Conflict, null);
        }
    }

    public async Task<(int totalAmount, IEnumerable<ProductDto>)> GetAllProductsPaginatedByCategoryId(
        int page,
        int limit,
        bool includeDeleted,
        Guid categoryId
    )
    {
        var (totalAmount, products) = await _productRepository.GetAllProductsPaginatedByCategoryId(
            page,
            limit,
            includeDeleted,
            categoryId
        );
        return (totalAmount, products.Select(x => x.ToDto()));
    }

    public async Task<(HttpStatusCode, ProductDto?)> UpdateProductById(Guid productId, UpdateProductDto productDto)
    {
        var productEntity = await _productRepository.GetEntityById(productId);
        if (productEntity is null)
            return (HttpStatusCode.NotFound, null);

        var shouldUpdate = false;
        if (productDto.Title.IsNotNullAndNotEquals(productEntity.Title))
        {
            productEntity.Title = productDto.Title ?? throw new NullReferenceException();
            shouldUpdate = true;
        }

        if (productDto.Description != productEntity.Description
            && (productDto.Description is not null && productDto.Description.Trim() != string.Empty
                || productDto.Description is null))
        {
            productEntity.Description = productDto.Description ?? throw new NullReferenceException();
            shouldUpdate = true;
        }

        if (productDto.Price is not null && productDto.Price != productEntity.Price)
        {
            productEntity.Price = productDto.Price.Value;
            shouldUpdate = true;
        }

        if (productDto.ImageUrl != productEntity.ImageUrl
            && (productDto.ImageUrl is not null && productDto.ImageUrl.Trim() != string.Empty
                || productDto.ImageUrl is null))
        {
            productEntity.ImageUrl = productDto.ImageUrl;
            shouldUpdate = true;
        }

        if (productDto.IsFavorite is not null && productDto.IsFavorite != productEntity.IsFavorite)
        {
            productEntity.IsFavorite = productDto.IsFavorite.Value;
            shouldUpdate = true;
        }

        if (productDto.CategoryId is not null
            && productDto.CategoryId != Guid.Empty
            && productDto.CategoryId != productEntity.CategoryId)
        {
            productEntity.CategoryId = productDto.CategoryId.Value;
            shouldUpdate = true;
        }

        if (!shouldUpdate)
            return (HttpStatusCode.NotModified, null);

        try
        {
            var updatedProduct = await _productRepository.UpdateEntity(productEntity);
            if (updatedProduct is null)
                return (HttpStatusCode.NotModified, null);

            return (HttpStatusCode.OK, updatedProduct.ToDto());
        }
        catch (DbUpdateException)
        {
            return (HttpStatusCode.Conflict, null);
        }
    }
}