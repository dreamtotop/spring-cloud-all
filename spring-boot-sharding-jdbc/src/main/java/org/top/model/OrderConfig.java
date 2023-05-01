package org.top.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderConfig {

    private Long id;

    private Long payTimeout;
}
