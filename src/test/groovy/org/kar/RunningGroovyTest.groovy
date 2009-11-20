package org.kar

class RunningGroovyTest extends GroovyTestCase
{
    def static groovyScriptOne = "src/test/resources/TestScriptOne.groovy"
    def static groovyClassOne = 'src/test/resources/TestClassOne.groovy'
    def static javaFile = 'src/test/resources/TestJavaOne.java'
    def static args = ['Hello', 'World'].asImmutable()

    /*****************************************************************************************************************/
    /* Tests against compiled code. */

    /**
     * Test that a compiled script can expose data in the Binding.
     */
    void testCompiledGroovyScript()
    {
        Binding binding = createBinding()
        new TestScriptTwo(binding).run()
        assertBinding(binding)
    }

    /**
     * Test that a compiled groovy class can expose data in the Binding. Script behaviour is simulated by
     * implementing a 'run' method and supplying a constructor with a Binding param.
     */
    void testCompiledGroovyClass()
    {
        Binding binding = createBinding()
        new TestClassTwo(binding).run()
        assertBinding(binding)        
    }

    /*****************************************************************************************************************/
    /* Tests against uncompiled Groovy scripts.   */

    /**
     * Test dynamic execution of a Groovy script using GroovyShell that exposes data in the shared Binding.
     */
    void testGroovyShell()
    {
        Binding binding = createBinding()
        def shell = new GroovyShell(binding)
        shell.evaluate(new File(groovyScriptOne))
        assertBinding(binding)
    }

    /**
     * Test dynamic execution of a Groovy script passing parameters on the command line.
     */
    void testGroovyCall()
    {
        def proc = """groovy $groovyScriptOne Hello World""".execute()
        proc.waitFor()
        def result = proc.text.split()
        assert result[0] == 'Hello'
        assert result[1] == 'World'
    }

    /**
     * Test dynamic execution of a Groovy script using GroovyScriptEngine that exposes data in the shared Binding.
     */
    void testGroovyScriptEngine()
    {
        Binding binding = createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        gse.run(groovyScriptOne, binding)
        assertBinding(binding)
    }

    /**
     * Test dynamic execution of a Groovy script using GroovyClassLoader that exposes data in the shared Binding.
     */
    void testGroovyClassLoader()
    {
        useClassLoader(groovyScriptOne)
    }

    /*****************************************************************************************************************/
    /* Tests against uncompiled Groovy classes. */
    
    /**
     * Test dynamic execution of a Groovy class using GroovyShell. Calls the 'main' method and we redirect
     * System.out temporarily to capture the output.
     */
    void testGroovyShellWithClass()
    {
        def buf = new ByteArrayOutputStream()
        def newOut = new PrintStream(buf)
        def saveOut = System.out
        System.out = newOut
        def shell = new GroovyShell()
        shell.run(new File(groovyClassOne),  new ArrayList(args) as String[] )
        System.out = saveOut
        def result = buf.toString().split()
        assert result[0] == args[0]
        assert result[1] == args[1]
    }

    /**
     * Test dynamic execution of a Groovy class passing parameters on the command line.
     */
    void testGroovyCallWithClass()
    {
        def proc = """groovy $groovyClassOne Hello World""".execute()
        proc.waitFor()
        def result = proc.text.split()
        assert result[0] == 'Hello'
        assert result[1] == 'World'
    }

    /**
     * BROKEN
     */
    void testGroovyScriptEngineWithClass()
    {
        Binding binding = createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        println "testGroovyScriptEngineWithClass = "+gse.run(groovyClassOne, binding)
        println binding.variables

    }

    /**
     * Test dynamic execution of a Groovy class using GroovyClassLoader that exposes data in the shared Binding.
     */
    void testGroovyClassLoaderWithClass()
    {
        useClassLoader(groovyClassOne)
    }


    /*****************************************************************************************************************/
    /* Tests against uncompiled Java classes.*/

    /**
     *
     */
    void testGroovyShellOnJava()
    {
        Binding binding = createBinding()
        def shell = new GroovyShell(binding)
        shell.evaluate(new File(javaFile))
        assert binding.getVariables().size() == 2
    }

    /**
     *
     */
    void testGroovyScriptEngineOnJava()
    {
        Binding binding = createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        gse.run(javaFile, binding)
        assert binding.getVariables().size() == 2
    }

    /**
     * 
     */
    void testGroovyClassLoaderOnJava()
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class javaClass = loader.parseClass(new File(javaFile));

        def groovyObject = javaClass.newInstance();
        def binding = createBinding()
        groovyObject.binding = binding
        groovyObject.args = new ArrayList(args)
        groovyObject.invokeMethod("main", new ArrayList(args) as String[]);
        groovyObject.invokeMethod("run", null);
        assertBinding(binding)
    }

    /**
     * Confirm that executing a Java file using the Groovy command line is not allowed.
     */
    void testGroovyJavaCall()
    {
        def proc = """groovy $javaFile Hello World""".execute()
        proc.waitFor()
        assertTrue(proc.text.contains('cannot compile file with .java extension'))
    }

    /**
     * Parse, instantiate and run the Class parsed from fileName
     * @param fileName  the fileName to parse and run
     */
    private def useClassLoader(fileName)
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File(fileName));

        Binding binding = createBinding()
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        groovyObject.binding = binding
        groovyObject.invokeMethod("run", null);
        assertBinding(binding)
    }

    /**
     * Create a Binding with a single parameter to be passed to scripts and an 'out' Writer to redirect console output.
     */
    private Binding createBinding()
    {
        Binding binding = new Binding()
        def sWriter = new StringWriter()
        def pWriter = new PrintWriter(sWriter)
        binding.setVariable ('args', new ArrayList(args))
        binding.setVariable ('out', pWriter)
        return binding
    }

    /**
     * Assert that the expected 'common' actions are done with the Binding by each of the use cases.
     * The original 'args' should be as expected.
     * A copy of 'args' should have been placed in the Binding during execution.
     * The 'result' should be the concatentation of 'args' separated by spaces.
     */
    private def assertBinding(Binding binding)
    {
        assert binding.variables.size() == 4
        assert binding.variables.args.value[0].toString() == args[0]
        assert binding.variables.args.value[1].toString() == args[1]
        assert binding.variables.result.value.toString() == args.join(' ')
        assert binding.variables.myArgs.value[0].toString() == args[0]
        assert binding.variables.myArgs.value[1].toString() == args[1]
    }
}
