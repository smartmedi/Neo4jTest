import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.util.Map;

/**
 * Created by vignesh on 28/5/15.
 */
public class CypherQueryRun {
    public static void main(String args[]){
        GraphDatabaseService db= new GraphDatabaseFactory().newEmbeddedDatabase("/home/vignesh/neo4j-community-2.3.0-M01/data/graph.db");

        Transaction tx = db.beginTx();
        Result result = db.execute( "START n=node:stadiumsLocation('withinDistance:[53.489271, -2.246704, 5.0]') \n" +
                "RETURN n.name, n.wkt;" );
        String rows="";
        while ( result.hasNext() )
        {
            Map<String,Object> row = result.next();
            for ( Map.Entry<String,Object> column : row.entrySet() )
            {
                rows += column.getKey() + ": " + column.getValue() + "; ";
            }
            rows += "\n";
        }
        System.out.println(rows);
        tx.success();
        tx.close();
    }
}
