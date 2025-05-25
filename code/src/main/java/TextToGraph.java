import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
//1
//第一次修改提交OVO
//第二次修改提交Ciallo～(∠?ω< )⌒★
//C4修改文件Ciallo～(∠?ω< )⌒★

/**
 * UI界面.
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("PATH_TRAVERSAL_IN")
public class TextToGraph extends JFrame {

  //protected static Graph graph;
  private static final HashMap<String, Map<String, Integer>> textToGraph = new HashMap<>();
  private JTextArea textArea;
  private JButton chooseFileButton;
  private JButton buildGraphButton;
  private JButton queryButton;
  private JButton insertButton;
  private JButton shortestPathButton;
  private JButton pageRankButton;
  private JButton randomPathButton;
  private JButton exitButton;
  private JTextField word1Field;
  private JTextField word2Field;
  private File selectedFile;

  /**
   * 初始化.
   */
  public TextToGraph() {
    setTitle("Directed Graph Operations");
    setSize(900, 800);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    textArea = new JTextArea();
    textArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
    JScrollPane scrollPane = new JScrollPane(textArea);
    textArea.setEditable(false);
    add(scrollPane, BorderLayout.CENTER);
    // 设置窗口居中显示
    setLocationRelativeTo(null);
    //1
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout());

    chooseFileButton = new JButton("Choose Text File");
    buildGraphButton = new JButton("Build Graph");
    queryButton = new JButton("Query BridgeWords");
    insertButton = new JButton("generateNewText");
    shortestPathButton = new JButton("Calculate Shortest Path");
    pageRankButton = new JButton("Calculate PageRank");
    randomPathButton = new JButton("Random Path");
    exitButton = new JButton("exit!");

    word1Field = new JTextField(10);
    word2Field = new JTextField(10);

    controlPanel.add(chooseFileButton);
    controlPanel.add(buildGraphButton);
    controlPanel.add(queryButton);
    controlPanel.add(insertButton);
    controlPanel.add(insertButton);
    controlPanel.add(shortestPathButton);
    controlPanel.add(pageRankButton);
    controlPanel.add(randomPathButton);
    controlPanel.add(exitButton);
    add(controlPanel, BorderLayout.SOUTH);

    //TextToGraph textToGraph = new TextToGraph();

    // 添加监听
    chooseFileButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        int result = fileChooser.showOpenDialog(TextToGraph.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          selectedFile = fileChooser.getSelectedFile();
          //buildDirectedGraph(selectedFile.getAbsolutePath());
          //printGraph();
          selectedFile = fileChooser.getSelectedFile();
          textArea.append("Selected file: " + selectedFile.getAbsolutePath() + "\n");
        }
      }
    });

    buildGraphButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (selectedFile != null) {
          try {
            String dotPath = "./result/directed_graph.dot";
            String imagePath = "./result/directed_graph.png";
            buildDirectedGraph(selectedFile.getAbsolutePath());
            generateDotFile(dotPath);
            convertDotToImage(dotPath, imagePath);
            displayImage(imagePath);
            textArea.append("Directed graph built successfully.\n");
          } catch (Exception ex) {
            textArea.append("Error building directed graph: " + ex.getMessage() + "\n");
          }
        } else {
          textArea.append("Please choose a text file first.\n");
        }
      }
    });

    queryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // 提示用户输入要查询的词
        String word1 = JOptionPane.showInputDialog(TextToGraph.this, "Enter the first word:");
        String word2 = JOptionPane.showInputDialog(TextToGraph.this, "Enter the second word:");

        // 如果用户未输入任何内容，则不进行查询
        if (word1 == null || word1.isEmpty() || word2 == null || word2.isEmpty()) {
          textArea.append("No words entered. Bridge word query canceled.\n");
          return;
        }
        if (!textToGraph.containsKey(word1) && !textToGraph.containsKey(word2)) {
          textArea.append("No " + word1 + " or " + word2 + " in the graph!\n");
          return;
        } else if (!textToGraph.containsKey(word1)) {
          textArea.append("No " + word1 + " in the graph!\n");
          return;
        } else if (!textToGraph.containsKey(word2)) {
          textArea.append("No " + word2 + " in the graph!\n");
          return;
        }
        // 执行桥接词查询
        List<String> bridgeWords = queryBridgeWords(word1.trim().toLowerCase(),
            word2.trim().toLowerCase());
        if (bridgeWords.isEmpty()) {
          textArea.append("No bridge words found.\n");
        } else if (bridgeWords.size() == 1) {
          textArea.append(
              "The bridge words from " + word1 + " to " + word2 + " is: " + bridgeWords.get(0)
                  + "\n");
        } else {
          textArea.append("The bridge words from " + word1 + " to " + word2 + " are: ");
          for (int i = 0; i < bridgeWords.size(); i++) {
            textArea.append(bridgeWords.get(i));
            if (i < bridgeWords.size() - 2) {
              textArea.append(", ");
            } else if (i == bridgeWords.size() - 2) {
              textArea.append(", and ");
            }
          }
          textArea.append("\n");
        }
      }
    });

    insertButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // 提示用户输入文本
        String newText = JOptionPane.showInputDialog(TextToGraph.this, "Enter a new line of text:");

        // 如果用户未输入任何内容，则不进行操作
        if (newText == null || newText.isEmpty()) {
          textArea.append("No text entered. Bridge word insertion canceled.\n");
          return;
        }

        // 生成带有桥接词的新文本
        String newTextWithBridgeWords = generateNewText(newText);
        textArea.append("your text:" + newText + "\n");
        textArea.append("new text:" + newTextWithBridgeWords + "\n");
      }
    });

    shortestPathButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String word1 = JOptionPane.showInputDialog(TextToGraph.this, "Enter the first word:")
            .toLowerCase();
        String word2 = JOptionPane.showInputDialog(TextToGraph.this,
            "Enter the second word(or not):").toLowerCase();
        String dotFilePath = "./result/marked_graph_all.dot"; // 标注后的 DOT 文件路径
        String pngFilePath = "./result/marked_graph_all.png";
        if (word2.isEmpty() && textToGraph.containsKey(word1)) {
          textArea.append(shortestPathsFromSingleWord(word1));
        } else if (textToGraph.containsKey(word1) && textToGraph.containsKey(word2)) {
          Map<List<String>, Integer> shortestPath = calcShortestPath(word1, word2, textToGraph);
          if (!shortestPath.isEmpty()) {
            textArea.append(formatShortestPath(textToGraph, shortestPath, word1, word2));
          } else {
            textArea.append(formatShortestPath(textToGraph, null, word1, word2));
          }
        } else {
          textArea.append(formatShortestPath(textToGraph, null, word1, word2));
        }


      }
    });
    // 添加事件监听
    pageRankButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // 输入阻尼因子 d
        String dampingFactorStr = JOptionPane.showInputDialog(TextToGraph.this,
            "Enter damping factor (0-1, default 0.85):");
        double d = 0.85;
        try {
          d = Double.parseDouble(dampingFactorStr);
        } catch (Exception ex) {
          textArea.append("Invalid input. Using default d=0.85\n");
        }

        // 计算 PageRank
        Map<String, Double> pageRank = calPageRank(d, 100, 1e-6);

        // 显示结果
        textArea.append("\nPageRank Results (d=" + d + "):\n");
        pageRank.forEach((word, score) ->
            textArea.append(word + ": " + String.format("%.4f", score) + "\n")
        );

        // 生成可视化（可选）
        /*try {
                    generatePageRankGraph(pageRank);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }*/
      }
    });
    randomPathButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String dotRandFilePath = "./result/marked_rand_graph.dot"; // 标注后的 DOT 文件路径
        List<String> randomPath = randomTraversalForGui(textArea);
        markAndDisplayShortestPath(randomPath, dotRandFilePath);
        //textToGraph.displayImage(dotRandFilePath);
        textArea.append(formatWordList(null, randomPath) + "\n");
        textArea.append(
            "Random traversal completed. Results written to './result/random_traversal.txt'.\n");
      }
    });

    exitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Assuming the button is inside a JFrame
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(exitButton);
        if (frame != null) {
          frame.dispose();
        }
      }
      /*public void actionPerformed(ActionEvent e) {
               System.exit(0);
            }*/
    });

  }

  /**
   * 主函数.
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        TextToGraph ui = new TextToGraph();
        ui.setVisible(true);
      }
    });
  }

  public static void addNode(String node) {
    textToGraph.putIfAbsent(node, new HashMap<>());
  }

  //添加边
  public static void addEdge(String source, String destination) {
    textToGraph.get(source).merge(destination, 1, Integer::sum);
  }

  //功能一：构建有向图

  /**
   * 从文本文件构建有向图 算法步骤：
   * 1. 读取文件内容，替换非字母字符为空格
   * 2. 分割单词为数组
   * 3. 遍历相邻单词，构建边的关系
   */
  public static void buildDirectedGraph(String filePath) throws IOException {
    textToGraph.clear();
    StringBuilder content = new StringBuilder();
    // 读取文件并处理编码
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(Files.newInputStream(Paths.get(filePath)), StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        content.append(line).append(" "); // 将每行内容添加到 content 中，并添加空格作为单词间的分隔符
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 将非字母字符替换为空格，并转换为小写
    String processedContent = content.toString().replaceAll("[^a-zA-Z\\n\\r]", " ").toLowerCase();
    String[] words = processedContent.split("\\s+"); // 按空格分割单词
    for (int i = 0; i < words.length - 1; i++) {
      String currentWord = words[i];
      String nextWord = words[i + 1];
      addNode(currentWord); // 添加节点
      addNode(nextWord);
      addEdge(currentWord, nextWord); // 添加边（权重默认为1，重复边权重累加）
    }
  }

  /**
   * 将图中的顶点及其邻接边信息格式化输出到控制台. 输出格式示例：
   * <pre>
   * Vertices and edges in the graph:
   * A: (A -> B: 3), (A -> C: 5)
   * B: (B -> D: 2)
   * </pre>
   */
  public void showDirectedGraph() {
    System.out.println("Vertices and edges in the graph:");
    for (Map.Entry<String, Map<String, Integer>> entry : textToGraph.entrySet()) {
      String vertex = entry.getKey();
      Map<String, Integer> edges = entry.getValue();
      System.out.print(vertex + ": ");
      for (Map.Entry<String, Integer> edge : edges.entrySet()) {
        String destination = edge.getKey();
        int weight = edge.getValue();
        System.out.print("(" + vertex + " -> " + destination + ": " + weight + "), ");
      }
      System.out.println();
    }
  }

  /**
   * 生成有向图的DOT文件表示，用于Graphviz可视化.
   *
   * @param dotFilePath 输出的DOT文件保存路径（需包含文件名，格式：*.dot）
   */
  @SuppressFBWarnings("PATH_TRAVERSAL_OUT")
  public void generateDotFile(String dotFilePath) {
    try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(dotFilePath),
        StandardCharsets.UTF_8);
        BufferedWriter writer = new BufferedWriter(osw)) {
      writer.write("digraph G {\n");
      for (Map.Entry<String, Map<String, Integer>> entry : textToGraph.entrySet()) {
        String vertex = entry.getKey();
        Map<String, Integer> edges = entry.getValue();
        for (Map.Entry<String, Integer> edge : edges.entrySet()) {
          String destination = edge.getKey();
          int weight = edge.getValue();
          writer.write("\t" + vertex + " -> " + destination + " [label=\"" + weight + "\"];\n");
        }
      }
      writer.write("}");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 将DOT文件转换为指定尺寸和分辨率的PNG图像文件.
   *
   * @param dotFilePath   输入的DOT文件路径（需包含文件名）
   * @param imageFilePath 输出的PNG图像文件路径（需包含文件名）
   */

  public void convertDotToImage(String dotFilePath, String imageFilePath) {
    try {

      ProcessBuilder processBuilder = new ProcessBuilder(
          "dot",
          "-Tpng",
          "-Gsize=9,9",       // 调整尺寸为 9x9 英寸
          "-Gdpi=100",          // 分辨率设为 100 DPI
          dotFilePath,
          "-o",
          imageFilePath);
      Process process = processBuilder.start();
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        System.out.println("Image file generated: " + imageFilePath);
      } else {
        System.out.println("Failed to generate image file.");
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * 在DOT文件中为多条最短路径的边标注不同颜色，生成可视化标记文件.
   *
   * @param dotFilePath 输出DOT文件的保存路径（需包含文件名）
   * @param shortPaths  需标记的最短路径集合（键为路径节点列表，值为路径长度）
   */

  @SuppressFBWarnings("PATH_TRAVERSAL_OUT")
  public void convertDotFile(String dotFilePath, Map<List<String>, Integer> shortPaths) {
    try (BufferedWriter bw = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(dotFilePath), StandardCharsets.UTF_8))) {
      String[] colors = {"red", "blue", "green", "orange", "purple", "yellow", "brown", "cyan"};

      // 为每个最短路径选择颜色
      Map<List<String>, String> pathColors = new HashMap<>();
      for (Map.Entry<List<String>, Integer> entry : shortPaths.entrySet()) {
        List<String> path = entry.getKey();
        String color = colors[pathColors.size() % colors.length];
        pathColors.put(path, color);
      }
      try (BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream("./result/directed_graph.dot"),
              StandardCharsets.UTF_8))) {
        String line;
        while ((line = br.readLine()) != null) {
          if (line.contains("->")) {
            String[] parts = line.split("->");
            String fromNode = parts[0].trim();
            String toNode = parts[1].trim().split("\\[")[0].trim();
            for (Map.Entry<List<String>, Integer> entry : shortPaths.entrySet()) {
              // 如果起始节点和终止节点在最短路径集合中，则修改颜色
              List<String> path = entry.getKey();
              if (path.contains(fromNode) && path.contains(toNode)
                  && !fromNode.equals(path.get(path.size() - 1))
                  && (path.indexOf(toNode) - path.indexOf(fromNode) == 1)) {
                if (parts[1].trim().endsWith(";")) {
                  // 如果是，删除最后一个字符';'
                  parts[1] = parts[1].trim().substring(0, parts[1].trim().length() - 1);
                }
                String color = pathColors.get(path);
                line = "\t" + parts[0].trim() + " -> " + parts[1] + " [color=" + color + "];";
                break; // 只需要为同一路径内的边选择一种颜色
              }
            }
          }
          bw.write(line);
          bw.newLine();
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  //功能三：桥接词查询

  /**
   * 查找两个单词之间的桥接词 算法逻辑： 桥接词定义：若存在路径 word1 -> bridge -> word2，则bridge为桥接词 实现步骤：
   * 1. 获取word1的所有邻接节点（直接后继）
   * 2. 检查这些邻接节点是否指向word2
   */
  public static List<String> queryBridgeWords(String start, String end) {
    List<String> bridgeWords = new ArrayList<>();
    if (!textToGraph.containsKey(start) && !textToGraph.containsKey(end)) {
      return bridgeWords;
    }
    if (!textToGraph.containsKey(start)) {
      return bridgeWords;
    }
    if (!textToGraph.containsKey(end)) {
      return bridgeWords;
    }
    Map<String, Integer> edges1 = textToGraph.get(start);
    List<String> neighbors1 = new ArrayList<>(edges1.keySet());
    for (String neighbor1 : neighbors1) { //遍历word1所有节点
      Map<String, Integer> edges2 = textToGraph.get(neighbor1);
      List<String> neighbors2 = new ArrayList<>(edges2.keySet());
      // 检查邻接节点是否连接word2
      if (neighbors2.contains(end)) {
        bridgeWords.add(neighbor1);
      }
    }
    return bridgeWords;
  }

  /**
   * 在输入文本的相邻单词间插入桥接词生成扩展后的新文本.
   *
   * @param text 原始文本（以空格分隔的单词序列）
   * @return 处理后的新文本：若相邻单词对存在桥接词，则随机插入一个；否则保持原顺序
   */
  public String generateNewText(String text) {
    StringBuilder result = new StringBuilder();
    String[] words = text.split("\\s+"); // 按空格分割单词
    for (int i = 0; i < words.length - 1; i++) {
      String currentWord = words[i];
      String nextWord = words[i + 1];
      result.append(currentWord).append(" ");
      if (textToGraph.containsKey(currentWord) && textToGraph.containsKey(nextWord)) {
        List<String> bridgeWords = queryBridgeWords(currentWord, nextWord);
        if (!bridgeWords.isEmpty()) {
          // 如果存在桥接词，则随机选择一个桥接词插入
          Random random = new SecureRandom();
          String selectedBridgeWord = new ArrayList<>(bridgeWords).get(
              random.nextInt(bridgeWords.size()));
          result.append(selectedBridgeWord).append(" ");
        }
      }
    }
    result.append(words[words.length - 1]); // 添加最后一个单词
    return result.toString();
  }

  //功能5：最短路径

  /**
   * 计算两点间所有最短路径（BFS实现） 算法步骤：
   * 1. 使用队列进行广度优先搜索
   * 2. 记录所有路径及其总权重
   * 3. 筛选权重最小的路径
   */
  public static Map<List<String>, Integer> calcShortestPath(String start, String end,
      Map<String, Map<String, Integer>> textToGraph) {
    Map<List<String>, Integer> allPaths = findAll(start, end, textToGraph);
    // 找到最短路径的长度
    int shortestLength = Integer.MAX_VALUE;
    for (int length : allPaths.values()) {
      if (length < shortestLength) {
        shortestLength = length;
      }
    }

    // 筛选出所有最短路径
    Map<List<String>, Integer> shortestPaths = new HashMap<>();
    for (Map.Entry<List<String>, Integer> entry : allPaths.entrySet()) {
      List<String> path = entry.getKey();
      int length = entry.getValue();
      if (length == shortestLength) {
        shortestPaths.put(path, length);
      }
    }

    return shortestPaths;
  }

  /**
   * 查找图中两个节点之间的所有可行路径及其对应权重.
   *
   * @param start       起始节点名称
   * @param end         目标节点名称
   * @param textToGraph 图的邻接表结构（节点与其邻接节点的映射关系）
   * @return 包含所有路径的映射集合：键为路径节点列表（顺序为从start到end），值为路径总权重
   */

  public static Map<List<String>, Integer> findAll(String start, String end,
      Map<String, Map<String, Integer>> textToGraph) {
    Map<List<String>, Integer> allPathsWithLength = new HashMap<>(); // 存储结果
    Queue<List<String>> queue = new LinkedList<>();                 // BFS队列
    Set<String> visited = new HashSet<>();                           // 已访问节点
    List<String> initialPath = new ArrayList<>();                    // 初始路径
    initialPath.add(start);
    queue.add(initialPath);
    int weight = 0;
    //allPathsWithLength.put(new ArrayList<>(), weight);
    while (!queue.isEmpty()) {

      List<String> currentPath = queue.poll(); // 取出当前路径
      String current = currentPath.get(currentPath.size() - 1); // 当前路径的最后一个节点

      if (current.equals(end)) { // 到达终点
        weight = calcWeight(currentPath, textToGraph); // 计算路径权重
        allPathsWithLength.put(new ArrayList<>(currentPath), weight);
        continue;
      }

      Map<String, Integer> neighbors = textToGraph.getOrDefault(current, Collections.emptyMap());
      for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
        String next = neighbor.getKey();
        if (!visited.contains(next)) { // 如果邻居未被访问过
          List<String> newPath = new ArrayList<>(currentPath);
          newPath.add(next); // 扩展新路径
          queue.add(newPath);
        }
      }
      visited.add(current); // 标记当前节点为已访问

    }

    return allPathsWithLength;
  }

  private static int calcWeight(List<String> path, Map<String, Map<String, Integer>> textToGraph) {
    int weight = 0;
    for (int i = 0; i < path.size() - 1; i++) {
      String currentNode = path.get(i);
      String nextNode = path.get(i + 1);
      //从 textToGraph 中获取 currentNode 的所有出边。
      // 如果 currentNode 不存在于图中，返回空Map（Collections.emptyMap()）。
      Map<String, Integer> neighbors = textToGraph.getOrDefault(currentNode,
          Collections.emptyMap());
      weight += neighbors.getOrDefault(nextNode, 0);
    }
    return weight;
  }

  /**
   * 在DOT文件中将最短路径标记为红色边，并生成可视化PNG图像.
   *
   * @param shortestPath      最短路径的节点序列（需非空且有效）
   * @param markedDotFilePath 标注后的DOT文件输出路径（需包含文件名）
   * @return 成功时返回标注DOT文件路径，失败（如图像生成异常）返回null
   */

  public static String markAndDisplayShortestPath(List<String> shortestPath,
      String markedDotFilePath) {
    try {
      // 写入标注后的 DOT 文件
      try (BufferedWriter bw = new BufferedWriter(
          new OutputStreamWriter(new FileOutputStream(markedDotFilePath),
              StandardCharsets.UTF_8))) {
        bw.write("digraph G {\n");
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream("./result/directed_graph.dot"),
                StandardCharsets.UTF_8))) {
          String line;
          while ((line = br.readLine()) != null) {
            if (line.contains("->")) {
              String[] parts = line.split("->");
              String fromNode = parts[0].trim();
              String toNode = parts[1].trim().split("\\[")[0].trim();
              // 如果当前边是最短路径上的一部分，则修改箭头颜色为红色
              if (shortestPath.contains(fromNode) && shortestPath.contains(toNode)) {
                if (parts[1].trim().endsWith(";")) {
                  // 如果是，删除最后一个字符';'
                  parts[1] = parts[1].trim().substring(0, parts[1].trim().length() - 1);
                }
                bw.write("\t" + parts[0].trim() + " -> " + parts[1] + " [color=red];");
                bw.newLine();
              } else {
                bw.write(line);
                bw.newLine();
              }
            }
          }
          bw.write("}"); // 结束有向图
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      // 调用 Graphviz 生成 PNG 文件
      String pngName = markedDotFilePath.substring(0, markedDotFilePath.lastIndexOf(".")) + ".png";
      ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", markedDotFilePath, "-o",
          pngName);
      Process process = processBuilder.start();
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        System.out.println("Image file generated: " + pngName);
        // 在屏幕上显示图像
        displayImage(pngName);
      } else {
        System.out.println("Failed to generate image file.");
      }

      return markedDotFilePath; // 返回标注后的 DOT 文件路径
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 生成从指定单个单词到图中所有其他节点的最短路径格式化结果.
   *
   * @param word 起始节点名称（需存在于图中）
   * @return 格式化字符串，每行对应一个目标节点：
   */

  public static String shortestPathsFromSingleWord(String word) {
    StringBuilder sb = new StringBuilder();
    for (String node : textToGraph.keySet()) {
      if (!node.equals(word)) {
        Map<List<String>, Integer> shortestPath = calcShortestPath(word, node, textToGraph);
        if (shortestPath.isEmpty()) {
          sb.append("no path from " + word + " to " + node + ".\n");
        } else {
          sb.append(
              "Shortest path from " + word + " to " + node + ": " + formatWordList(shortestPath,
                  null) + "\n");
        }
      }
    }
    return sb.toString();
  }

  /**
   * 格式化图中两个节点间的最短路径信息，生成可显示的字符串.
   *
   * @param textToGraph 图的邻接表结构（节点与其邻接节点的映射关系）
   * @param wordList    存储所有最短路径的集合（键为路径节点列表，值为路径长度），若为null表示未找到路径
   * @param word1       起始节点名称
   * @param word2       目标节点名称
   * @return 格式化后的字符串
   */

  public static String formatShortestPath(
      HashMap<String, Map<String, Integer>> textToGraph, Map<List<String>, Integer> wordList,
      String word1, String word2) {
    StringBuilder formattedString = new StringBuilder();
    if (wordList != null) {
      for (Map.Entry<List<String>, Integer> entry : wordList.entrySet()) {
        List<String> node = entry.getKey();
        for (int i = 0; i < node.size(); i++) {
          formattedString.append(node.get(i));
          if (i < node.size() - 1) {
            formattedString.append("→");
          }

        }
        //formattedString.append("\n");
        formattedString.append("\nthe shortest path:" + entry.getValue() + ";" + "\n");
      }
    } else {
      if (!textToGraph.containsKey(word1) || !textToGraph.containsKey(word2)) {
        formattedString.append(word1 + " or " + word2 + " not in the graph!\n");
      } else {
        formattedString.append("No path found between " + word1 + " and " + word2 + "\n");
      }
    }

    return formattedString.toString();
  }

  /**
   * 将路径列表或随机遍历结果格式化为可显示的字符串.
   *
   * @param wordList 路径列表（键为路径节点列表，值为路径长度），若为null则使用rand参数
   * @return 格式化后的字符串（路径节点用→连接，不同路径/节点用换行符分隔）
   */

  public static String formatWordList(Map<List<String>, Integer> wordList, List<String> rand) {
    StringBuilder formattedString = new StringBuilder();
    if (wordList != null) {
      for (Map.Entry<List<String>, Integer> entry : wordList.entrySet()) {
        List<String> node = entry.getKey();
        for (int i = 0; i < node.size(); i++) {
          formattedString.append(node.get(i));
          if (i < node.size() - 1) {
            formattedString.append("→");
          }

        }
        formattedString.append("\n");
        formattedString.append("the shortest path:" + entry.getValue() + "\n");
      }
    } else {
      for (int i = 0; i < rand.size(); i++) {
        formattedString.append(rand.get(i));
        formattedString.append("\n");
      }
    }

    return formattedString.toString();
  }

  /**
   * 在屏幕上显示指定路径的PNG图像窗口.
   *
   * @param imagePath 要显示的PNG图像文件路径（不可为null或空，且需指向有效文件）
   */

  public static void displayImage(String imagePath) {

    // 创建标签，用于显示图像
    JLabel label = new JLabel();
    // 设置标签的对齐方式为居中
    label.setHorizontalAlignment(JLabel.CENTER);
    label.setVerticalAlignment(JLabel.CENTER);

    // 读取 PNG 图像文件并更新标签的图标
    updateImageIcon(label, imagePath);

    // 创建窗口
    JFrame frame = new JFrame();
    frame.setTitle("PNG Image Viewer");
    frame.setSize(2000, 2000);
    // 设置窗口关闭时不退出程序
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    // 使用BorderLayout布局管理器，并将标签添加到中心位置
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(label, BorderLayout.CENTER);

    // 调整窗口大小以适应图像大小
    frame.pack();

    // 设置窗口可见
    frame.setVisible(true);

    // 使窗口在屏幕中间显示
    frame.setLocationRelativeTo(null);

    // 添加窗口关闭监听器，以便在窗口关闭时释放资源
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        // 可以在这里添加释放资源的代码
        // 释放图像资源
        Image image = label.getIcon() != null ? ((ImageIcon) label.getIcon()).getImage() : null;
        if (image != null) {
          image.flush();
        }

        // 可以选择在这里设置标签的图标为null，以帮助垃圾回收器回收对象
        label.setIcon(null);
      }
    });

    // 强制刷新窗口
    frame.revalidate();
    frame.repaint();
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("PATH_TRAVERSAL_IN")
  private static void updateImageIcon(JLabel label, String imagePath) {
    // 检查文件是否存在，如果不存在则返回
    File imageFile = new File(imagePath);
    if (!imageFile.exists()) {
      System.err.println("Image file does not exist: " + imagePath);
      return;
    }

    // 创建新的 ImageIcon 对象并设置到标签上
    ImageIcon icon = new ImageIcon(imagePath);
    label.setIcon(icon);
  }
  //功能6：pagerank

  /**
   * PageRank计算（迭代法） 公式：PR(A) = (1-d)/N + d * Σ(PR(Ti)/C(Ti)) 其中：
   * - d: 阻尼因子（通常0.85）
   * - N: 总节点数
   * - Ti: 指向A的所有节点
   * - C(Ti): Ti的出度 实现步骤：
   * 1. 初始化所有节点PR值为1/N
   * 2. 迭代计算直至收敛（变化量小于阈值）
   * 3. 处理悬挂节点（出链为0的节点）
   */
  public static Map<String, Double> calPageRank(double d, int maxIterations, double tolerance) {
    Map<String, Double> pr = new HashMap<>();
    Map<String, Double> tempPr = new HashMap<>();
    int n = textToGraph.size();

    // 初始化 PR 值（均匀分布）
    double initialValue = 1.0 / n;
    textToGraph.keySet().forEach(node -> pr.put(node, initialValue));

    // 处理出度为 0 的节点集合
    Set<String> zeroOutDegreeNodes = new HashSet<>();
    textToGraph.forEach((node, edges) -> {
      if (edges.isEmpty()) {
        zeroOutDegreeNodes.add(node);
      }
    });

    for (int iter = 0; iter < maxIterations; iter++) {
      double danglingSum = zeroOutDegreeNodes.stream()
          .mapToDouble(node -> pr.get(node))
          .sum();
      double distribute = danglingSum / n;  // 正确：均分后再乘 d

      textToGraph.keySet().forEach(node -> {
        // 常规 PR 贡献
        double sum = textToGraph.entrySet().stream()
            .filter(entry -> entry.getValue().containsKey(node)) // 所有指向当前节点的节点 v
            .mapToDouble(vertexEntry -> {
              String v = vertexEntry.getKey();
              int outDegree = vertexEntry.getValue().size();
              return pr.get(v) / outDegree;
            })
            .sum();

        // 更新
        double newPr = (1 - d) / n + d * (sum + distribute);
        tempPr.put(node, newPr);
      });

      // 检查收敛
      double diff = textToGraph.keySet().stream()
          .mapToDouble(node -> Math.abs(tempPr.get(node) - pr.get(node)))
          .sum();
      pr.putAll(tempPr);
      if (diff < tolerance) {
        break;
      }
    }
    return pr;
  }

  private void generatePageRankGraph(Map<String, Double> pageRank) throws IOException {
    // 安全获取最大值
    double maxPr = pageRank.values().stream()
        .max(Double::compare)
        .orElse(1.0); // 处理空值情况

    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(
            Files.newOutputStream(Paths.get("./result/pagerank_graph.dot")),
            StandardCharsets.UTF_8  //  强制指定 UTF-8 编码
        )
    )) {
      writer.write("digraph G {\n");
      writer.write("  node [shape=circle, style=filled];\n");

      textToGraph.forEach((node, edges) -> {
        // 防御性空值检查
        double prValue = pageRank.getOrDefault(node, 0.0);

        // 修正颜色格式
        String color = String.format("#%02x%02x%02x",
            (int) (200 * (1 - prValue / maxPr)),
            (int) (200 * (prValue / maxPr)),
            200
        );

        // 节点尺寸计算
        double scale = 0.5 + (prValue / maxPr) * 1.5;

        try {
          writer.write(String.format(
              "  \"%s\" [width=%.2f, height=%.2f, fillcolor=\"%s\"];%n",
              node, scale, scale, color
          ));

          edges.forEach((dest, weight) -> {
            try {
              writer.write("  \"" + node + "\" -> \"" + dest + "\";\n");
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
      writer.write("}");
    }

    // 生成图片
    convertDotToImage("./result/pagerank_graph.dot", "./result/pagerank_graph.png");
    displayImage("./result/pagerank_graph.png");
  }
  //功能7：游走路径

  /**
   * 随机游走实现（带GUI交互） 逻辑流程：
   * 1. 随机选择起始节点
   * 2. 每一步随机选择邻接节点
   * 3. 检测重复边或用户停止信号
   * 4. 实时更新UI显示路径
   */
  public static String randomWalk() {
    StringBuilder resultBuilder = new StringBuilder();
    Random random = new SecureRandom();
    List<String> nodesVisited = new ArrayList<>(); // 记录经过的节点
    Set<String> edgesVisited = new HashSet<>(); // 记录经过的边

    try {
      // 随机选择起始节点
      List<String> nodes = new ArrayList<>(textToGraph.keySet());
      if (nodes.isEmpty()) {
        return "Error: Empty graph";
      }
      String currentNode = nodes.get(random.nextInt(nodes.size()));
      nodesVisited.add(currentNode);

      // 开始随机遍历
      while (true) {
        // 检查停止条件
        if (shouldStopTraversal()) {
          resultBuilder.append("\nUser requested stop");
          break;
        }

        Map<String, Integer> edges = textToGraph.get(currentNode);
        if (edges == null || edges.isEmpty()) {
          resultBuilder.append("\nNo outgoing edges from: ").append(currentNode);
          break;
        }

        // 随机选择下一个节点
        List<String> nextNodes = new ArrayList<>(edges.keySet());
        String nextNode = nextNodes.get(random.nextInt(nextNodes.size()));
        String edge = currentNode + "->" + nextNode;

        if (edgesVisited.contains(edge)) {
          resultBuilder.append("\nRepeated edge: ").append(edge);
          break;
        }

        edgesVisited.add(edge);
        nodesVisited.add(nextNode);
        currentNode = nextNode;
      }

      // 构建结果字符串
      resultBuilder.append("Traversal path:\n");
      for (int i = 0; i < nodesVisited.size(); i++) {
        resultBuilder.append(nodesVisited.get(i));
        if (i < nodesVisited.size() - 1) {
          resultBuilder.append(" → ");
        }
      }

    } catch (Exception e) {
      return "Error during traversal: " + e.getMessage();
    }

    return resultBuilder.toString();
  }

  // 检查用户是否希望停止随机游走
  private static boolean shouldStopTraversal() {
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
    System.out.print("Press 'q' to stop random traversal, or press any other key to continue: ");
    String input = scanner.nextLine().trim().toLowerCase();
    return input.equals("q");
  }

  /**
   * 执行图的随机遍历，并通过GUI交互逐步显示遍历过程，结果写入文件.
   *
   * @param textArea 用于显示遍历过程中实时信息的文本区域
   * @return 按访问顺序排列的节点列表
   */

  public static List<String> randomTraversalForGui(JTextArea textArea) {
    List<String> nodesVisited = new ArrayList<>(); // 记录经过的节点
    try {
      // 创建文件写入器
      PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream("./result/random_traversal.txt"), StandardCharsets.UTF_8)));
      Random random = new SecureRandom();

      List<String> nodes = new ArrayList<>(textToGraph.keySet());

      // first node 随机选择起始节点
      final String[] currentNode = {
          nodes.get(random.nextInt(nodes.size()))}; // 声明为 final 的数组(final String不行！
      nodesVisited.add(currentNode[0]);

      // begin
      while (true) {
        // 在控制台输出当前顶点
        System.out.println("Current node: " + currentNode[0]);

        // 在 UI 中显示当前顶点
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            textArea.append("Current node: " + currentNode[0] + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength()); // 将文本区域滚动到最后一行
          }
        });
        // continue?
        int option = JOptionPane.showConfirmDialog(null, "Continue traversal?", "Continue",
            JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
          textArea.append("quit!\n");
          textArea.setCaretPosition(textArea.getDocument().getLength());
          break;
        }

        // 获取当前节点的出边
        Map<String, Integer> edges = textToGraph.get(currentNode[0]);
        if (edges == null || edges.isEmpty()) {
          textArea.append("no edge!\n");
          textArea.setCaretPosition(textArea.getDocument().getLength());
          break; // 当前节点没有出边，遍历结束
        }

        // 随机选择下一个节点
        List<String> nextNodes = new ArrayList<>(edges.keySet());
        String nextNode = nextNodes.get(random.nextInt(nextNodes.size()));

        // 出现重复的顶点，遍历结束
        if (nodesVisited.contains(nextNode)) {
          // 添加重复节点
          nodesVisited.add(nextNode);
          textArea.append("Current node: " + nextNode + "\n");
          textArea.setCaretPosition(textArea.getDocument().getLength());
          textArea.append("node repeat!\n");
          textArea.setCaretPosition(textArea.getDocument().getLength());
          break;
        }

        // 记录经过的节点
        nodesVisited.add(nextNode);

        // 更新当前节点
        currentNode[0] = nextNode;


      }

      // 将遍历的节点写入文件
      for (String node : nodesVisited) {
        writer.print(node + " ");
      }
      writer.println();
      // 关闭文件写入器
      writer.close();

      // 提示用户遍历已完成并文件已生成
    } catch (IOException e) {
      e.printStackTrace();
    }
    return nodesVisited;
  }


}
