import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;


public class queryBridgeWordsTest extends TestCase {

  private TextToGraph testGraph;

  private void initGraph() throws Exception {
    String Path = "./data/lab3.txt";
    testGraph = new TextToGraph();
    testGraph.buildDirectedGraph(Path);
  }

  @Before
  public void setup() throws Exception {
    initGraph();
  }

  @Test
  public void testQueryBridgeWordsCase1() throws Exception {
    setup();
    List<String> result = testGraph.queryBridgeWords("h", "a");
    List<String> expected = new ArrayList<>();
    expected.add("i");
    assertEquals(expected, result);
  }

  @Test
  public void testQueryBridgeWordsCase2() throws Exception {
    setup();
    List<String> result = testGraph.queryBridgeWords("h", "c");
    List<String> expected = new ArrayList<>();
    assertEquals(expected, result);
  }

  @Test
  public void testQueryBridgeWordsCase3() throws Exception {
    setup();
    List<String> result = testGraph.queryBridgeWords("a", "b");
    List<String> expected = new ArrayList<>();
    assertEquals(expected, result);
  }

  @Test
  public void testQueryBridgeWordsCase4() throws Exception {
    setup();
    List<String> result = testGraph.queryBridgeWords("g", "");
    //List<String> result = testGraph.queryBridgeWords("", "g");
    List<String> expected = new ArrayList<>();
    assertEquals(expected, result);
  }

  @Test
  public void testQueryBridgeWordsCase5() throws Exception {
    setup();
    List<String> result = testGraph.queryBridgeWords("q", "a");
    List<String> expected = new ArrayList<>();
    assertEquals(expected, result);
  }

  @Test
  public void testQueryBridgeWordsCase6() throws Exception {
    setup();
    List<String> result = testGraph.queryBridgeWords("c", "e");
    List<String> expected = new ArrayList<>();
    expected.add("d");
    assertEquals(expected, result);
  }

  @Test
  public void testQueryBridgeWordsCase7() throws Exception {
    setup();
    List<String> result = testGraph.queryBridgeWords("", "");
    List<String> expected = new ArrayList<>();
    assertEquals(expected, result);
  }

}
