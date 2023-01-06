package cn.edu.bit.cs.anomaly.evaluate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSetConcat {

  static String file1 = "F:\\Antinomies\\Desktop\\univary\\twitter stl.csv",
                file2 = "F:\\Antinomies\\Desktop\\univary\\twitter anoms.csv",
                file3 = "F:\\Antinomies\\Desktop\\univary\\out.csv";
  public static void main(String [] args) throws IOException {
    ArrayList<Integer> anoms = new ArrayList<>();
    ArrayList<List<String>> values = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file2));//换成你的文件名
      reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
      String line = null;
      while((line=reader.readLine())!=null){
        String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
        anoms.add(Integer.parseInt(item[1]));
      }
      reader = new BufferedReader(new FileReader(file1));//换成你的文件名
      reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
      line = null;
      while((line=reader.readLine())!=null){
        List<String> item = new ArrayList<>(Arrays.<String>asList(line.split(",")));
        if(anoms.contains(Integer.parseInt(item.get(0)))){
          item.add("1");
        }else{
          item.add("0");
        }
        values.add(item);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    PrintWriter pw = new PrintWriter(new FileWriter(file3));
    for(List<String> l :values){
      pw.println(String.join(",",l));
    }
    pw.close();
  }
}
