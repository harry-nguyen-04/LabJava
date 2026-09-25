package vhuwng.lab.D3.dto;

public record LineDto (
    int lineNo,
    int productId,
    String sku,
    String productName,
    int quantity,
    long unitPrice,
    long lineTotal
){
}
