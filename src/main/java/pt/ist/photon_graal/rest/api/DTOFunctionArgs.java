package pt.ist.photon_graal.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DTOFunctionArgs {
    private final Object[] args;

    public DTOFunctionArgs(
            @JsonProperty(value = "args", required = true) Object[] args) {
        this.args = args;
    }
}
