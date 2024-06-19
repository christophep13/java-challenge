package jp.co.axa.apidemo.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.axa.apidemo.entities.Employee;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {
	
	@Autowired
	private CacheManager cacheManager;
	
	private MockMvc mvc;

	@Autowired
	private EmployeeController controller;
	
	@Before
	public void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	};

	@Test
	public void allEmployees_noRecordShouldReturnEmpty() throws Exception {
		cacheManager.getCache("employee").clear();
		mvc.perform(MockMvcRequestBuilders.get("/api/v1/employees")).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void addEmployee_shouldReturnOne() throws Exception {
		cacheManager.getCache("employee").clear();
		Employee emp = Employee.builder().name("John").department("Dev").salary(1000).build();
		ObjectMapper mapper = new ObjectMapper();
		String empJson = mapper.writeValueAsString(emp);
		
		MvcResult result = mvc.perform(
				MockMvcRequestBuilders
					.post("/api/v1/employees")
					.contentType(MediaType.APPLICATION_JSON)
					.content(empJson)
				).andReturn();
		assertThat(result.getResponse().getContentAsString().contains(emp.getName()));
		mvc.perform(MockMvcRequestBuilders.get("/api/v1/employees")).andExpect(status().is2xxSuccessful());
		// ensure that the entry is not in the cache since it is disabled for findAll
		Optional<Employee> cacheEmp = Optional.ofNullable(cacheManager.getCache("employee")).map(e -> e.get(1L, Employee.class));
		assertThat(!cacheEmp.isPresent());
				
		mvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/1")).andExpect(status().is2xxSuccessful());
		// ensure that the entry is in the cache
		cacheEmp = Optional.ofNullable(cacheManager.getCache("employee")).map(e -> e.get(2L, Employee.class));
		assertEquals(emp.getName(), cacheEmp.get().getName());
	}
	
	@Test
	public void addEmployee_thenDelete_shouldReturnEmpty() throws Exception {
		cacheManager.getCache("employee").clear();
		Employee emp = Employee.builder().name("John").department("Dev").salary(1000).build();
		ObjectMapper mapper = new ObjectMapper();
		String empJson = mapper.writeValueAsString(emp);
		
		MvcResult result = mvc.perform(
				MockMvcRequestBuilders
					.post("/api/v1/employees")
					.contentType(MediaType.APPLICATION_JSON)
					.content(empJson)
				).andReturn();
		assertThat(result.getResponse().getContentAsString().contains(emp.getName()));
		mvc.perform(MockMvcRequestBuilders.get("/api/v1/employees")).andExpect(status().is2xxSuccessful());
		// ensure that the entry is in the cache
		Optional<Employee> cacheEmp = Optional.ofNullable(cacheManager.getCache("employee")).map(e -> e.get(1L, Employee.class));
		assertEquals(emp.getName(), cacheEmp.get().getName());
		
		result = mvc.perform(
				MockMvcRequestBuilders
					.delete("/api/v1/employees/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(empJson)
				).andReturn();
		// ensure that the entry is not in the cache
		cacheEmp = Optional.ofNullable(cacheManager.getCache("employee")).map(e -> e.get(1L, Employee.class));
		assertThat(!cacheEmp.isPresent());
	}
}

