# SQL-Query-Builder

A tool to help you compose native sql queries and make DAO code more readable.

Assuming the definition of a library of clauses, your DAO code will look like :

```java
NativeQueryBuilder nqb = NativeQueryBuilder
	.select(INITIAL_SELECT)
	.where(JOIN_USER_CIVILITY,
			JOIN_USER_SCORE,
			JOIN_USER_REGION,
			SCORE_HISTO.with(false),
			USER_AGE.with(ageMin, ageMax),
			SCORE_MIN.with(scoreMin, scoreMinInclusive),
			SCORE_MAX.with(scoreMax, scoreMaxInclusive),
			"M.".equals(civility) ? CIVILITY_MR : CIVILITY_NOT_MR,
			SCORE_NAMES.with(names),
			CREATION_DATE.with(Dates.floor(startDate),
					Dates.ceiling(endDate != null ? endDate : startDate)),
			REGION_CODE.with(code),
			REGION_CATEGORY.with(category))
	.groupOrOrder("group by s.name");

List<?> results = nqb.query(getEntityManager()).getResultList();
```

Depending on the type of clause, you will benefit from implicit parameter check :

```java
if(names != null && !names.isEmpty()) {
	sql.append(" and s.name in (:names) ");
}
...
if(names != null && !names.isEmpty()) {
	query.setParameter("names", names);
}
```
is done by the equivalent clause :
```java
SCORE_NAMES.with(names)
```
