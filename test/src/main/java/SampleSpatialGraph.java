import org.neo4j.gis.spatial.indexprovider.SpatialIndexProvider;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vignesh on 27/5/15.
 */
public class SampleSpatialGraph {
    public static void main(String[] args) throws IOException {
        List<String> lines = readFile("/home/vignesh/stadiums.csv");

        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase("/home/vignesh/neo4j-community-2.3.0-M01/data/graph.db");

        Transaction tx = db.beginTx();
        long start = System.currentTimeMillis();
        Index<Node> index = createSpatialIndex(db, "stadiumsLocation");
        for (String stadium : lines) {
            String[] columns = stadium.split(",");
            Node stadiumNode = db.createNode();
            stadiumNode.setProperty("wkt", String.format("POINT(%s %s)", columns[4], columns[3]));
            stadiumNode.setProperty("name", columns[0]);
            index.add(stadiumNode, "dummy", "value");
            System.out.println("nodes added");
        }

        Result result = db.execute("START n=node:stadiumsLocation('withinDistance:[53.489271, -2.246704, 40.0]') \n" +
                "RETURN n.name, n.wkt;");
        String rows = "";
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            for (Map.Entry<String, Object> column : row.entrySet()) {
                rows += column.getKey() + ": " + column.getValue() + "; ";
            }
            rows += "\n";
        }
        System.out.println(rows);
        long end = System.currentTimeMillis();
        System.out.print((end - start) / 1000);
        tx.success();
        tx.finish();
    }


    private static List<String> readFile(String stadiumsFile) throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(stadiumsFile));
        br.readLine();
        String line = br.readLine();
        while (line != null) {
            lines.add(line);
            line = br.readLine();
        }
        return lines;
    }

    private static Index<Node> createSpatialIndex(GraphDatabaseService db, String indexName) {
        return db.index().forNodes(indexName, SpatialIndexProvider.SIMPLE_WKT_CONFIG);
    }


}
