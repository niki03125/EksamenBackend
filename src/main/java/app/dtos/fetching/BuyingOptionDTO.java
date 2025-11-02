package app.dtos.fetching;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BuyingOptionDTO {
    public String shopName;
    public String shopUrl;
    public  int price;
}
