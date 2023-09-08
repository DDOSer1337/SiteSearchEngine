package searchengine.dto.result;

import lombok.Data;
import searchengine.dto.result.Result;

@Data
public class FailedResult {
    Result result;
    String error;
}
