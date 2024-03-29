﻿using NimbleFlow.Api.Repositories.Base;
using NimbleFlow.Data.Context;
using NimbleFlow.Data.Models;

namespace NimbleFlow.Api.Repositories;

public class CategoryRepository : RepositoryBase<NimbleFlowContext, Category>
{
    public CategoryRepository(NimbleFlowContext dbContext) : base(dbContext)
    {
    }
}