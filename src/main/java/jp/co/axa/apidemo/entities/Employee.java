package jp.co.axa.apidemo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="EMPLOYEE")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor // necessary for JUnit
@NoArgsConstructor // necessary for JUnit
public class Employee {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Employee name is required")
    @Column(name="EMPLOYEE_NAME")
    private String name;

    @Min(value=0, message = "Employee salary is required")
    @Column(name="EMPLOYEE_SALARY")
    private Integer salary;

    @NotEmpty(message = "Employee department is required")
    @Column(name="DEPARTMENT")
    private String department;
}
