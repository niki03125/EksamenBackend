package app.dtos.fetching;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PackingItemDTO {
    public String name;
    public int weightInGrams;
    public int quantity;
    public String description;
    public String category;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public List<BuyingOptionDTO> buyingOptions;
}
