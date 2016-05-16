package com.ledoyen.cukesalad.stepdef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;

public class SqlStepDef {

	@Autowired
	private DataSource dataSource;

	@Given("^an SQL table (\\S+) containing$")
	public void a_call_on_service_method_is_made(String tableName, DataTable data) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(tableName).append(" (");
		sql.append(data.topCells().stream().map(String::toUpperCase).collect(Collectors.joining(", ")));
		sql.append(") VALUES (");
		sql.append(IntStream.range(0, data.topCells().size()).mapToObj(i -> "?").collect(Collectors.joining(", ")));
		sql.append(")");
		try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
			// CREATE TABLE IF NOT EXISTS TEST(ID INT PRIMARY KEY, NAME VARCHAR(255));
			for (List<String> line : data.cells(1)) {
				int i = 1;
				for (String value : line) {
					ps.setString(i, value);
					i++;
				}
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}
}
