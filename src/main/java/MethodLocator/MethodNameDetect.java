package MethodLocator;
import org.eclipse.jdt.core.dom.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 *  总结：
 * 查找测试类中的方法调用：使用 MethodInvocation 节点来查找测试方法中对源代码方法的调用。
 * 提取类名和方法名：从 MethodInvocation 中可以获取被调用的类名和方法名，进一步确定哪个方法被测试。
 * 获取方法参数：通过访问 arguments()，你还可以获得调用方法时传递的参数。
 *
 * 功能：
 * 能确定一个测试文件中所涉及到的所有源代码的方法名和类
 *
 * 过程：
 * 解析单元测试文件的 AST：通过解析单元测试类的 AST，你可以获取测试方法以及它们所调用的源代码方法的信息。
 * 分析测试方法中的调用：在测试方法中，通常会通过 assert 或直接调用被测试的方法。你可以通过 AST 查找这些方法调用节点，从而确定测试的是哪一个源代码方法。
 * 获取类名和方法名：通过 AST，可以提取出类名和方法名等信息。
 */
public class MethodNameDetect {

    public static void main(String[] args) throws Exception {
        // 创建 AST 解析器
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        // 假设你的单元测试类路径是 "src/test/java/CalculatorTest.java"
        String testFilePath = "src/test/java/CalculatorTest.java";  // 更改为你的测试类路径
        String sourceCode = new String(Files.readAllBytes(Paths.get(testFilePath)), StandardCharsets.UTF_8);
        parser.setSource(sourceCode.toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // 遍历 AST 查找测试方法中的方法调用
        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodInvocation node) {
                // 获取被调用方法的信息
                String methodName = node.getName().toString();
                String className = "";

                // 获取方法调用的目标对象（可能是实例方法调用）
                if (node.getExpression() != null) {
                    className = node.getExpression().toString();  // 获取对象的类名（如 calculator）
                }

                // 如果是单元测试中的方法调用
                System.out.println("Method '" + methodName + "' is being tested in class '" + className + "'");

//                // 如果需要还可以获取方法的参数信息
//                List<?> arguments = node.arguments();
//                for (Object arg : arguments) {
//                    System.out.println("Argument: " + arg);
//                }

                return super.visit(node);
            }

            @Override
            public boolean visit(MethodDeclaration node) {
                // 你可以额外获取测试方法的信息
                String methodName = node.getName().toString();
                System.out.println("Found test method: " + methodName);
                return super.visit(node);
            }
        });
    }
}
