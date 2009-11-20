package org.kar

class RunningGroovyTest extends GroovyTestCase
{
    def static groovyScriptOne = "src/test/resources/TestScriptOne.groovy"
    def static groovyClassOne = 'src/test/resources/TestClassOne.groovy'
    def static javaFileOne = 'src/test/resources/TestJavaOne.java'
    def static javaFileTwo = 'src/test/resources/TestJavaTwo.java'
    def static args = ['Hello', 'World'].asImmutable()
    def helper = new RunningGroovyTestHelper()

    /*****************************************************************************************************************/
    /* Tests against uncompiled Groovy scripts.   */

    /**
     * Test dynamic execution of a Groovy script using GroovyShell that exposes data in the shared Binding.
     */
    void testGroovyShell()
    {
        Binding binding = helper.createBinding()
        def shell = new GroovyShell(binding)
        shell.evaluate(new File(groovyScriptOne))
        helper.assertBinding(binding)
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
        Binding binding = helper.createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        gse.run(groovyScriptOne, binding)
        helper.assertBinding(binding)
    }

    /**
     * Test dynamic execution of a Groovy script using GroovyClassLoader that exposes data in the shared Binding.
     */
    void testGroovyClassLoader()
    {
        helper.useClassLoader(groovyScriptOne)
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
        Binding binding = helper.createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        println "testGroovyScriptEngineWithClass = "+gse.run(groovyClassOne, binding)
        println binding.variables

    }

    /**
     * Test dynamic execution of a Groovy class using GroovyClassLoader that exposes data in the shared Binding.
     */
    void testGroovyClassLoaderWithClass()
    {
        helper.useClassLoader(groovyClassOne)
    }


    /*****************************************************************************************************************/
    /* Tests against uncompiled Java classes.*/

    /**
     * Can call the java file, but cannot pass in any parameters to the main method. 
     */
    void testGroovyShellOnJava()
    {
        Binding binding = helper.createBinding()
        def shell = new GroovyShell(binding)
        shell.evaluate(new File(javaFileOne))
        assert binding.getVariables().size() == 2
    }

    /**
     * Can call the java file, but cannot pass in any parameters to the main method.
     */
    void testGroovyScriptEngineOnJava()
    {
        Binding binding = helper.createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        gse.run(javaFileOne, binding)
        assert binding.getVariables().size() == 2
    }

    /**
     * Dynamically compile, instantiate, inspect and call methods on a POJO.
     */
    void testGroovyClassLoaderOnJava()
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class javaClass = loader.parseClass(new File(javaFileOne));

        def groovyObject = javaClass.newInstance();
        def binding = helper.createBinding()
        groovyObject.binding = binding
        if(groovyObject.metaClass.respondsTo(groovyObject, 'run'))
        {
            groovyObject.invokeMethod("run", null);
            helper.assertBinding(binding)
        }
        if(groovyObject.metaClass.respondsTo(groovyObject, 'main'))
        {
            groovyObject.invokeMethod("main", new ArrayList(args) as String[]);
        }
    }

    /**
     * Confirm that executing a Java file using the Groovy command line is not allowed.
     */
    void testGroovyJavaCall()
    {
        def proc = """groovy $javaFileOne Hello World""".execute()
        proc.waitFor()
        assertTrue(proc.text.contains('cannot compile file with .java extension'))
    }



    /**
    * If the Java file extends Script, all is good in the world.
    */
    void testGroovyJavaExtendsScript()
    {
        Binding binding = helper.createBinding()
        def shell = new GroovyShell(binding)
        shell.evaluate(new File(javaFileTwo))
        helper.assertBinding(binding)
    }

    /*****************************************************************************************************************/
    /* Tests against compiled code. */

    /**
     * Test that a compiled script can expose data in the Binding.
     */
    void testCompiledGroovyScript()
    {
        Binding binding = helper.createBinding()
        new TestScriptTwo(binding).run()
        helper.assertBinding(binding)
    }

    /**
     * Test that a compiled groovy class can expose data in the Binding. Script behaviour is simulated by
     * implementing a 'run' method and supplying a constructor with a Binding param.
     */
    void testCompiledGroovyClass()
    {
        Binding binding = helper.createBinding()
        new TestClassTwo(binding).run()
        helper.assertBinding(binding)
    }

    /**
     * Test that a compiled groovy class extending Script is a nice marriage of both worlds.
     */
    void testCompiledGroovyClassExtendsScript()
    {
        Binding binding = helper.createBinding()
        new TestClassThree(binding:binding).run()
        helper.assertBinding(binding)
    }

}
