package com.ledoyen.sql.querybuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

public abstract class AbstractTest {

	protected Integer ageMin;
	protected Integer ageMax;
	protected Double scoreMin;
	protected Double scoreMax;
	protected boolean scoreMinInclusive;
	protected boolean scoreMaxInclusive;
	protected String civility;
	protected List<String> names;
	protected Date startDate;
	protected Date endDate;
	protected String code;
	protected String category;

	public AbstractTest(Integer ageMin, Integer ageMax, Double scoreMin, Double scoreMax,
			boolean scoreMinInclusive, boolean scoreMaxInclusive, String civility,
			List<String> names, Date startDate, Date endDate, String code, String category) {
		this.ageMin = ageMin;
		this.ageMax = ageMax;
		this.scoreMin = scoreMin;
		this.scoreMax = scoreMax;
		this.scoreMinInclusive = scoreMinInclusive;
		this.scoreMaxInclusive = scoreMaxInclusive;
		this.civility = civility;
		this.names = names;
		this.startDate = startDate;
		this.endDate = endDate;
		this.code = code;
		this.category = category;
	}

	@Parameters
	public static Collection<Object[]> params() {
	    return Arrays.asList(
	    		new Object[] { null, null, null, null, true, true, null, null, null, null, null, null},
	            new Object[] { 7, 77, 0d, null, true, true, "M.", Lists.newArrayList("toto", "titi"), new Date(), null, null, null}
	            );
	}
}
