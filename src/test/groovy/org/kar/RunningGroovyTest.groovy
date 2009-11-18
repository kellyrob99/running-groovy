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
        def shell = new GroovyScriptEngine(new File('.').toURL())
        shell.run(groovyScriptOne, binding)
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
        def shell = new GroovyScriptEngine(new File('.').toURL())
        println "testGroovyScriptEngineWithClass = "+shell.run(groovyClassOne, binding)
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
        assert binding.getVariables().size() == 1
    }

    void testGroovyScriptEngineOnJava()
    {
        Binding binding = createBinding()
        def shell = new GroovyScriptEngine(new File('.').toURL())
        shell.run(javaFile, '')
        assert binding.getVariables().size() == 1
    }

    void testGroovyClassLoaderOnJava()
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class javaClass = loader.parseClass(new File(javaFile));

        def groovyObject = javaClass.newInstance();
        groovyObject.invokeMethod("main", new ArrayList(args) as String[]);
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

    private Binding createBinding()
    {
        Binding binding = new Binding()
        binding.setVariable ('args', args)
        return binding
    }

    private def assertBinding(Binding binding)
    {
        assert binding.variables.size() == 3
        assert binding.variables.args.value[0].toString() == args[0]
        assert binding.variables.args.value[1].toString() == args[1]
        assert binding.variables.result.value.toString() == args.join(' ')
        assert binding.variables.myArgs.value[0].toString() == args[0]
        assert binding.variables.myArgs.value[1].toString() == args[1]
    }
}
