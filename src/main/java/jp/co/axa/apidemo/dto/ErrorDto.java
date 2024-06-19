package jp.co.axa.apidemo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Base class for holding error response details
 */
@ToString
@SuperBuilder
@Getter
@Setter
public class ErrorDto {

	private ErrorCode errorCode;

	private String errorType;
}
