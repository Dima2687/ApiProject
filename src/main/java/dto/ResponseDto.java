package dto;

import lombok.Data;

@Data
public class ResponseDto {
    private Integer code;
    private String type;
    private String message;
}
