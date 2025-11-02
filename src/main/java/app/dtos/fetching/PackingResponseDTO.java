package app.dtos.fetching;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PackingResponseDTO {
    public List<PackingItemDTO> items;
}
