# SQL-Query-Builder

A tool to help you compose native sql queries and make DAO code more readable.

Assuming the definition of a library of clauses, your DAO code will look like :

```java
NativeQueryBuilder nqb = NativeQueryBuilder
	.select(INITIAL_SELECT)
	.where(JOIN_USER_CIVILITY,
			JOIN_USER_PROFILE_VALUE,
			JOIN_USER_BUSINESS_UNIT,
			PROVILE_VALUE_HISTO.with(false),
			USER_AGE.with(ageMin, ageMax),
			PROFILE_VALUE_SCORE_MIN.with(scoreMin, scoreMinInclusive),
			PROFILE_VALUE_SCORE_MAX.with(scoreMax, scoreMaxInclusive),
			"M.".equals(civility) ? CIVILITY_MR : CIVILITY_NOT_MR,
			PROFILE_VALUE_VARIABLE_NAMES.with(variableNames),
			CREATION_DATE.with(Dates.floor(startDate), Dates.ceiling(endDate != null ? endDate : startDate)),
			BUSINESS_UNIT_CODE.with(code))
	.groupOrOrder("group by pv.var_name");

List<?> results = nqb.query(getEntityManager()).getResultList();
```

Depending on the type of clause, you will benefit from implicit parameter check :

```java
if(variableNames != null && !variableNames.isEmpty()) {
	sql.append(" and pv.variable_name in (:variableNames) ");
}
...
if(variableNames != null && !variableNames.isEmpty()) {
	query.setParameter("variableNames", variableNames);
}
```
is done by the equivalent clause :
```java
PROFILE_VALUE_VARIABLE_NAMES.with(variableNames)
```
