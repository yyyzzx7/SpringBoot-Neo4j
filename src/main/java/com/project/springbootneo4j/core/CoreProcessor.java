package com.project.springbootneo4j.core;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.project.springbootneo4j.model.Province;
import com.project.springbootneo4j.model.Station;
import com.project.springbootneo4j.model.TrainNo;
import com.project.springbootneo4j.model.TrainType;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;


import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreProcessor {

    public CoreProcessor() {

    }


    public String getCypher(String userQuestion) {

        System.out.println("\n原始句子：" + userQuestion);
        System.out.println("========开始处理========");

        // STEP1 利用HanLP分词，返回分词结果
        List<Term> segQuestion = segQuerySentence(userQuestion);
        System.out.println("问题分词结果:" + segQuestion);

        // STEP2 实体识别
        List<Term> entities = getSentenceEntity(segQuestion);
        System.out.println("实体识别结果:" + entities);

        // STEP3 实体链接（通过别名词典）
        List<Term> entities_neo4j = getNeo4jEntity(entities);
        System.out.println("实体链接结果:" + entities_neo4j);

        // STEP4 链接的实体替换问题模板，还原成问题，得到标准问题List
        int questionType = questionClassify(userQuestion);
        System.out.println("问题分类结果:" + questionType);

        // STEP5 根据问题分类结果查找对应Cypher模板
        String cypherTemplate = getCypherTemplate(questionType);
        System.out.println("查询语句模板:" + cypherTemplate);

        // STEP6 构建完整查询语句
        String cypher = getCypher(entities_neo4j, cypherTemplate);
        System.out.println("查询语句:" + cypher);


        return cypher;
    }

    /**
     * 对用户问题进行分词
     *
     * @param userQuestion 用户问题
     * @return 分词结果
     */
    public List<Term> segQuerySentence(String userQuestion) {
        // HanLP分词对象，加载用户词典
        Segment segment = HanLP.newSegment().enableCustomDictionaryForcing(true);
        // 分词后的短语list
        return segment.seg(userQuestion);
    }

    /**
     * 识别分词结果中的实体
     *
     * @param seg 分词结果List
     * @return 实体List
     */
    public List<Term> getSentenceEntity(List<Term> seg) {
        List<Term> entities = new ArrayList<>();
        List<String> natures = new ArrayList<>();
        natures.add(Station.className);
        natures.add(Province.className);
        natures.add(TrainNo.className);
        natures.add(TrainType.className);

        for (Term term : seg) {
            String natureString = term.nature.toString();
            // 当前分词的词性是否为实体词性
            for (String nature : natures) {
                if (natureString.equals(nature)) {
                    entities.add(term);
                    break;
                }
            }
        }
        return entities;
    }

    /**
     * 链接到Neo4j数据库中的实体（从别名词典中）
     *
     * @param terms 用户问题中的实体
     * @return 链接实体
     */
    public List<Term> getNeo4jEntity(List<Term> terms) {
        List<Term> entities = new ArrayList<>();

        for (Term term : terms) {
            Term result = new Term("", term.nature);
            // TODO Auto-generated method stub
            Process proc;
            try {
                String[] args1 = new String[]{"E:\\project\\venv\\Scripts\\python.exe", "E:\\project\\QA\\search_neo4j_entity.py", term.nature.toString(), term.word};
                proc = Runtime.getRuntime().exec(args1);
                //用输入输出流来截取结果
                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(), "gbk"));
                /*String line = null;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }*/
                result.word = in.readLine();
                in.close();
                proc.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            entities.add(result);
        }

        return entities;
    }

    /**
     * 问题分类
     *
     * @param userQuestion 用户问题
     * @return 问题类别
     */
    public int questionClassify(String userQuestion) {
        int type = 0;
        Process proc;
        try {
            String[] args1 = new String[]{"E:\\project\\venv\\Scripts\\python.exe", "E:\\project\\QA\\user_question_classifier.py", userQuestion};
            proc = Runtime.getRuntime().exec(args1);
            //用输入输出流来截取结果
            InputStreamReader inputStreamReader = new InputStreamReader(proc.getInputStream(), "gbk");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            type = Integer.parseInt(bufferedReader.readLine());
            bufferedReader.close();

            //java代码中的process.waitFor()返回值为0表示调用python脚本成功，
            //返回值为1表示调用python脚本失败，这和通常意义上见到的0与1定义正好是相反的
            int response = proc.waitFor();
//            System.out.println(response);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return type;

    }

    /**
     * 根据问题类型查找Cypher模板
     *
     * @param questionType 问题类型
     * @return Cypher模板
     */
    public String getCypherTemplate(int questionType) {
        String cypherTemplate = "";
        try {
            // 创建tsv解析器settings配置对象
            TsvParserSettings settings = new TsvParserSettings();
            settings.getFormat().setLineSeparator("\n"); // 文件中使用 '\n' 作为行分隔符
            // 创建TSV解析器（将分隔符传入对象）
            TsvParser parser = new TsvParser(settings);
            // 对tsv文件,调用parseAll
            List<String[]> allRows = parser.parseAll(new FileInputStream("E:\\project\\QA\\cypher\\cypher_template.tsv"));
            for (String[] allRow : allRows) {
                if (Integer.parseInt(allRow[0]) == questionType) {
                    cypherTemplate = allRow[1];
                    break;
                }
                // System.out.println(Arrays.asList(allRows.get(i)));
                // System.out.println(allRows.get(i)[0].trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cypherTemplate;
    }

    /**
     * 将链接的实体填入cypher模板
     * 使用正则表达式匹配
     *
     * @param entities_neo4j 实体List
     * @return cypher查询语句
     */
    public String getCypher(List<Term> entities_neo4j, String cypherTemplate) {
        String cypher = cypherTemplate;
        for (Term entity : entities_neo4j) {
            System.out.println(entity);
            String entity_label = entity.nature.toString(); // 词性即标签名
            String regex = String.format("\\{(%s)}", entity_label); //正则表达式匹配{}
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(cypher);

            if (Objects.equals(entity_label, "train_type")) {
                switch (entity.word) {
                    case "高铁":
                        cypher = matcher.replaceFirst("G\\\\\\\\d{1,4}");
                        break;
                    case "动车":
                        cypher = matcher.replaceFirst("[C,D]\\\\\\\\d{1,4}");
                        break;
                    case "特快":
                        cypher = matcher.replaceFirst("T\\\\\\\\d{1,4}");
                        break;
                    case "普快":
                        cypher = matcher.replaceFirst("\\\\\\\\d{1,4}");
                        break;
                    case "快速":
                        cypher = matcher.replaceFirst("[K,S,Y]\\\\\\\\d{1,4}");
                        break;
                    case "直达":
                    case "直特":
                    case "直达特快":
                        cypher = matcher.replaceFirst("Z\\\\\\\\d{1,4}");
                        break;
                }
            } else {
                cypher = matcher.replaceFirst(entity.word);
            }
        }

        // 没有车次类型的约束则将train_type的正则表达式替换为 .*
        cypher = cypher.replaceFirst(String.format("\\{(%s)}", "train_type"), ".*");

        return cypher;
    }


    public static void main(String[] args) throws Exception {
//        new CoreProcessor().analysis("天府之国有哪些车站？"); // 1
//        new CoreProcessor().analysis("路过重庆北的高铁有哪些？");// 2
//        new CoreProcessor().getCypher("从保定到宝鸡有哪些特快？"); // 3
//        new CoreProcessor().analysis("起点是成都站终点是郑州的车的有哪些？"); // 3
//        new CoreProcessor().analysis("从济南站出发可以乘坐哪些高铁？"); // 4
//        new CoreProcessor().analysis("有哪些车次最终到达深圳北？"); // 5
        new CoreProcessor().getCypher("从成都出发到西安有几点发车的高铁？"); // 11
//        new CoreProcessor().analysis("从北京到成都有几点到达的车？"); // 12
//        new CoreProcessor().analysis("Z202都从哪些站经过？"); // 18
//        new CoreProcessor().analysis("G2422终点站？"); // 19
//        new CoreProcessor().analysis("G4起始站点是哪个地方？"); // 20


//        String cypher = "MATCH (s1:Station)<-[:`途径`]-(id1:TrainNode)<-[:`站点信息`]-(no:TrainNo)-[:`站点信息`]->(id2:TrainNode)-[:`途径`]->(s2:Station) WHERE s1.name='成都站' AND s2.name='西安站' AND no.name=~'{train_type}' WITH id1,no,id2 MATCH (id2)-[:`到达时间`]->(t:TrainInfo),(id1)-[:`站点顺序`]->(seq1:TrainInfo),(id2)-[:`站点顺序`]->(seq2:TrainInfo) WHERE toInt(seq1.name) < toInt(seq2.name) RETURN t.name AS time";
//        cypher = cypher.replaceFirst(String.format("\\{(%s)}", "train_type"), ".*");
//        System.out.println(cypher);


    }

}
