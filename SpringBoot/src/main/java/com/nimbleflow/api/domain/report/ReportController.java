package com.nimbleflow.api.domain.report;

import com.nimbleflow.api.domain.order.OrderDTO;
import com.nimbleflow.api.domain.product.ProductDTO;
import com.nimbleflow.api.domain.report.dto.ReportDTO;
import com.nimbleflow.api.exception.response.example.ExceptionResponseExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Report Controller")
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ExceptionResponseExample.UnauthorizedException.class))
)
@RequestMapping(value = "api/v1/report", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/orders/month-report")
    @Operation(description = "Get orders month report")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content)
    })
    public ResponseEntity<ReportDTO<OrderDTO>> findOrdersByInterval(
            @RequestParam(value = "getDeletedOrders", required = false) boolean getDeletedOrders
    ) {
        log.info(String.format("Getting orders month report, getDeletedOrders: %s", getDeletedOrders));
        ReportDTO<OrderDTO> responseBody = reportService.getOrdersMonthReport(getDeletedOrders);
        HttpStatus httpStatus = !responseBody.getItems().isEmpty() ? HttpStatus.OK : HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(responseBody, httpStatus);
    }

    @GetMapping("/orders/interval")
    @Operation(description = "Get orders report by interval")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content)
    })
    public ResponseEntity<ReportDTO<OrderDTO>> getOrdersReportByInterval(
            @RequestParam(value = "getDeletedOrders", required = false) boolean getDeletedOrders,
            @RequestParam(value = "startDate") ZonedDateTime startDate,
            @RequestParam(value = "endDate") ZonedDateTime endDate
    ) {
        log.info(String.format("Getting orders report by interval (startDate: %s, endDate: %s, getDeletedOrders: %s)", startDate, endDate, getDeletedOrders));
        ReportDTO<OrderDTO> responseBody = reportService.getOrdersReportByInterval(startDate, endDate, getDeletedOrders);
        HttpStatus httpStatus = !responseBody.getItems().isEmpty() ? HttpStatus.OK : HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(responseBody, httpStatus);
    }

    @GetMapping("/products/top-sold")
    @Operation(description = "Get top sol sold products report")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content)
    })
    public ResponseEntity<ReportDTO<ProductDTO>> getTopSoldProductsReport(
            @RequestParam(value = "getDeletedOrders", required = false) boolean getDeletedOrders,
            @RequestParam(value = "maxProducts", required = false) Integer maxProducts
    ) {
        log.info(String.format("Getting top sold products report (maxProducts: %s, getDeletedOrders: %s)", maxProducts, getDeletedOrders));
        ReportDTO<ProductDTO> responseBody = reportService.getTopSoldProductsReport(maxProducts, getDeletedOrders);
        HttpStatus httpStatus = !responseBody.getItems().isEmpty() ? HttpStatus.OK : HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(responseBody, httpStatus);
    }
}