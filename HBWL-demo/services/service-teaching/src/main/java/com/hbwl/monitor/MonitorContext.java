package com.hbwl.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitorContext implements Serializable {

    private String userId;

    private String appId;

    @Serial
    private static final long serialVersionUID = 1L;
}
