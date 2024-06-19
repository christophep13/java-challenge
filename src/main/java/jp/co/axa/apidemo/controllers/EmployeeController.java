package jp.co.axa.apidemo.controllers;

import java.util.List;

import javax.validation.Valid;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.dto.ErrorCode;
import jp.co.axa.apidemo.dto.ErrorDto;
import jp.co.axa.apidemo.dto.ResponseDto;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.entities.mapper.EmployeeMapper;
import jp.co.axa.apidemo.services.EmployeeService;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

	private static final Logger LOGGER = Logger.getLogger(EmployeeController.class);
	
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private EmployeeMapper mapper;

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> getEmployees() {
        List<Employee> employees = employeeService.retrieveEmployees();
        if(employees == null || employees.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        
        List<EmployeeDto> emps = mapper.toResponse(employees);
        return ResponseEntity.status(HttpStatus.OK).body(emps);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable(name="employeeId")Long employeeId) {
    	Employee emp = employeeService.getEmployee(employeeId);
    	
    	if(emp == null) {
    		return ResponseEntity.status(HttpStatus.OK).body(null);
    	}
    	
    	EmployeeDto empResp = mapper.toResponse(emp);
    	return ResponseEntity.status(HttpStatus.OK).body(empResp);
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeDto> saveEmployee(@Validated @RequestBody Employee employee, BindingResult result) {
        Employee emp = employeeService.saveEmployee(employee);
        
        if(emp == null) {
        	return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        LOGGER.info("Employee " + employee.toString() + " Saved Successfully");
        EmployeeDto empResp = mapper.toResponse(emp);
    	return ResponseEntity.status(HttpStatus.OK).body(empResp);
    }

    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable(name="employeeId")Long employeeId) {
    	
    	if(employeeId == null) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    	}
    	employeeService.deleteEmployee(employeeId);
        LOGGER.info("Employee " + employeeId + " Deleted Successfully");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody Employee employee,
                               @PathVariable(name="employeeId") Long employeeId, BindingResult result) {

    	if(employeeId == null || employee == null || employee.getId() == null) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee Id must not be null");
    	}
        ErrorDto resp = employeeService.updateEmployee(employee);
        
        if(resp.getErrorCode() == ErrorCode.SUCCESS) {
        	EmployeeDto empResp = mapper.toResponse(((ResponseDto) resp).getEmp());
        	return ResponseEntity.status(HttpStatus.OK).body(empResp);
        } else {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp.getErrorType());
        }
    }

}
