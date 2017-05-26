Javac是什么
------------
Javac是一种编译器，编译器是将便于人理解的语言规范转化成机器容易理解的语言规范，如C、C++和汇编语言都是将源码直接编译成目标机器代码，这个目标机器代码是CPU直接执行的指令集合。

Javac的任务是将Java源代码语言转化成JVM能够识别的语言，然后由JVM将JVM语言再转化成当前这个机器能够识别的机器语言。

Javac编译器的基本结构
-------------

* 词法分析：读取源代码，将源文件的字符流转变为对应的Token流
* 语法分析：检查Token流中的关键字组合在一起是不是符合Java语言规范。语法分析的结果就是形成一个符合Java语言规范的抽象语法树。
* 语义分析：将复杂的语法转化成最简单的语法，形成一个注解过后的抽象语法树。
* 最后，根据根据注解的抽象语法树生成字节码。

Javac工作原理分析
--------------

词法分析过程是在JavacParser的parseCompilationUnit方法中完成的：

```java
public JCTree.JCCompilationUnit parseCompilationUnit(){
	int pos = S.pos();
	JCExpression pid = null;
	String dc = S.docComment();
	JCModifiers mods = null;
	List<JCAnnotation> packageAnnotations = List.null();
	if (S.token() == MONKEYS_AT) 
		mods == modifiersOpt(); //解析修饰符
	if (S.token() == PACKAGE) { //解析package声明
		if (mods == null) {
			checkNoMods(mods.flags);
			packageAnnotations = mods.annotations;
			mods == null;	
		}
		S.nextToken();
		pid = qualident();
		accept(SEMI);
	}
	ListBuffer<JCTree> defs = new ListBuffer<JCTree>();
	boolean checkForImports = true;
	while(S.token() != EOF){
		if (S.pos() <= errorEndPos) {
			//跳过错误字符
			skip(ckeckForImports, false, false, false);
			if (S.token == EOF)
				break;
		}
		if (checkForImports && mods == null && S.token() == IMPORT) {
			defs.append(importDeclaration());//解析import声明
		}else{
			JCTree def = typeDeclaration(mods);
			if (keepDocComments && dc != null && docComment.get(def) == dc) {
				dc == null;//如果前面的类型声明中已经解析过了，那么top level中将不再重复解析
			}
			if (def instanceof JCExpressionStatement)
				def = ((JCExpressionStatement)def).expr;
			defs.append(def);
			if (def instanceof JCClassDecl)
				checkForImports = false;
			mods == null;
		}
	}
	JCTree.JCCompilationUnit toplevel = F.at(pos).Toplevel(packageAnnotations, pid, defs.toList());
	attach(toplevel, dc);
	if (defs.elems.isEmpty)
		storeEnd(toplevel, S.prevEndPos());
	if (keepDocComments)
		toplevel.docComments = docComments;
	if (keepLineMap)
		toplevel.lineMap = S.getLineMap();
	return toplevel;
}
```

语法分析器
------
Package节点解析完后进入while循环，首先解析importDeclaration：首先检查Token是不是Token.IMPORT，如果是，用import的语法规则来解析import节点，最后构造一个import语法树：
```java
JCTree importDeclaration(){
	int pos = S.pos();
	S.nextToken();
	boolean importStatic = false;
	if(S.token() == STATIC){//如果有static设置这个import是静态类引入
		ckeckStaticImports();
		importStatic = true;
		S.nextToken();
	}
	JCExpression pid = toP(F.at(S.pos()).Ident(ident()));
	do{
		int posl = S.pos();
		accept(DOT);
		if (S.token() == STAR) {//如果最后一个Token是"*"
			pid = to(F.at(posl).Select(pid, names.asterisk));
			S.nextToken();
			break;
		}else{
			pid = toP(F.at(posl).Select(pid, ident));
		}
	}while (S.token() == DOT);
	accept(SEMI);
	return toP(F.at(pos).Import(pid, importStatic));
}
```
class如何解析成一颗语法树：
```java
JCClassDecl classDeclaration(JCModifiers mods, String dc){
	int pos = S.pos();
	accept(CLASS);
	Name name = ident();
	List<JCTypeParameter> typarams = typeParametersOpt();
	JCTree extending = null;
	if (S.token() == EXTENDS) {
		S.nextToken();
		extending = parseType();
	}
	List<JCExpression> implementing = List.nil();
	if (S.token() == IMPLEMENTS) {
		S.nextToken();
		implementing = typeList();
	}
	List<JCTree> defs = classOrInterfaceBody(name, false);
	JCClassDecl result = toP(F.at(pos).ClassDef(
		mods, name, typarams, extending, implementing, defs));
	attach(result, dc);
	return result;
}
```