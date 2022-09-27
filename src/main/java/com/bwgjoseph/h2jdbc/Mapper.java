package com.bwgjoseph.h2jdbc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Mapper {
    private int id;
    private String tableName;
    private String dateCol;
    private String accCol;
}
