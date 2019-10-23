package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;

public class JDBCDepartmentDAO implements DepartmentDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Department> getAllDepartments() {
		ArrayList<Department> departments = new ArrayList<> ();
		String sqlAllDepartment = "SELECT department_id, name FROM department";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllDepartment);
		while(results.next()) {
			Department department = mapRowToDepartment(results);
			departments.add(department);
		}
		
		return departments;
	}

	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		ArrayList<Department> departments = new ArrayList<> ();
		String sqlAllDepartment = "SELECT department_id, name FROM department WHERE name ILIKE '%'|| ? ||'%'";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllDepartment,nameSearch);
		while(results.next()) {
			Department department = mapRowToDepartment(results);
			departments.add(department);
		}
		
		return departments;
	}

	@Override
	public void saveDepartment(Department updatedDepartment) {
		String sqlAllDepartment = "UPDATE department SET name = ? WHERE department_id = ?";
		jdbcTemplate.update (sqlAllDepartment, updatedDepartment.getName(), updatedDepartment.getId());
	}

	@Override
	public Department createDepartment(Department newDepartment) {
		long nextDepartmentId = getNextDepartmentId();
		newDepartment.setId(nextDepartmentId);
		String sqlAllDepartment = "INSERT INTO department (department_id, name) VALUES (?,?)";
		jdbcTemplate.update(sqlAllDepartment, newDepartment.getId(), newDepartment.getName());
		return newDepartment;
		
	}

	@Override
	public Department getDepartmentById(Long id) {
		Department departments = null;
		String sqlAllDepartment = "SELECT department_id, name FROM department WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllDepartment, id);
		if(results.next()) {
			Department department = mapRowToDepartment(results);
			departments = department;
		}
		
		return departments;
		
	}

	private Department mapRowToDepartment(SqlRowSet results) {
		Department departments = new Department();//making a pojo
		departments.setId(results.getLong("department_id"));
		departments.setName(results.getString("name"));
		
		
		return departments;
	}
	
	private long getNextDepartmentId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet ("SELECT nextval('seq_department_id')");
		if(nextIdResult.next()) {
			long nextID = nextIdResult.getLong(1);
			return nextID;
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new department");
		}
	}
}
