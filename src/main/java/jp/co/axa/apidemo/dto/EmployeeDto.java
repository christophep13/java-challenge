package jp.co.axa.apidemo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Wrap class representing the data that is actually presented to the requester
 */
@ToString
@Getter
@Setter
public class EmployeeDto {

    private Long id;

    private String name;

    private Integer salary;

    private String department;
}