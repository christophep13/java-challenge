package jp.co.axa.apidemo.dto;

import jp.co.axa.apidemo.entities.Employee;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString
@SuperBuilder
@Getter
@Setter
public class ResponseDto extends ErrorDto {

	private Employee emp;
}
