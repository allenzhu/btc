import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;


/**
 * @auther lansheng.zj
 */
public class CreateDiagram {

    public static void main(String[] args) throws Exception {
        print();
    }


    public static void print() throws Exception {
        String filename = "D:/self/项目/比特币对冲/图表/t.html";
        PrintWriter pw = new PrintWriter(new FileWriter(filename));
        pw.println("<!DOCTYPE HTML>");
        pw.println("<html>");
        pw.println("    <head>");
        pw.println("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        pw.println("        <title>Demo</title>");
        pw.println("        <script type=\"text/javascript\" src=\"./jquery-1.8.2.min.js\"></script>");
        pw.println("    </head>");
        pw.println("    <body>");
        pw.println("        <script src=\"./js/highstock.js\"></script>");
        pw.println("        <script src=\"./js/modules/exporting.js\"></script>");
        pw.println("        <div id=\"container\" style=\"height: 400px; min-width: 310px\"></div>");
        pw.println("        <script type=\"text/javascript\">");
        pw.println("            function show(data){");
        pw.println("                $('#container').highcharts('StockChart', {");
        pw.println("                    rangeSelector : {");
        pw.println("                    selected : 1");
        pw.println("                    },");
        pw.println("                    title : {");
        pw.println("                    text : 'diff price'");
        pw.println("                    },");
        pw.println("                    series : [{");
        pw.println("                    name : 'diff',");
        pw.println("                    data : data,");
        pw.println("                    tooltip: {");
        pw.println("                        valueDecimals: 2");
        pw.println("                    }");
        pw.println("                    }]");
        pw.println("                });");
        pw.println("            }");
        pw.println("            var data=[" + createData() + "];");
        pw.println("            show(data);");
        pw.println("        </script>");
        pw.println("    </body>");
        pw.println("</html>");
        pw.flush();
        pw.close();
    }


    public static String createData() throws Exception {
        StringBuilder sb = new StringBuilder();
        String filename = "D:/self/项目/比特币对冲/图表/newformat.log";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = null;
        boolean first = true;
        while (null != (line = br.readLine())) {
            if (first) {
                first = false;
            }
            else {
                sb.append(",");
            }
            sb.append(line.trim());
        }
        br.close();
        return sb.toString();
    }
}
