package jp.co.axa.apidemo.entities.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.entities.Employee;

/**
 * Mapper class to convert data that are returned to the requester
 */
@Component
public class EmployeeMapper {

	private ModelMapper mapper;

	public EmployeeMapper() {
		this.mapper = new ModelMapper();
		this.mapper.typeMap(Employee.class, EmployeeDto.class)
		.addMappings(mapper -> mapper.skip(EmployeeDto::setId));
	}
	public EmployeeDto toResponse(Employee emp) {
		return mapper.map(emp, EmployeeDto.class);
	}

	public List<EmployeeDto> toResponse(List<Employee> emps) {
		return mapper.map(emps, new TypeToken<List<EmployeeDto>>() {}.getType());
	}
}
