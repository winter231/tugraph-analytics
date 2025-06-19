package com.antgroup.geaflow.dsl;

import com.antgroup.geaflow.dsl.util.SqlQueryTester;
import org.testng.annotations.Test;

public class GQLAlgorithmTest {
  
    @Test
    public void testAlgorithm_009() throws Exception {
        SqlQueryTester
            .build()
            .withGraphDefine("/query/modern_graph.sql")
            .withQueryPath("/query/gql_algorithm_009.sql")
            .execute()
            .checkSinkResult();
    }
}