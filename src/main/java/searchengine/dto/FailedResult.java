package searchengine.dto;

import lombok.Data;

@Data
public class FailedResult {
    Result result;
    String error;
}
