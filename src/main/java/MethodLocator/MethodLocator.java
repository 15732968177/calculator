package MethodLocator;

import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * 功能：
 * 我现在有一个java项目，我遍历了所有文件生成了多个AST，我想要寻找一个方法所在的位置（包括在哪个文件，哪一行）
 *
 * 过程：
 * 1. 生成AST
 * 你已经完成了对Java项目中所有文件的AST生成，这意味着你现在可以通过AST数据来查找方法。
 * 2. 遍历AST寻找方法
 * 在Java的AST中，每个方法通常会被表示为一个方法声明节点（如 MethodDeclaration 类型的节点），这个节点包含了方法的名字、返回类型、参数、修饰符等信息。
 * 对每个AST进行遍历，找到所有的 MethodDeclaration 节点，并检查它们的方法名称是否与目标方法匹配。
 * 3. 获取方法的位置
 * 每个方法的AST节点通常包含位置信息（如起始行号和结束行号）。这个位置通常是源代码文件中方法的起始和结束行数。
 *
 * 结果：
 * 你可以通过访问这些位置属性，获取方法所在的文件和具体的行号。
 * 在找到方法后，输出方法的代码片段。
 */
public class MethodLocator {

    public static void main(String[] args) throws Exception {
        // 创建AST解析器
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        // 假设你的Java项目路径是 "path_to_your_project"
        String projectPath = "src/main/java";  // 更改为你的项目路径
        // 创建文件对象并递归遍历所有 .java 文件
        File projectDir = new File(projectPath);
        File[] files = listJavaFiles(projectDir);

        // 遍历项目中的所有Java文件
        for (File file : files) {
            if (file.getName().endsWith(".java")) {
                String sourceCode = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                parser.setSource(sourceCode.toCharArray());
                CompilationUnit cu = (CompilationUnit) parser.createAST(null);

                // 遍历AST查找方法
                cu.accept(new ASTVisitor() {
                    @Override
                    public boolean visit(MethodDeclaration node) {
                        // 检查方法名称
                        if ("divide".equals(node.getName().toString())) {
                            // 获取方法所在的行号
                            int startLine = cu.getLineNumber(node.getStartPosition());
                            int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
                            System.out.println("Method '" + node.getName() + "' found in " + file.getName() + " at lines " + startLine + "-" + endLine);

                            // 输出方法的代码片段
                            String methodCode = sourceCode.substring(node.getStartPosition(), node.getStartPosition() + node.getLength());
                            System.out.println("Method code:");
                            System.out.println(methodCode);
                        }
                        return super.visit(node);
                    }
                });
            }
        }
    }

    // 递归方法来列出所有 .java 文件
    public static File[] listJavaFiles(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return new File[0];
        }

        // 使用 ArrayList 来保存结果
        ArrayList<File> javaFiles = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                // 递归调用子目录
                File[] subDirFiles = listJavaFiles(file);
                for (File subFile : subDirFiles) {
                    javaFiles.add(subFile);
                }
            } else if (file.getName().endsWith(".java")) {
                // 如果是 .java 文件，则添加到列表
                javaFiles.add(file);
            }
        }

        // 返回文件列表
        return javaFiles.toArray(new File[0]);
    }
}
