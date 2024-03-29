using NimbleFlow.Api.ConfigurationExtensions;
using NimbleFlow.Api.ServiceCollectionExtensions;
using Serilog;

var builder = WebApplication.CreateBuilder(args);

Log.Logger = new LoggerConfiguration().WriteTo.Console().CreateLogger();
builder.Host.UseSerilog();

builder.Configuration.ConfigureSwaggerOptions();
builder.Services.InjectCors();
builder.Services.InjectOptions(builder.Configuration);
builder.Services.InjectDatabases(builder.Configuration);
builder.Services.InjectRepositories();
builder.Services.InjectServices();
builder.Services.AddControllers();
builder.Services.InjectSwagger(out var enableSwagger);

var app = builder.Build();
if (enableSwagger)
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapControllers();
app.Run();