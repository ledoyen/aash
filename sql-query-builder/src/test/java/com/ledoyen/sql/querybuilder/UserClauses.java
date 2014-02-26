package com.ledoyen.sql.querybuilder;

import com.ledoyen.sql.querybuilder.clause.DefaultWhereClause;
import com.ledoyen.sql.querybuilder.clause.DefaultWhereClause.FormattedDefaultWhereClauseBinder;
import com.ledoyen.sql.querybuilder.clause.DoubleBindedWhereClause.DoubleBindedWhereClauseBuilder;
import com.ledoyen.sql.querybuilder.clause.OperatorSimpleBindedWhereClauseBuilder;
import com.ledoyen.sql.querybuilder.clause.OperatorSimpleBindedWhereClauseBuilder.Operator;
import com.ledoyen.sql.querybuilder.clause.SimpleBindedWhereClause.CollectionSimpleBindedWhereClauseBuilder;
import com.ledoyen.sql.querybuilder.clause.SimpleBindedWhereClause.SimpleBindedWhereClauseBuilder;

public interface UserClauses {

	String INITIAL_SELECT = new StringBuilder("select count(s.user_id), s.name  from (((scores s ")
									.append("join users u) join civility c) join region r) ").toString();

	DefaultWhereClause JOIN_USER_CIVILITY = new DefaultWhereClause("c.id = u.civility_id");
    DefaultWhereClause JOIN_USER_PROFILE_VALUE = new DefaultWhereClause("s.user_id = u.id");
    DefaultWhereClause JOIN_USER_BUSINESS_UNIT = new DefaultWhereClause("u.region_id = r.id");
    FormattedDefaultWhereClauseBinder PROVILE_VALUE_HISTO = new FormattedDefaultWhereClauseBinder("s.is_histo is %s");
    DoubleBindedWhereClauseBuilder USER_AGE = new DoubleBindedWhereClauseBuilder(
	    "timestampdiff(YEAR, u.birth_date, sysdate()) between :ageMin and :ageMax", "ageMin", "ageMax");
    OperatorSimpleBindedWhereClauseBuilder PROFILE_VALUE_SCORE_MIN = new OperatorSimpleBindedWhereClauseBuilder("s.score %s :scoreMin",
	    "scoreMin", Operator.MORE);
    OperatorSimpleBindedWhereClauseBuilder PROFILE_VALUE_SCORE_MAX = new OperatorSimpleBindedWhereClauseBuilder("s.score %s :scoreMax",
	    "scoreMax", Operator.LESS);
    DefaultWhereClause CIVILITY_MR = new DefaultWhereClause("c.name = 'M.'");
    DefaultWhereClause CIVILITY_NOT_MR = new DefaultWhereClause("c.name != 'M.'");
    CollectionSimpleBindedWhereClauseBuilder PROFILE_VALUE_VARIABLE_NAMES = new CollectionSimpleBindedWhereClauseBuilder(
	    "s.name in (:names) ", "names");
    DoubleBindedWhereClauseBuilder CREATION_DATE = new DoubleBindedWhereClauseBuilder("u.creation_date between :startDate and :endDate", "startDate",
	    "endDate");
    SimpleBindedWhereClauseBuilder BUSINESS_UNIT_CODE = new SimpleBindedWhereClauseBuilder("r.code = :code", "code");
    SimpleBindedWhereClauseBuilder BUSINESS_UNIT_CATEGORY = new SimpleBindedWhereClauseBuilder("r.category = :category", "category");
}
