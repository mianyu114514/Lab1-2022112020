import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

//merge
public class calcShortestPathTest extends TestCase {

  private final static HashMap<String, Map<String, Integer>> textToGraph = new HashMap<>();

  public static void addNode(String node) {
    textToGraph.putIfAbsent(node, new HashMap<>());
  }

  //????
  public static void addEdge(String source, String destination) {
    textToGraph.get(source).merge(destination, 1, Integer::sum);
  }

  public static void buildDirectedGraph(String filePath) throws IOException {
    textToGraph.clear();
    StringBuilder content = new StringBuilder();
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
//            StringBuilder content = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        content.append(line).append(" ");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    String processedContent = content.toString().replaceAll("[^a-zA-Z\\n\\r]", " ").toLowerCase();
    String[] words = processedContent.split("\\s+"); // ?????????
    for (int i = 0; i < words.length - 1; i++) {
      String currentWord = words[i];
      String nextWord = words[i + 1];
      addNode(currentWord);
      addNode(nextWord);
      addEdge(currentWord, nextWord);
    }
  }

  private void initGraph() throws Exception {
    String Path = "./data/lab3.txt";
    buildDirectedGraph(Path);
  }

  private static String calcShortestPath(HashMap<String, Map<String, Integer>> textToGraph,
      String word1, String word2) throws Exception {
    String result = null;
//        System.out.println(textToGraph.containsKey(word1));
//        System.out.println(textToGraph.containsKey(word2));

    if (textToGraph.containsKey(word1) && textToGraph.containsKey(word2)) {
      Map<List<String>, Integer> shortestPath = TextToGraph.calcShortestPath(word1, word2,
          textToGraph);
      if (!shortestPath.isEmpty()) {
        result = TextToGraph.formatShortestPath(textToGraph, shortestPath, word1,
            word2);//白盒可以调用输出函数
      } else {
        result = TextToGraph.formatShortestPath(textToGraph, null, word1, word2);
      }
    } else {
      result = TextToGraph.formatShortestPath(textToGraph, null, word1, word2);
    }
    return result;
  }

  @Before
  public void setup() throws Exception {
    initGraph();
  }

  @Test
  public void testShortestPath1() throws Exception {
    setup();
    String word1 = "a";
    String word2 = "a";
    String result = calcShortestPath(textToGraph, word1, word2);
    String expectedOutput = "a" + "\nthe shortest path:0;\n";
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testShortestPath2() throws Exception {
    setup();
    String word1 = "a";
    String word2 = "b";
    String result = calcShortestPath(textToGraph, word1, word2);
    String expectedOutput = word1 + " or " + word2 + " not in the graph!\n";
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testShortestPath3() throws Exception {
    setup();
    String word1 = "h";
    String word2 = "e";
    String result = calcShortestPath(textToGraph, word1, word2);

    // Define expected output strings
    String expectedOutput = "h→g→e\nthe shortest path:2;\n" +
        "h→m→e\nthe shortest path:2;\n";
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testShortestPath4() throws Exception {
    setup();
    String word1 = "m";
    String word2 = "n";
    String result = calcShortestPath(textToGraph, word1, word2);
    String expectedOutput = word1 + " or " + word2 + " not in the graph!\n";
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testShortestPath5() throws Exception {
    setup();
    String word1 = "k";
    String word2 = "a";
    String result = calcShortestPath(textToGraph, word1, word2);
    String expectedOutput = "No path found between " + word1 + " and " + word2 + "\n";
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testShortestPath6() throws Exception {
    setup();
    String word1 = "a";
    String word2 = "n";
    String result = calcShortestPath(textToGraph, word1, word2);
    String expectedOutput = word1 + " or " + word2 + " not in the graph!\n";
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testShortestPath7() throws Exception {
    setup();
    String word1 = "m";
    String word2 = "b";
    String result = calcShortestPath(textToGraph, word1, word2);
    String expectedOutput = word1 + " or " + word2 + " not in the graph!\n";
    assertEquals(expectedOutput, result);
  }
}