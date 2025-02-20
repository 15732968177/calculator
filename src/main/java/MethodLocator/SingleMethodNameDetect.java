package MethodLocator;
import org.eclipse.jdt.core.dom.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * 功能：
 * 如果测试文件中含有多个单元测试，而我只想确定其中的一个单元测试涉及到的源代码方法名称
 *
 * 过程：
 *要在一个测试文件中只确定某个特定单元测试方法涉及到的源代码方法名称，你需要做以下几步：
 *
 * 定位特定的测试方法：首先定位到你感兴趣的单元测试方法（如 testDivide）。
 * 解析并遍历该方法的 AST：只解析该测试方法内的代码，而不是整个文件。你可以通过遍历 MethodDeclaration 节点来找到目标测试方法。
 * 查找该方法内的 MethodInvocation：在目标方法内部查找所有的 MethodInvocation 节点，这些节点代表了对其他源代码方法的调用。
 */
public class SingleMethodNameDetect {
    public static void main(String[] args) throws Exception {
        // 创建 AST 解析器
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        // 假设你的测试类路径是 "src/test/java/CalculatorTest.java"
        String testFilePath = "src/test/java/CalculatorTest.java";  // 更改为你的测试类路径
        String sourceCode = new String(Files.readAllBytes(Paths.get(testFilePath)), StandardCharsets.UTF_8);
        parser.setSource(sourceCode.toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // 遍历 AST 查找目标测试方法（比如 testDivide）
        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                // 如果是目标测试方法，比如 testDivide
                if ("testTargetMethod".equals(node.getName().toString())) {
                    System.out.println("Found test method: " + node.getName());

                    // 遍历该方法内的 AST，查找方法调用
                    node.accept(new ASTVisitor() {
                        @Override
                        public boolean visit(MethodInvocation methodNode) {
                            // 获取被调用的源代码方法名
                            String methodName = methodNode.getName().toString();
                            String className = "";

                            // 获取方法调用的目标对象（可能是实例方法调用）
                            if (methodNode.getExpression() != null) {
                                className = methodNode.getExpression().toString();  // 获取对象的类名（如 calculator）
                            }

                            System.out.println("Method '" + methodName + "' is called in " + className);
                            return super.visit(methodNode);
                        }
                    });
                }
                return super.visit(node);
            }
        });
    }
}
