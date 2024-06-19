package jp.co.axa.apidemo.services;

import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jp.co.axa.apidemo.dto.ErrorCode;
import jp.co.axa.apidemo.dto.ErrorDto;
import jp.co.axa.apidemo.dto.ResponseDto;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService{

	private static final Logger LOGGER = Logger.getLogger(EmployeeServiceImpl.class);
	
    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * do not annotate the method with @Cacheable to avoid issue if 
     */
    public List<Employee> retrieveEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }
    
    @Cacheable(value = "employee", key= "#employeeId")
    public Employee getEmployee(Long employeeId) {
    	try {
    		Optional<Employee> optEmp = employeeRepository.findById(employeeId);
    		return optEmp.orElse(null);
	    } catch (IllegalArgumentException e) {
			LOGGER.error(e);
			return null;
		}
        
    }

    @CachePut(value = "employee", key = "#p0.id")
    public Employee saveEmployee(Employee employee){
    	// since we are saving a new employee, no Id should be specified to avoid updating existing record
    	employee.setId(null);
        return employeeRepository.save(employee);
    }

    @CacheEvict(value = "employee", key= "#employeeId")
    public ErrorDto deleteEmployee(Long employeeId){
    	try {
    		if(employeeRepository.existsById(employeeId)) {
    			employeeRepository.deleteById(employeeId);
    			return ResponseDto.builder().errorCode(ErrorCode.SUCCESS).build();
    		} else {
    			if(LOGGER.isDebugEnabled()) {
    				LOGGER.info("Employee " + employeeId + " not found");
    			}
    			return ResponseDto.builder().errorCode(ErrorCode.NOT_FOUND).build();
    		}
    	} catch (IllegalArgumentException e) {
    		LOGGER.error(e);
    		return ResponseDto.builder().errorCode(ErrorCode.ID_NULL).build();
    	}
    }
    
    @CachePut(value = "employee", key = "#employee.id")
    public ErrorDto updateEmployee(Employee employee) {
    	if(employee.getId() == null) {
    		return ResponseDto.builder().errorCode(ErrorCode.ID_NULL).errorType("Employee Id must not be null").build();
    	}
    	try {
    		if(employeeRepository.existsById(employee.getId())) {
    			Employee emp = employeeRepository.save(employee);
    			return ResponseDto.builder().emp(emp).errorCode(ErrorCode.SUCCESS).build();
    		} else {
    			if(LOGGER.isDebugEnabled()) {
    				LOGGER.info("Employee " + employee.getId() + " not found");
    			}
    			return ResponseDto.builder().errorCode(ErrorCode.NOT_FOUND).build();
    		}
    	} catch (IllegalArgumentException e) {
    		LOGGER.error(e);
    		return ResponseDto.builder().errorCode(ErrorCode.NOT_FOUND).build();
    	}
    }
}
